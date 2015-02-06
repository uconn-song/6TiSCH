package Serial;

import java.util.ArrayList;

public abstract class Frame {
	//common to Frames in order to distinguish from one another
	protected String _frameType;
	protected ArrayList<Byte> _data;
	
	public void setType(String type){
		_frameType = type;
	}
	
	public String getType(){
		return _frameType;
	}
	
	public void setData(ArrayList<Byte> data){
		_data = data;
	}
	
	public ArrayList<Byte> data(){
		return _data;
	}
	
	public static String byteToString(byte b)
	{
		return String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ', '0');
	}
	
	//debug print packet
	public void printRaw(){
		for(int i = 0 ; i < _data.size();i++){
			System.out.println(byteToString(_data.get(i)) + " " + (_data.get(i)&0xFF) + " " + (char)((_data.get(i)&0xFF)));
			
		}
		System.out.println();
	}
}
