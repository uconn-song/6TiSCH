package network_model;

import java.util.HashMap;

/**
 * Mote represents a mote in the network containing information about its neighbors
 */
public class Mote
{
	//key is 64 bit mote id in hex
    private HashMap<String,NeighborEntry> _neighbors = new HashMap<String,NeighborEntry>();
    private String _IID64_Hex;
    public Mote(String iid64_hex){
    	_IID64_Hex = iid64_hex;
    }
    
    /**
     * @return the hex id for this mote
     */
    public String getID64(){
    	return _IID64_Hex;
    }
    
    /**
     * Overwrite the neighbor entry
     * If the entry does not exist, create it.
     * @param id64HexNeighbor
     * @param neighbor
     */
    public void updateTable(String id64HexNeighbor, NeighborEntry neighbor){
    	_neighbors.put(id64HexNeighbor, neighbor);
    }
    public HashMap<String,NeighborEntry> getNeighborTable(){
    	return _neighbors;
    }
}
