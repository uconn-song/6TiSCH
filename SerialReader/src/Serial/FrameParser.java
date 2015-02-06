package Serial;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FrameParser {
	private ArrayList<Byte> data = new ArrayList<Byte>();
	
	public int getSize(){
		return data.size();
	}
	
	public boolean isEmpty(){
		if(data.size()==0)
			return true;
		return false;
	}
	public void addData(Byte b)
	{
		data.add(b);
	}
	
	public Frame parse() throws Exception, UnsupportedEncodingException
	{
		byte[] b = new byte[]{data.get(0)};
		
		for(int i = 0 ; i < data.size();i++){
			System.out.print(byteToString(data.get(i)));
		}
		System.out.println();
		String type;
		//build a frame based on the packet encoding
		
				type = new String(b, "UTF-8");
				//System.out.print(type);
				switch(type){
				case "S":
					return new SFrame(data);
				case "R":
					return new RFrame(data);
				case "E":
					return new EFrame(data);
				case "D":
					return new DFrame(data);
				case "I":
					return new IFrame(data);
				default:
					throw(new Exception(type + " unsupported"));
				}
				
	}
	
	public String byteToString(byte b)
	{
		return String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ', '0')+ " ";
	}
	
	

	
}

/*//old function to print the packet
 * 
 * public void printData()
	{
		//determine which type of packet it is
		//https://openwsn.atlassian.net/wiki/display/OW/Serial+Format#SerialFormat-GeneralFormat.1
		byte[] b = new byte[]{data.get(0)};
		String packType;
		try {
				packType = new String(b, "UTF-8");
				System.out.print(packType);
				
				if(packType.equals("S")){
					System.out.print(" Address: " + byteToString(data.get(1)) + byteToString(data.get(2)));
					System.out.print(", Type: "+ (data.get(3)&0xFF));
					System.out.println();
					
				}
			} catch (UnsupportedEncodingException e) {	e.printStackTrace();}
			
			
		for(int i = 0; i< data.size(); i++)
		{
			System.out.print(byteToString(data.get(i))+ " ");
		}
		System.out.println();
	}
	*/
