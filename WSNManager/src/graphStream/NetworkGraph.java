package graphStream;
import javax.swing.JPanel;

import network_model.NetworkObserver;
import network_model.WSNManager;

import org.graphstream.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.util.DefaultCamera;

public class NetworkGraph extends MultiGraph implements NetworkObserver {
	
		View view;
		Viewer viewer;
	
	public NetworkGraph(String name) {
		super(name);
		setNodeFactory(new MoteNodeFactory());
		
		addAttribute("ui.quality");
		addAttribute("ui.antialias");
		String stylesheet = "node { size: 20px; fill-color: rgb(50,50,150); text-size:20;}" +
				"edge { fill-color: rgb(50,250,50); size: 2px; }" +
				"edge.cut { fill-color: rgba(200,200,200,128); }";
		addAttribute("ui.stylesheet",stylesheet);
		//viewer = display(false);
		viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		new DefaultCamera(viewer.getGraphicGraph()).setViewPercent(0.75);
		view = viewer.addDefaultView(false);   // false indicates "no JFrame".
		
		
		
	}
	
	public View getView(){
		return view;
	}
	
	/**add a node based on the mote id
	 * if it already exists return that node instead
	 */
	public MoteNode addNode(String moteIID){
		try{
			MoteNode temp = super.addNode(moteIID);
			
			temp.addAttribute("ui.label",moteIID);
			if(moteIID == WSNManager.ROOT_ID_HEX){
				temp.addAttribute("ui.label","[ROOT]"+moteIID);
			}
			//viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			//viewer.enableAutoLayout();
			// view = viewer.addDefaultView(false);
			return temp;
		}catch(org.graphstream.graph.IdAlreadyInUseException e){
			return getNode(moteIID);
		}
	}
	
	
	/**add an edge based on the two mote ids with the name being the concatenation of the
	 * base mote with the neighbor mote
	 * if it already exists ignore
	 */
	public void addEdge(String moteIIDbase, String moteIIDneighbor){
		try{
			addEdge(moteIIDbase+moteIIDneighbor, moteIIDbase,moteIIDneighbor,true); 
			//viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			//viewer.enableAutoLayout();
			 //view = viewer.addDefaultView(false);
			
		}catch(org.graphstream.graph.IdAlreadyInUseException e){
			System.out.println("edge exists, no change");
		}
	}
	
	//TODO:Add edge removal
	@Override
	public void notifyEdgeUpdate(String iid64hex_base, String iid64hex_neighbor) {
			addEdge(iid64hex_base,iid64hex_neighbor);
	}
	@Override
	public void newMoteNotification(String iid64hex) {
			addNode(iid64hex);
	}
}
