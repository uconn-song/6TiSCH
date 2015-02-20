package jNetPcap;

import java.util.ArrayList;

import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip6;
//http://jnetpcap.com/?q=node/187
public class JPacketTest {
	public JPacketTest(ArrayList<Byte> b){
		byte[] b2 = new byte[b.size()];
		for(int i = 0 ; i < b2.length;i++){
			b2[i]=b.get(i);
		}
		
		JPacket tmpP= new JMemoryPacket(Ip6.ID, b2); // Will scan as well 
		System.out.println("Packet : \n" + tmpP.toHexdump());  
		System.out.println("Decoded : \n" + tmpP.toString());
	}
	
	public JPacketTest(byte[] b){
		
		
		JPacket tmpP= new JMemoryPacket(Ip6.ID, b); // Will scan as well 
		System.out.println("Packet : \n" + tmpP.toHexdump());  
		System.out.println("Decoded : \n" + tmpP.toString());
	}
}
