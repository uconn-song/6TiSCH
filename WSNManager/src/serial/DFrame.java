package serial;

import jNetPcap.JPacketTest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import packet.PacketAnalyzer;
import packet.IPUtils;
import packet.IPHCPacketAnalyzer;
import packet.PacketAnalyzer.Packet;
public class DFrame extends Frame {

		//This frame is used by the mote to indicate to the computer it is ready to receive data.
		private String _address="";
		private byte[] ipv6data;
		public DFrame(ArrayList<Byte> data) {
			setType("Data");
			setData(data);
			data.get(0);//"D"
			_address =  Integer.toHexString(data.get(2)&0xFF)+ Integer.toHexString(data.get(1)&0xFF);//address part 2
			data.get(3);//asn 0 
			data.get(4);//asn 1
			data.get(5);//asn 2
			data.get(6);//asn 3
			data.get(7);//asn 4
			ipv6data = new byte[data.size()-10];
			
			byte[] llreceiver = new byte[8];
			byte[] llsender = new byte[8];
			//fill in destination address
			for(int i = 8; i < 16;i++){
				llreceiver[i-8] = _data.get(i);
			}
			//fill in source address
			for(int i =16; i<24;i++){
				llsender[i-16] = _data.get(i);
			}
			
			for(int i = 24 ; i < data.size()-2;i++)
			{
				ipv6data[i-24]=data.get(i);
				//System.out.print("Data: " + byteToString(data.get(i))+"(0x"+Integer.toHexString(data.get(i)&0xFF)+") ");
			}
			//System.out.println();
			//HDLC.crcValidate(data);
			Packet p = new Packet(ipv6data, PacketAnalyzer.NETWORK_LEVEL);
			
			StringBuilder brief = new StringBuilder();
			StringBuilder verbose = new StringBuilder();
			IPHCPacketAnalyzer anal = new IPHCPacketAnalyzer();
			p.llsender = llsender;
			p.llreceiver=llreceiver;
			anal.analyzePacket(p, brief, verbose);
			System.out.println(verbose.toString());
			
			//new JPacketTest(ipv6data);

		}
		
		
		@Override
		public String toString(){
			String toret= _address + " sent: ";
			
			if(_data.size()<15){
			for(int i = 8 ; i < _data.size()-2;i++)
			{
				toret = toret+" " + byteToString(_data.get(i)) + "("+(char)(_data.get(i)&0xFF)+")" ;
			}
			}else{
				for(int i = 8 ; i < _data.size()-2;i++)
				{
					toret = toret+" " + byteToString(_data.get(i));
				}
				
				toret = toret+"\n";
				for(int i = 8 ; i < _data.size()-2;i++)
				{
					toret = toret+" " + Integer.toHexString(_data.get(i)&0xFF);
				}
			}
			
			//this is temporary unsafe way of getting payload in ascii may fail if multiple 
			//bytes have 0xFF 
			boolean payloadMarkerSeen =false;
			StringBuilder sb = new StringBuilder();
			sb.append("\npayload:");
			for(int i =5;i<_data.size()-2;i++)
			{
				if(payloadMarkerSeen){
					try {
						sb.append( new String(new byte[]{_data.get(i)}, "US-ASCII"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//sb.append((char)((byte) _data.get(i)));
				}else{
					if((byte)(_data.get(i)&0xFF)==(byte)0xFF) payloadMarkerSeen = true;
				}
			}
			
			return toret + sb.toString();
		}
		
		
		public String convert(byte[] data) {
		    StringBuilder sb = new StringBuilder(data.length);
		    for (int i = 0; i < data.length; ++ i) {
		        if (data[i] < 0) throw new IllegalArgumentException();
		        sb.append((char) data[i]);
		    }
		    return sb.toString();
		}
	
		
}
