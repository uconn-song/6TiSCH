package serial;

import java.util.ArrayList;

public class UknownFrame extends Frame {
	
	public UknownFrame(ArrayList<Byte> data) {
		setType("Unknown Frame");
		setData(data);	
		}
public String toString(){
	return "Unknown Frame Received";
}
}
