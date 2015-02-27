package stack;

import java.util.ArrayList;
import java.util.IllegalFormatException;

import javax.xml.bind.DatatypeConverter;

import org.jnetpcap.packet.Payload;


public class CoapMessage extends ByteMessage {
	private byte _ver = 0b01000000; // version 1 always, bit 0 and 1
	private byte _type = 0b00000000;// 0 confirmable by default bits 2 and 3

	// using one byte tokens
	private byte _TKL = 0b00000001;// bits 4-7 token length

	// code is split into 3 bit class 5 bit detail aaa.bbbbb
	// for example GET is 0.01 or 0b000_00001
	private byte _Code = 0b00000000;// GET,PUT, ETC
	private static int _GLBmessageID = (int) Math.floor(Math.random() * 65535);

	private int _messageID;
	private int _token;

	private ArrayList<Byte> _options = new ArrayList<Byte>();
	private ArrayList<Byte> _payload = new ArrayList<Byte>();

	public byte[] _destIID64 = new byte[8];
	// For UDP

	public int sourcePort = 5683;// 2 bytes
	public int destPort = 5683;// default coap port

	private int lastOptionDelta = 0;

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

	}

	/**
	 * Debug CoapMessage only supported type is GET URIPath like "l" for led app
	 * in firware DESTIID like "14159200000d2616" one string all 8 bytes must be
	 * represented (0 as 00)
	 * 
	 * @param type
	 * @param URIPath
	 * @param DestIID
	 */
	public CoapMessage(String type, String URIPath, String DestIID) {

		_GLBmessageID = _GLBmessageID++ % 65535;

	
		_token = 1;
		_messageID = _GLBmessageID;

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
			// printRaw(_destIID64);
		} else {
			System.out.println("ERROR IN ADDR LENGTH");
		}

	}

	/**
	 * adds resource option
	 * 
	 * @param URIPath
	 */
	private void newURIPathOption(String URIPath) {
		// option delta = difference between this option code and last option
		// delta
		int optionDelta = Math.abs(lastOptionDelta - 11);
		lastOptionDelta = optionDelta;
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
	 * 
	 * @return entire CoAP header and payload as byte[]
	 */
	public byte[] getMessage() {
		byte[] b = new byte[4 + _TKL + _options.size() + _payload.size()];
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

		if (_payload.size() > 0) {
			index++;
			b[index] = (byte) 0b11111111;
		}
		for (int i = 0; i < _payload.size(); i++) {
			b[index] = _payload.get(i);
			index++;
		}
		return b;
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
 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |Ver| T |
 * TKL | Code | Message ID |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Token (if
 * any, TKL bytes) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Options
 * (if any) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |1 1 1 1 1
 * 1 1 1| Payload (if any) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
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
