package stack;
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
 public byte[] destAddr64 = new byte[8];
 private byte[] payload;
 
 public IPHC_Data(UDP_Datagram d){
	 destAddr64 = d._destAddr64;
	 payload = d.getMessage();
	 for(int i = 0 ; i < srcAddr128.length;i++){
		 srcAddr128[i] = (byte) i;
	 }
	 //System.out.println(destaddr64.length);
 }
 
 
 public byte[] getMessage(){
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
	 return b;
 }
 
 public void printRaw(byte[] b){
		for(int i = 0 ; i < b.length;i++){
			if(i==2+1+1+srcAddr128.length+destAddr64.length){
				System.out.println("Payload:");
			}
			if(i>0 && i%4==0.0)
			{
				System.out.println();
			}
			System.out.print(byteToString(b[i]) + " ");
			//every 4 bytes new line
		}
		System.out.println();
	}
 
 private String byteToString(byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(
				' ', '0');
	}
}
