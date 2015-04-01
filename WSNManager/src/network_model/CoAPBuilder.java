package network_model;

import stack.CoapMessage;
import stack.IPHC_Data;
import stack.UDP_Datagram;

/**
 * Class to abstract the details of sending a CoAP message from the pc to the motes
 *
 */
public class CoAPBuilder{
	
	private NetworkModel _model;

	public CoAPBuilder(NetworkModel model) {
		_model = model;
	}

	//tk
	public byte[] getSerialPacket(String method, String URIPath, String dest64, byte[] payload){
		CoapMessage m = new CoapMessage(method,URIPath, dest64,payload);
		
		//m.printRaw(m.getMessage());
		UDP_Datagram d = new UDP_Datagram(m);
		//d.printRaw(d.getMessage());
		//source routing
		
		IPHC_Data hc = new IPHC_Data(d);
		//hc.printRaw(hc.getMessage());
		
		//now we have iphc all we need to do is append 64 bit address and send
		byte[] hcArr = hc.getMessage();
		byte[] dest = hc.destAddr64;
		byte[] finalMessage = new byte[hcArr.length+9];
		finalMessage[0] = (byte) ("D".charAt(0)&0xFF);
		for(int i = 0;i<8;i++){
			finalMessage[i+1] = dest[i];
		}
    	for(int i = 0; i< hcArr.length;i++){
    		finalMessage[i+9] = hcArr[i];
    	}
    	return finalMessage;
	}
}
