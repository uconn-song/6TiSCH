package Testing;

import Serial.Frame;
import Serial.HDLC;

public class CRCTester {
	public static void crc(){
		
		byte[] b = {
				
				(byte) 01000100, (byte) 11111100, (byte) 11001011, (byte) 11101111,
				(byte) 11111111, (byte) 00001000, 00000000, 00000000,(byte) 00001111
		};
		
		int crc = HDLC.crcIteration(HDLC.HDLC_CRCINIT, b[0]);
		for(int i = 1 ; i< b.length;i++)
		{
			crc = HDLC.crcIteration(crc, b[i]);
		}
		crc = ~crc;
		
		System.out.println((byte) (crc>>0&0xFF));
		System.out.println(crc>>8&0xFF);
	}
}
