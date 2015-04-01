package serial;

import java.util.ArrayList;

/**
 * The dag root cannot run CoAP applications, so we use custom serial messages to update neighbor information.
 *
 */
public class RootNeighborFrame extends Frame {
	private String address;
	public RootNeighborFrame(ArrayList<Byte> data) {
		setType("RootNeighbor");
		setData(data);	
		//first payload byte should be the type for this message
		//type P - put frame, add the neighbor for the root
		//type D - remove specified address from the root's neighbor
		 address = "";
		for (int i =0;i<8;i++){
			address = address + String.format("%2s", Integer.toHexString((data.get(i+1)&0xFF))).replace(' ','0');
		}
	}
	
	public String getRemovedNeighborID(){
		return address;
	}
}
