package stack;

import java.util.Arrays;

import network_model.WSNManager;

/*   
 	   0                                       1
       0   1   2   3   4   5   6   7   8   9   0   1   2   3   4   5
     +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     | 0 | 1 | 1 |  TF   |NH | HLIM  |CID|SAC|  SAM  | M |DAC|  DAM  |
     +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+

                    Figure 2: LOWPAN_IPHC base Encoding
011		IPHC flag
11		TF 11:  Traffic Class and Flow Label are elided. 
0		NH 0: Full 8 bits for Next Header are carried in-line.
00		Hop Limit: Hop limit carried in-line
0 		CID:	No additional 8-bit context identifer extension is used
0		SAC: Stateless source address compression
xx		Source Address Mode
x		Is destination address multicast?
0		Is destination address compression stateful?
xx 		Destination Address mode
*/
public class IPHC_Data {
 private byte firstbyte = 0b01111000;
 private byte secondbyte = 0b00000001;
 private byte nextHeader = 0x11;
 private byte hopLimit = 0b01000000;
 private byte[] srcAddr128 = new byte[16];
 private byte[] destPrefix64 = new byte[8];
 public byte[] destAddr64 = new byte[8];
 //iphc payload, not including header
 private byte[] payload;
 //iphc header and payload
 private byte[] _messageBytes = new byte[0];
 //udp or icmpv6
 private byte[] _nextHeaderData = new byte[0];
private String _protocol;
 
 public IPHC_Data(UDP_Datagram d){
	 destAddr64 = d._destAddr64;
	 payload = d.getMessage();
	 for(int i = 0 ; i < 8;i++){
		 srcAddr128[i] =WSNManager.NETWORK_PREFIX[i];
	 }
	 for(int i=0;i<8;i++){
		 srcAddr128[i+8]=WSNManager.ROOT_ID[i];
	 }
	 //System.out.println(destaddr64.length);
	 
	 packageMessage();
 }
 
 /**
  * creates the IPHC header and stores in messagebytes
  * note that payload is the iphc payload which does not include the header
  */
 private void packageMessage() {
	 byte[] b = new byte[2+1+1+srcAddr128.length+destAddr64.length+payload.length];
	 b[0] = firstbyte;
	 b[1] = secondbyte;
	 b[2] = nextHeader;
	 b[3] = hopLimit;
	 int index = 4;
	 for(int i = 0 ; i < srcAddr128.length;i++){
		 b[index] = srcAddr128[i];
		 index++;
	 }
	 for(int i=0;i<destAddr64.length;i++){
		 b[index] = destAddr64[i];
		 index++;
	 }
	 for(int i = 0; i<payload.length;i++){
		 b[index] = payload[i];
		 index++;
	 }
	 _messageBytes = b;
}

/**
 * Constructor for parsing iphc header and data, deriving fields, etc
 * @param ipv6data
 * @param l2sender
 * @param l2receiver
 */
public IPHC_Data(byte[] ipv6data, byte[] l2sender, byte[] l2receiver) {
	//assume all possible fields elided, next header starts at byte 2
		int nhPtr=2;
		int tfSize=0;
		int nhSize=0;
		int hlSize=0;
		//printRaw(ipv6data);
		if(!((ipv6data[0]>>5)==3)) throw new IllegalArgumentException("The expected iphc header was not found");
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
		//System.out.println("tf:"+tf + " nhc " + nhc + " hlim:" + hlim + " cid:" +cid+" sac:" + sac+" sam:" + sam +" m:"+ m +" dac:" +dac +" dam:" + dam );
		
		switch(tf){
		case 0: nhPtr = nhPtr+4;
				tfSize = 4;
			break;
		case 1: nhPtr = nhPtr+3;
				tfSize=3;
			break;
		case 2: nhPtr = nhPtr+1;
				tfSize=1;
			break;
		case 3: break;
		}
		
		if(nhc){
			throw new IllegalArgumentException("IPHC Next header compression not yet implemented.");
		}else{
			nhSize=1;
			nhPtr++;
		}
		
		if(hlim==0){
			hlSize=1;
			nhPtr++;
		}else{
			throw new IllegalArgumentException("Hop limit compression not yet implemented");
		}
		
		if(cid!=0){
			throw new IllegalArgumentException("CID not yet implemented");
		}
		
		//source address compression
		if(sac==0){
			switch(sam){
			case 0: nhPtr = nhPtr + 16; break;
			case 1:nhPtr = nhPtr+ 8;break;
			case 2:nhPtr = nhPtr+2;break;
			case 3:
				//System.out.println("source from elided recomposition:");
				//printRawHex(WSNManager.NETWORK_PREFIX);
				//printRawHex(l2sender);
				break;
			}
		} else{
			switch(sam){
			case 0:break;
			case 1:nhPtr = nhPtr+8;break;
			case 2:nhPtr = nhPtr+2;break;
			case 3:	break;
			}
		}
		//destination address compression
		if(dac==0){
			switch(dam){
			case 0: nhPtr = nhPtr + 16; break;
			case 1:nhPtr = nhPtr+ 8;break;
			case 2:nhPtr = nhPtr+2;break;
			case 3:
				//both prefix and iid elided, prefix same as manager, iid derived from pseudo header passed as argument
				destPrefix64 = WSNManager.NETWORK_PREFIX;
				destAddr64 = l2receiver;
				break;
			}
		} else{
			switch(dam){
			case 0:break;
			case 1:nhPtr = nhPtr+8;break;
			case 2:nhPtr = nhPtr+2;break;
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
		
		//System.out.println(_protocol);
		//printRaw(ipv6data);
		byte[] nextHeader= new byte[ipv6data.length-nhPtr];
		for(int i = 0 ; i < nextHeader.length;i++){
			nextHeader[i] = ipv6data[i+nhPtr];
		}
		
		_nextHeaderData = nextHeader;
		
		//System.out.println("hi" + _nextHeaderData.length);
		//printRaw(_nextHeaderData);
		
}

/**
 * Used in analyzing received packet, if true then this message was meant to be parsed by the manager.
 * @return
 */
public boolean isDestinationRoot(){
	for(int i = 0 ; i< 8;i++){
		if((WSNManager.ROOT_ID[i]&0xFF)!=(destAddr64[i]&0xFF)) return false;
	}
	return true;
}


/**
 * returns whether this is a UDP or ICMPv6 message
 */
public String getNextHeaderProtocol(){
	return _protocol;
}

/**
 * used when parsing a received frame from the motes
 */
public byte[] getNextHeader(){
  return _nextHeaderData;
}

/**
 * returns the header + payload as a byte array
 */
public byte[] getMessage(){
	 return _messageBytes;
 }
 
 public void printRaw(byte[] b){
		for(int i = 0 ; i < b.length;i++){
			if(i>0 && i%4==0.0)
			{
				System.out.println();
			}
			System.out.print(byteToString(b[i]) + " ");
			//every 4 bytes new line
		}
		System.out.println();
	}
 private void printRawHex(byte[] b) {
		for(int i =0;i<b.length;i++)
		{
			String out = Integer.toHexString(b[i]&0xFF);
			if(out.length()==1){
				out = "0"+out;
			}
			System.out.print(out);
		}
		System.out.println();
		
	}
 private String byteToString(byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(
				' ', '0');
	}
}
