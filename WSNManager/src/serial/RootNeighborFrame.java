package serial;

import java.util.ArrayList;

public class RootNeighborFrame extends Frame {
	public RootNeighborFrame(ArrayList<Byte> data) {
		setType("RootNeighbor");
		setData(data);	
		//first payload byte should be the type for this message
		//type P - put frame, add the neighbor for the root
		//type D - remove specified address from the root's neighbor
		System.out.println("implement root neighbor frame");
		}
}
