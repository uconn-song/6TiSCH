package network_model;

import java.util.HashMap;

/**
 * Observer interface to allow the graph to listen for network changes
 */
public interface NetworkObserver {
	//notify observers that an edge is being added if the neighbor does not exist it is created automatically
	public void notifyAddEdge(String iid64hex_base, String ii64hex_neighbor);
	//notify observer of new mote
	public void newMoteNotification(String iid64hex);
	//notify the observer that a neighbor needs to be removed
	public void removeNeighbor(String iid64, String iid64_neighbor);
}
