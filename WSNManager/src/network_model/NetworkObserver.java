package network_model;

import java.util.HashMap;

/**
 * Observer interface to allow the graph to listen for network changes
 */
public interface NetworkObserver {
	//notify new edge update
	public void notifyEdgeUpdate(String iid64hex_base, String ii64hex_neighbor);
	//notify observer of new mote
	public void newMoteNotification(String iid64hex);
}
