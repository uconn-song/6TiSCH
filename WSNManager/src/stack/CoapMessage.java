package stack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;

import javax.xml.bind.DatatypeConverter;




public class CoapMessage extends ByteMessage {
	
	private static HashMap<String,String> COAP_CODES = new HashMap<String,String>();
	public static int CONTENT_FLAG_NEIGHBOR = 0x110;
	static{
		COAP_CODES.put("2.1", "Created");
		COAP_CODES.put("2.2", "Deleted");
		COAP_CODES.put("2.3", "Valid");
		COAP_CODES.put("2.4", "Changed");
		COAP_CODES.put("2.5", "Content");
		//error codes
		COAP_CODES.put("4.0", "Bad Request");
		COAP_CODES.put("4.1", "Unauthorized");
		COAP_CODES.put("4.2", "Bad Option");
		COAP_CODES.put("4.3", "Forbidden");
		COAP_CODES.put("4.4", "Not Found");
		COAP_CODES.put("4.5", "Method Not Allowed");
		COAP_CODES.put("4.6", "Not Acceptable");
		
		COAP_CODES.put("4.12", "Precondition Failed");
		COAP_CODES.put("4.13", "Request Entity Too Large");
		COAP_CODES.put("4.15", "Unsupported Content-Format");
		
		COAP_CODES.put("5.0", "Internal Server Error");
		COAP_CODES.put("5.1", "Not Implemented");
		COAP_CODES.put("5.2", "Bad Gateway");
		COAP_CODES.put("5.3", "Service Unavailable");
		COAP_CODES.put("5.4", "Gateway Timeout");
		COAP_CODES.put("5.5", "Proxying Not Supported");
	}
			
		
	
	private int _verMask =  0b11000000;
	private byte _ver =     0b01000000; // version 1 always, bit 0 and 1
	
	private int _typeMask = 0b00110000;
	private byte _type = 0b00000000;// 0 confirmable by default bits 2 and 3

	// using one byte tokens
	private int _TKLMask = 0b00001111;
	private int _TKL = 0b00000001;// bits 4-7 token length
	
	// code is split into 3 bit class 5 bit detail aaa.bbbbb
	// for example GET is 0.01 or 0b000_00001
	private byte _Code = 0b00000000;// GET,PUT, ETC
	private static int _GLBmessageID = (int) Math.floor(Math.random() * 65535);

	private int _messageID;
	private int _token;

	private ArrayList<Byte> _options = new ArrayList<Byte>();
	private byte[] _payload = new byte[0];
	
	
	//for lower layer
	public byte[] _destIID64 = new byte[8];
	
	// For UDP
	public int sourcePort = 5683;// 2 bytes
	public int destPort = 5683;// default coap port

	private int _lastOptionType=0;
	
	//The full CoAP header and payload
	private byte[] _message;
	
	
	/**
	 * Constructor for parsing CoAP Message received from motes
	 * @param toParse the byte array to parse
	 */
	public CoapMessage(byte[] toParse){
		//printRaw(toParse);
		
		parseCoAPMessage(toParse);
	}
	private void parseCoAPMessage(byte[] toParse) {
		
		byte b = toParse[0];
		//System.out.println(ByteMessage.byteToString(b) +" " + ((b&_verMask)>>6) + " " + ByteMessage.byteToString((byte) _verMask));
		if(((b&_verMask)>>6)!=1) throw new IllegalArgumentException("wrong CoAP version");
		_TKL = (byte) (b&_TKLMask)&0xFF;
		if(_TKL>1) throw new IllegalArgumentException("token length too long");
		
		//two part coap code determining message type
		byte code = toParse[1];
		int codeA = (code & 0b11100000)>>5;
		int codeB = code & 0b00011111;
		printRaw(toParse);
		if(codeA ==0){
			System.out.println("get post put implement in coapMessage parsing");
		}
		else if(codeA != 2) throw new IllegalArgumentException( "COAP ERROR : " + COAP_CODES.get(codeA+"."+codeB));
		
		int MessageID = toParse[2]<<8+toParse[3];
		//System.out.println(MessageID);
		if(_TKL==1){
		_token = toParse[4];
		}
		//if length = 5 then header and token is all this packet consists of
		if(toParse.length==4 || toParse.length==5) return;
		
		int payloadMarker = 5;
		int lastOptionCode = 0;
		boolean optionsFound=false;
		//check for options
		while(payloadMarker< toParse.length){
			if((toParse[payloadMarker]&0xFF)==0xFF){
				payloadMarker++;
				break;
			}
			optionsFound=true;	
			payloadMarker++;
		}
		if(optionsFound) throw new IllegalArgumentException("Options should not be added to messages to manager");
		//if payload size > 0
		if(toParse.length-1 >= payloadMarker){
			_payload = new byte[toParse.length-payloadMarker];
			for(int i  = 0; i< toParse.length-payloadMarker;i++){
				_payload[i] = toParse[i+payloadMarker];
			}
		}
		
		
		
	/*
		if(_payload.length>0){
			System.out.println("CoAP Payload: [Binary:{");
			this.printRaw(_payload);
			System.out.println("}\nASCII: {"+ getPayloadAsAscii()+ "} \n] ");
		}
		
		*/
	}
	
	/**
	 * Constructors for building CoAP Messages
	 * @param type
	 * @param coapURI
	 */

	public CoapMessage(String type, String coapURI) {
		_GLBmessageID = _GLBmessageID++ % 65535;
		_token = 1;
		_messageID = _GLBmessageID;

		switch (type) {
		case "GET":
			_Code = 0b000_00001;
			break;
		default:
			System.out.println(type + " not implemented, message not parsed");
			return;
		}
		parseURI(coapURI);
		buildMessage();
	}

	

	/**
	 * Debug CoapMessage only supported type is GET URIPath like "l" for led app
	 * in firware DESTIID like "14159200000d2616" one string all 8 bytes must be
	 * represented (0 as 00)
	 * 
	 * @param type (GET/PUT/ETC)
	 * @param URIPath
	 * @param DestIID 64B address in hex string format (pad bytes to be 2 characters each so 0->00)
	 * @param payload payload for the CoAP message must be under 46? bytes
	 */
	public CoapMessage(String type, String URIPath, String DestIID, byte[] payload) {

		_GLBmessageID = _GLBmessageID++ % 65535;
		_token = 1;
		_messageID = _GLBmessageID; //make sure that acknowledgments have the same message id on the way back
		switch (type) {
		case "GET":
			_Code = 0b000_00001;
			break;
		default:
			throw new IllegalArgumentException(type
					+ " not implemented, message not parsed");
		}
		newURIPathOption(URIPath);
		if (DestIID.length() == 16) {
			// try to parse address
			_destIID64 = DatatypeConverter.parseHexBinary(DestIID);
		} else {
			System.out.println("ERROR IN ADDR LENGTH");
		}
		
		buildMessage();

	}

	/**
	 * adds resource option
	 * @param URIPath
	 */
	private void newURIPathOption(String URIPath) {
		// option delta = difference between this option code and last option
		// delta
		int optionDelta = Math.abs(_lastOptionType - 11);
		_lastOptionType = 11;
		int optionLength = URIPath.length();
		_options.add((byte) ((optionDelta << 4 ^ optionLength) & 0xFF));
		for (int i = 0; i < URIPath.length(); i++) {
			_options.add((byte) (URIPath.charAt(i) & 0xFF));
		}

	}

	/**
	 * TODO: UNFINISHED
	 * 
	 * @param coapURI
	 */
	private void parseURI(String coapURI) {
		if (coapURI.startsWith("coap://[")) {
		}
		throw new IllegalArgumentException(coapURI);

	}

	/** 
	 * @return entire CoAP header and payload as byte[]
	 */
	public byte[] getMessage() {
		return _message;
	}
	
	
	/**
	 * build CoAP message from information provided
	 */
	private void buildMessage() {
		byte[] b = new byte[4 + _TKL + _options.size() + _payload.length];
		b[0] = (byte) (_ver ^ _type ^ _TKL);
		b[1] = _Code;
		b[2] = (byte) ((_messageID >> 8) & 0xFF);// first byte of message id
		b[3] = (byte) (_messageID & 0xFF);// second byte of message id
		b[4] = (byte) (_token & 0xFF);
		int index = 5;
		for (int i = 0; i < _options.size(); i++) {
			b[index] = _options.get(i);
			index++;
		}
		
		//check if payload is empty, add bytes if not
		if (_payload.length > 0) {
			index++;
			b[index] = (byte) 0b11111111;
			//add payload bytes to the message
			for (int i = 0; i < _payload.length; i++) {
				b[index] = _payload[i];
				index++;
			}
		}
		_message = b;
	}

	// Debug Functions
	public void printRaw(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			if (i > 0 && i % 4 == 0.0) {
				System.out.println();
			}
			System.out.print(byteToString(b[i]) + " ");
			// every 4 bytes new line
		}
		System.out.println();
	}
	
	public String getPayloadAsAscii(){
		    StringBuilder sb = new StringBuilder(_payload.length);
		    for (int i = 0; i < _payload.length; ++ i) {
		        sb.append((char) _payload[i]);
		    }
		    return sb.toString();
	}
	public byte[] getPayload() {
		return _payload;
	}

	// PAYLOAD
	/*
	 * //options? COAP_OPTION_NUM_URIPATH = 11,
	 * 
	 * coap://[{MOTEIP]/l //coap://[aaaa::1]:1234/test1/test2"
	 * 
	 * // port WKP_UDP_COAP = 5683, //open coap
	 * https://github.com/openwsn-berkeley
	 * /openwsn-fw/blob/53974cf7d7b7ce3d8451e3b154445a14b75394b3
	 * /openstack/04-TRAN/opencoap.c
	 */
}

/*
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * 
 * 
 * 
 * 
 *  0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |Ver| T |  TKL  |      Code     |          Message ID           |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |   Token (if any, TKL bytes) ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |   Options (if any) ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |1 1 1 1 1 1 1 1|    Payload (if any) ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * +------+--------+-----------+ | Code | Name | Reference |
 * +------+--------+-----------+ | 0.01 | GET | [RFC7252] | | 0.02 | POST |
 * [RFC7252] | | 0.03 | PUT | [RFC7252] | | 0.04 | DELETE | [RFC7252] |
 * +------+--------+-----------+
 * 
 * 
 * 
 * 
 * 0 1 2 3 4 5 6 7 +---------------+---------------+ | | | | Option Delta |
 * Option Length | 1 byte | | | +---------------+---------------+ \ \ / Option
 * Delta / 0-2 bytes \ (extended) \ +-------------------------------+ \ \ /
 * Option Length / 0-2 bytes \ (extended) \ +-------------------------------+ \
 * \ / / \ \ / Option Value / 0 or more bytes \ \ / / \ \
 * +-------------------------------+
 * 
 * 
 * option delta is the difference between this option's option code and the last
 * code
 */
