package Serial;

import java.util.ArrayList;

public class RFrame extends Frame {
	//This frame is used by the mote to indicate to the computer it is ready to receive data.
	public RFrame(ArrayList<Byte> data) {
		setType("Request");
		setData(data);
	}
	
	@Override
	public String toString(){
		return "Request from mote to connect to computer";
	}
}
