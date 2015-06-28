package network_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Mote represents a mote in the network containing information about its neighbors
 */
public class Mote
{
	//key is 64 bit mote id in hex
    private HashMap<String,NeighborEntry> _neighbors = new HashMap<String,NeighborEntry>();
    private String _IID64_Hex;
    private int _dagRank=0;
    //private class
    private MessageHistory _history = new MessageHistory(10);
    
    
    //report whether neighbors have been updated
    private boolean neighborsUpdated = false;
	private NetworkModel _model;
    
    public Mote(String iid64_hex, NetworkModel m){
    	_model = m;
    	_IID64_Hex = iid64_hex;
    }
    
    
    /**
     * @return the hex id for this mote
     */
    public String getID64(){
    	return _IID64_Hex;
    }
    
    /**
     * 
     * @return string representing the last x messages (default 10)
     */
    public String getMessagesSentByThisMote(){
    	return _history.getHistory();
    }
    
    
 
    public void addMessageToHistory(String s){
    	_history.add(s);
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
    
  private class MessageHistory{
	  private String[] buffer;
	  private int currentIndex = 0;
	  public MessageHistory(int maxNumMessages){
		  buffer = new String[maxNumMessages];
	  }
	  public void add(String s){
		  currentIndex = (currentIndex+1)%(buffer.length-1);
		  buffer[currentIndex] =s;
	  }
	  public String getHistory(){
		  String s ="";
		  for(int i = 1; i< buffer.length;i++){
			  int oldest = (i+currentIndex)%(buffer.length-1);
			  if(buffer[oldest]!=null) s = s+ buffer[oldest] + "\n\n";
		  }
		//  System.out.println(s);
		  return s;
	  }
  }
  

  /**
   * calculate the RPL preferred parent based on the dag rank of the neighbors of this node.
   * @return null if no neighbors, else the neighbor with the lowest dag rank.
   */
  public NeighborEntry getPreferredParent(){
	  //if there are no neighbors recorded return null
	  if(_neighbors.size()==0) return null;
	  Iterator<NeighborEntry> it = _neighbors.values().iterator();
	  NeighborEntry preferredParent;
	   preferredParent = it.next();
	   //for each neighbor check if dag rank < the current neighbor with lowest dag rank, if so set as preferred parent
	  while(it.hasNext()){
		  NeighborEntry next = it.next(); 
		  String id = next.getiid64Hex();
		 int nextDag =  _model.getMote(id).getDagRank();
		  if(nextDag < preferredParent.DAGrank){
			  preferredParent = next;
		  }
	  }
	  
	  return preferredParent;
  }
  
  public void setDagRank(int i ){
	  _dagRank = i;
  }
  public int getDagRank(){
	  return _dagRank;
  }
  
  
  
  public boolean getNeighborsUpdated(){
	  return neighborsUpdated;
  }
  public void setNeighborsUpdated(boolean b){
	  neighborsUpdated = b;
  }
}
