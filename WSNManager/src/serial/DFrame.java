package serial;

import jNetPcap.JPacketTest;

import java.io.UnsupportedEncodingException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;

import packet.PacketAnalyzer;
import packet.IPUtils;
import packet.IPHCPacketAnalyzer;
import packet.PacketAnalyzer.Packet;
import stack.CoapMessage;
public class DFrame extends Frame {

		//This frame is used by the mote to indicate to the computer it is ready to receive data.
		private String _address="";
		private byte[] ipv6data;
		private String _protocol;
		private byte[] _L3_payload;
		private CoapMessage _CoAPMessage;
		private boolean _isCoAP_DFrame=false;
		
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
		
			
			/*
			 * determine what type of message this is, and extract the layer 3 message from it
			 * after this is called:
			 * _protocol field will be set to either "UDP" or "ICMPv6" if no errors found
			 * _L3_payload will have the raw data corresponding to that protocol
			 */
			decompressICMPv6(ipv6data, llsender,llreceiver);
			
			/*
			 * since udp is simple and we have no ports and this message is for the manager
			 * we can let CoAP ignore the header.
			 */
			if(_protocol.equals("UDP")){
				byte[] coapMess = new byte[_L3_payload.length-8];
				for(int i = 8;i<_L3_payload.length;i++)
				{
					coapMess[i-8] = _L3_payload[i];
				}
				try{
				_CoAPMessage = new CoapMessage(coapMess);
				_isCoAP_DFrame = true;
				}catch(IllegalArgumentException e)
				{
					System.out.println("Failed to parse UDP Datagram as CoAP: "+e.getMessage());
				}
				
			}
			
			/*Packet p = new Packet(ipv6data, PacketAnalyzer.NETWORK_LEVEL);
			StringBuilder brief = new StringBuilder();
			StringBuilder verbose = new StringBuilder();
			IPHCPacketAnalyzer anal = new IPHCPacketAnalyzer();
			p.llsender = llsender;
			p.llreceiver=llreceiver;
			anal.analyzePacket(p, brief, verbose);
			//System.out.println(verbose.toString());
			if(anal.isUDP()){
			//printRaw(p.getPayload());
			}
			//new JPacketTest(ipv6data);
			*/
		}
		
		
		
		public boolean isCoAPMessage(){
			return _isCoAP_DFrame;
				
		}
		/**
		 * attempt to decode DFrame as CoAP
		 * @return CoapMessage
		 */
		public CoapMessage getCoAPMessage(){
			if(!_isCoAP_DFrame) throw new IllegalArgumentException("Failed to decode DFrame as CoAP");
			return _CoAPMessage;
		}
		
		/**
		 * attempt to decompress and toss the iphc header and extract ICMPv6/UDP data
		 * @param ipv6data
		 * @param llsender
		 * @param llreceiver
		 * @return
		 */
		private byte[] decompressICMPv6(byte[] ipv6data, byte[] llsender,
				byte[] llreceiver) {
			//assume all possible fields elided, next header starts at byte 2
			int nh=2;
			int tfSize=0;
			int nhSize=0;
			int hlSize=0;
			//printRaw(ipv6data);
			if(!((ipv6data[0]>>5)==3)) throw new IllegalArgumentException("Expected iphc header not found");
			byte toParse = ipv6data[0];
			int tf = (toParse >> 3) & 0x03;
			boolean nhc = (toParse & 0x04) > 0;
			int hlim = (toParse & 0x03);
			toParse = ipv6data[1];
			int cid = (toParse >> 7) & 0x01;
			int sac = (toParse >> 6) & 0x01;
			int sam = (toParse >> 4) & 0x03;
			boolean m = ((toParse >> 3) & 0x01) != 0;
			int dac = (toParse >> 2) & 0x01;
			int dam = toParse & 0x03;
			
			//calculate next header location based on elided fields
			System.out.println("tf:"+tf + " nhc " + nhc + " hlim:" + hlim + " cid:" +cid+" sac:" + sac+" sam:" + sam +" m:"+ m +" dac:" +dac +" dam:" + dam );
			
			switch(tf){
			case 0: nh = nh+4;
					tfSize = 4;
				break;
			case 1: nh = nh+3;
					tfSize=3;
				break;
			case 2: nh = nh+1;
					tfSize=1;
				break;
			case 3: break;
			}
			
			if(nhc){
				throw new IllegalArgumentException("IPHC Next header compression not yet implemented.");
			}else{
				nhSize=1;
				nh++;
			}
			
			if(hlim==0){
				hlSize=1;
				nh++;
			}else{
				throw new IllegalArgumentException("Hop limit compression not yet implemented");
			}
			
			if(cid!=0){
				throw new IllegalArgumentException("CID not yet implemented");
			}
			
			//source address compression
			if(sac==0){
				switch(sam){
				case 0: nh = nh + 16; break;
				case 1:nh = nh+ 8;break;
				case 2:nh = nh+2;break;
				case 3:break;
				}
			} else{
				switch(sam){
				case 0:break;
				case 1:nh = nh+8;break;
				case 2:nh = nh+2;break;
				case 3:break;
				}
			}
			//destination address compression
			if(dac==0){
				switch(dam){
				case 0: nh = nh + 16; break;
				case 1:nh = nh+ 8;break;
				case 2:nh = nh+2;break;
				case 3:break;
				}
			} else{
				switch(dam){
				case 0:break;
				case 1:nh = nh+8;break;
				case 2:nh = nh+2;break;
				case 3:break;
				}
			}
			//figure out what transport layer protocol is being used
			int nextHeaderProtocol = ipv6data[2+tfSize];
			switch(nextHeaderProtocol){
			case 17:
				_protocol= "UDP";
				break;
			case 58:
				_protocol = "ICMPv6";
				break;
			default:
				throw new IllegalArgumentException("Unsupported protocol " + nextHeaderProtocol);
			}
			byte[] nextHeader= new byte[ipv6data.length-16-nh];
			for(int i = 0 ; i < nextHeader.length;i++){
				nextHeader[i] = ipv6data[i+nh];
			}
			_L3_payload = nextHeader;
			return nextHeader;
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
			
			
			
			return toret;
		}
		
		
		public String convert(byte[] data) {
		    StringBuilder sb = new StringBuilder(data.length);
		    for (int i = 0; i < data.length; ++ i) {
		        if (data[i] < 0) throw new IllegalArgumentException();
		        sb.append((char) data[i]);
		    }
		    return sb.toString();
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
		
}
