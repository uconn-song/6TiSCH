package stack;

import java.util.ArrayList;

public class ByteMessage {
protected ArrayList<Byte> message = new ArrayList<Byte>();




/**
 * 
 * @param byteIndex starting from 0
 * @param positionFromLeft starting from 0
 */
public void setBit(int byteIndex, int positionFromLeft){
	
	while(message.size()<=byteIndex){
		message.add((byte) 0);
	}
	byte newByte = (byte) (message.get(byteIndex)^(1<<(7-positionFromLeft)));
	message.set(byteIndex, newByte);	
	printRaw();
}

/**
 * 
 * @param byteIndex starting from 0
 * @param positionFromLeft starting from 0
 */
private void clearBit(int byteIndex, int positionFromLeft){
	
	while(message.size()<=byteIndex){
		message.add((byte) 0);
	}
	
	byte newByte = (byte) (message.get(byteIndex)&(0b11111111^1<<(7-positionFromLeft)));
	message.set(byteIndex, newByte);	
	printRaw();
}
/**
 * @param byteIndex starting from 0
 */
private void  setByte(int byteIndex, byte newByte){
	message.set(byteIndex, newByte);
}


private byte[] getMessageAsByteArray(){
	byte[] b = new byte[message.size()];
	for(int i =0;i< b.length;i++)
	{
		b[i] = message.get(i);
	}
	
	return b;
}










//Debug Functions
public void printRaw(){
	for(int i = 0 ; i < message.size();i++){
		if(i>0 && i%4==0.0)
		{
			System.out.println();
		}
		System.out.print(byteToString(message.get(i)) + " "  );
		//every 4 bytes new line
		
	}
	System.out.println();
}

public static String byteToString(byte b)
{
	return String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ', '0');
}
}
