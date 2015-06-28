package network_model;

import java.util.List;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;

import graphStream.NetworkGraph;
import stack.CoapMessage;
import stack.IPHC_Data;
import stack.SourceRoutingHeader;
import stack.UDP_Datagram;

/**
 * Class to abstract the details of sending a CoAP message from the pc to the motes
 *
 */
public class CoAPBuilder{
	
	private NetworkModel _model;
	private byte[] _packetBytes;
	private int _messageID;
	public CoAPBuilder(NetworkGraph sourceRouteGraph, NetworkModel model,String method, String URIPath, String dest64, byte[] payload, int ackMessageID) {
		_model = model;
		CoapMessage m;
		 m = new CoapMessage(method,URIPath, dest64,payload,ackMessageID);
		_messageID = m.getMessageID();
		
		//m.printRaw(m.getMessage());
		UDP_Datagram udpHeader = new UDP_Datagram(m);
		IPHC_Data ipv6Header;
		//d.printRaw(d.getMessage());
		//check if source routing is necessary
		SourceRoutingHeader h = null;
		boolean directNeighbor;
		try{
		 directNeighbor = _model.getRootMote().getNeighborTable().containsKey(dest64);
		}catch(NullPointerException e){
			return;
		}
		
		if( !directNeighbor){
			//TODO: implement source routing header
		
			 h = new SourceRoutingHeader(_model,sourceRouteGraph, dest64);
			//DAM should be destination mode elided if source routing is present.
			//SAM should be all 16 bytes carried in line.
			//change destination address to be the first hop, append udp header with source routing header
			byte[] udpbytes= udpHeader.getMessage();
			byte[] srcRouteBytes = h.getHeaderBytes();
			byte[] firstHopAddr = h.getFirstHopBytes();
			
			byte[] combinedHeaders = new byte[udpbytes.length+srcRouteBytes.length];
			
			//src route header should be followed by udp header and CoAP message
			int index = 0;
			for(int i =0;i<srcRouteBytes.length;i++){
				combinedHeaders[i] = srcRouteBytes[i];
				index++;
			}

			for(int i = 0 ; i<udpbytes.length;i++){
				combinedHeaders[index]=udpbytes[i];
				index++;
			}
			
			
			ipv6Header = new IPHC_Data(firstHopAddr,combinedHeaders,true);
		}else{
			System.out.println("direct neighbor");
			//no source routing
			ipv6Header = new IPHC_Data(udpHeader._destAddr64,udpHeader.getMessage(),false);
		}
	
		
		
		//now we have iphc all we need to do is append 64 bit address and send to serial processing
		byte[] headerBytes = ipv6Header.getMessage();
		byte[] dest = ipv6Header._destAddr64;
		
		//TODO:REMOVE FOR NON SOURCE ROUTE
		if(!directNeighbor){
			dest = h.getFirstHopBytes();
			
		}
		byte[] finalMessage = new byte[headerBytes.length+9];
		finalMessage[0] = (byte) ("D".charAt(0)&0xFF);
		//prepend the iphc header by nextOrPreviousHop, in this case next hop
		for(int i = 0;i<8;i++){
			finalMessage[i+1] = dest[i];
		}
    	for(int i = 0; i< headerBytes.length;i++){
    		finalMessage[i+9] = headerBytes[i];
    	}
    	
    	_packetBytes = finalMessage;
    	
	}

	
	public int getMessageID(){
		return _messageID;
	}
	
	
	/**
	 * @return byte message which can be sent using WSNManager.send() method
	 */
	public byte[] getSerialPacket(){

		printEntirePacketBinary();
		return _packetBytes;
		
	}
	
	
	public void printEntirePacketBinary(){
		System.out.println("\nbegin packet:\n");
		
		printRaw(_packetBytes);
		System.out.println("\nendPacket\n");
		
		
	}
	

	public void printRaw(byte[] message){
		for(int i = 0 ; i < message.length;i++){
			if(i>0 && i%4==0.0)
			{
				System.out.println();
			}
			System.out.print(byteToString(message[i]) + " "  );
			//every 4 bytes new line
			
		}
		System.out.println();
	}

	public static String byteToString(byte b){
		return String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ', '0');
	}
}
