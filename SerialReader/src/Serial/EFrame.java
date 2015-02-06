package Serial;

import java.util.ArrayList;

public class EFrame extends Frame {
	//This frame is used by the mote to indicate to the computer it is ready to receive data.
	private String _address;
	public EFrame(ArrayList<Byte> data) {
		setType("Error");
		setData(data);	
		_address = Integer.toHexString(data.get(1)&0xFF) + Integer.toHexString(data.get(2)&0xFF);
	}
	
	@Override
	public String toString(){
		return _address + " Error Frame 0x" + Integer.toHexString(_data.get(4)) + " " + _data.get(5) + " " + _data.get(6);
	}
}
