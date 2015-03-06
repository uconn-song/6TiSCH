package network_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import serial.DFrame;
import serial.Frame;
import serial.SFrame;
import serial.SerialListener;

/**
 * Model of the network.
 */
public class NetworkModel implements SerialListener {
	private HashMap<String, Mote> _motes = new HashMap<String, Mote>();
	private String _rootid64Hex;
	private boolean _rootset = false;

	/**
	 * Method called once we have toggled root
	 * @param iidroot
	 */
	public void setRoot(String iidroot){
		_rootset =true;
		_rootid64Hex = iidroot;
	}
	
	// observer model collection
	private ArrayList<NetworkObserver> _networkObservers = new ArrayList<NetworkObserver>();

	public void addObserver(NetworkObserver o) {
		_networkObservers.add(o);
	}

	/**
	 * method to add neighbor to a specific mote.
	 */
	public void updateTableForMote(String id64hex, String id64HexNeighbor,
			NeighborEntry entry) {
			Mote base = _motes.get(id64hex);
			Mote neighbor  = _motes.get(id64HexNeighbor);
			if(neighbor!=null){
			}else{
				System.out.println("adding neighbor mote to network table" + id64hex);
				addNewMote(id64HexNeighbor);
			}
			if(base!=null){
				notifyAddEdge(id64hex, id64HexNeighbor);
			}else{
				System.out.println("adding mote to network table " + id64hex);
				addNewMote(id64hex);
				updateTableForMote(id64hex, id64HexNeighbor, entry);	
			}
	}

	/**
	 * method to create a new mote
	 */
	public void addNewMote(String id64hex) {
		_motes.put(id64hex, new Mote(id64hex));
		notifyObserversNewMote(id64hex);
	}

	/**
	 * Let the network graph know that a new mote was found
	 * 
	 * @param id64hex
	 */
	private void notifyObserversNewMote(String id64hex) {
		for (int i = 0; i < _networkObservers.size(); i++) {
			_networkObservers.get(i).newMoteNotification(id64hex);
		}
	}

	/**
	 * notify the network graph of an edge update
	 */
	public void notifyAddEdge(String iid64hex_base, String iid64hex_neighbor) {
		for (int i = 0; i < _networkObservers.size(); i++) {
			_networkObservers.get(i).notifyAddEdge(iid64hex_base,
					iid64hex_neighbor);
		}
	}

	/**
	 * @return an iterator over the motes connected to the network
	 */
	public Iterator<Mote> getConnectedMotes() {
		return _motes.values().iterator();
	}

	// public void updateNeighbor(String moteiid, int row, )

	@Override
	public void acceptFrame(Frame collectedFrame) {
		if(!_rootset) return;
		//confirm status frame
		if (collectedFrame.getType().equals("Status")) {
			SFrame f = (SFrame) collectedFrame;
			//confirm neighbor entry
			if (f._statusType.startsWith("9")) {
				NeighborEntry entry = f.parseNeighbors();
				if (entry.used == 1) {
					//System.out.println(entry.toString());
					//System.out.println(entry.getiid64Hex());
					updateTableForMote(_rootid64Hex,entry.getiid64Hex(),entry);
				}
			}
		}else if(collectedFrame.getType().equals("Data")){
			DFrame f = (DFrame)collectedFrame;
			if(f.isCoAPMessage()){
				String sourceMote = f.getSrcMoteId64Hex();
				byte[] coapPayload = f.getCoAPMessage().getPayload();
				int flag = coapPayload[0]&0xFF;
				if(flag==110)// 'n', then this is a neighbor entry
				{
					NeighborEntry e = new NeighborEntry(coapPayload);
					System.out.println(e.toString());
					if(e.used==1){
						System.out.println("neighbors " + sourceMote + " " + e.getiid64Hex());
						updateTableForMote(sourceMote,e.getiid64Hex(),e);
					}
				}else if (flag==100){ //'d'
					//neighbor table delete message
					
				}
			}
		}
		//TODO: Implement delete functions for the 
	}

}
