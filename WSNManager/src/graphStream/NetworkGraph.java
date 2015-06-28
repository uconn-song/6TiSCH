package graphStream;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;

import network_model.NetworkObserver;
import network_model.WSNManager;

import org.graphstream.*;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.util.DefaultCamera;



/**
 * Handles the modification of GraphStream components, adding and deleting edges, nodes, and modifying their colors
 *
 */
public class NetworkGraph extends MultiGraph implements NetworkObserver {
	
		View view;
		Viewer viewer;
		Layout layout = new FrozenLayout(false);
	
	public NetworkGraph(String name) {
		super(name);
		   System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		setNodeFactory(new MoteNodeFactory());
		
		addAttribute("ui.quality");
		addAttribute("ui.antialias");
		//gs css
		//https://github.com/graphstream/gs-core/blob/master/src/org/graphstream/ui/graphicGraph/stylesheet/The_GraphStream_CSS
		String stylesheet = 
				"node { shape: circle; size: 24px,24px,24px; fill-color: rgb(137,169,227); text-size:18;" +
				" text-alignment: above; stroke-mode:plain; stroke-width: 6px; stroke-color: rgb(44,44,44); " +
				"}" +
				"edge { fill-color: rgb(50,250,50),red; size: 2px; }" +
				"edge.cut { fill-color: rgba(200,200,200,128); }" +
				"graph{fill-color:rgb(241,241,241); padding:100px; }" +
				"sprite{size:14px;}";
		addAttribute("ui.stylesheet",stylesheet);
		
		/**testing
		 * 
		 * 
		 */
		/* Layout layout = new FrozenLayout(false);
		
		    addSink(layout);
		    layout.addAttributeSink(this);
		    Toolkit.computeLayout(this, 0.50); // 0.99 is the stabilization threhold
		*/
		   // System.setProperty("gs.ui.layout", "FrozenLayout");
		/*
		SpringBox s = new SpringBox(false){
			@Override
			public void nodeAdded(String graphID, long time, String nodeID){
				
				super.nodeAdded(graphID, time, nodeID);
				//shake();
			
			}
		};
		s.setForce(1.0);
		s.setGravityFactor(1.0);
		s.setQuality(0.2);
		s.setStabilizationLimit(0.5);
		 addSink(s);
		    layout.addAttributeSink(s);
		*/
		viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
	//	viewer.enableAutoLayout(layout);
		//new DefaultCamera(viewer.getGraphicGraph()).setViewPercent(0.75);
		view = viewer.addDefaultView(false);   // false indicates "no JFrame".
		
		//prevent sprites from moving with custom manager
		view.setMouseManager(new MouseManager());
		view.getCamera().setViewPercent(0.5);
		view.getCamera().setAutoFitView(true);
		DefaultView v = (DefaultView)view;
		//v.getCamera().
		//(org.graphstream.ui.swingViewer.util.DefaultCamera)(view.getCamera());
		
		
	}
	public Viewer getViewer(){
		return viewer;
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
			//temp.addAttribute("layout.frozen");
			//layout.freezeNode(moteIID, true);
			temp.addAttribute("ui.label",moteIID);
			temp.addAttribute("layout.weight",0.01);
			if(moteIID == WSNManager.ROOT_ID_HEX){
				temp.addAttribute("ui.label","[ROOT]"+moteIID);
				temp.addAttribute("xyz", 0,0,0);
				
			}
			//viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			//viewer.enableAutoLayout();
			// view = viewer.addDefaultView(false);
		
			//view.getCamera().setAutoFitView(true);
			return temp;
		}catch(org.graphstream.graph.IdAlreadyInUseException e){
			return getNode(moteIID);
		}
	}
	
	/**
	 * remove a node and all edges associated with it from the graph
	 */
	public MoteNode removeNode(String moteIID){
		try{
			return super.removeNode(moteIID);
		}catch(org.graphstream.graph.ElementNotFoundException e){
		//	System.out.println("tried to remove " + moteIID + ", but it was not in the graph");
			return null;
		}
	}
	
	/**add an edge based on the two mote ids with the name being the concatenation of the
	 * base mote with the neighbor mote
	 * if it already exists ignore
	 */
	public void addEdge(String moteIIDbase, String moteIIDneighbor){
		try{
			String edgeName = moteIIDbase+moteIIDneighbor;
			addEdge(edgeName, moteIIDbase,moteIIDneighbor,true).addAttribute("stability", "stable"); 
			this.getEdge(edgeName).addAttribute("ui.style", "fill-color:green;");
			this.getEdge(edgeName).addAttribute("layout.weight", 0.7);
			
			
			/*****/
			
			SpriteManager sman = new SpriteManager(this);
			
			//if(sman.getSprite(moteIIDneighbor+moteIIDbase)==null){
			Sprite s = sman.addSprite(edgeName);
			s.attachToEdge(moteIIDbase+moteIIDneighbor);
			s.setPosition(0.5);
			//}
			//viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			//viewer.enableAutoLayout();
			 //view = viewer.addDefaultView(false);
			
		}catch(org.graphstream.graph.IdAlreadyInUseException e){
			//System.out.println("edge exists, no change");
			 Element edge = this.getEdge(moteIIDbase+moteIIDneighbor);
			if(edge.getAttribute("stability").equals("blocked")){
				System.out.println("edge blocked do not update");
				return;
			}
			this.getEdge(moteIIDbase+moteIIDneighbor).addAttribute("ui.style", "fill-color:green;");
			this.getEdge(moteIIDbase+moteIIDneighbor).addAttribute("stability", "stable");
		}
		
		
	}
	
	//TODO:Add edge removal
	@Override
	public void removeNeighbor(String iid64, String iid64_neighbor){
		try{
			getEdge(iid64 + iid64_neighbor).addAttribute("ui.style", "fill-color:red;");
			getEdge(iid64 + iid64_neighbor).addAttribute("stability", "unstable");
		}catch(NullPointerException e){
				System.out.println("attempt to remove non-existant edge");
			}
			try{
			getEdge(iid64_neighbor+ iid64).addAttribute("ui.style", "fill-color:red;");
			getEdge(iid64_neighbor+ iid64).addAttribute("stability", "unstable");}
			catch(NullPointerException e){
				System.out.println("attempt to remove non-existant edge");
			}
			
	}
	@Override
	public void notifyAddEdge(String iid64hex_base, String iid64hex_neighbor) {
			addEdge(iid64hex_base,iid64hex_neighbor);
			//addEdge(iid64hex_neighbor,iid64hex_base);
	}
	
	@Override
	public void newMoteNotification(String iid64hex) {
			addNode(iid64hex);
	}
	
	
	
	public void removeEdgeBothDirections(String id){
		
		
		String reverseEdge = id.substring(16, 32)+id.substring(0, 16);
		
		SpriteManager sm = new SpriteManager(this);
		if(	sm.getSprite(id) !=null) sm.getSprite(id).detach();
		sm.removeSprite(id);
		if(	sm.getSprite(reverseEdge) !=null) sm.getSprite(reverseEdge).detach();
		sm.removeSprite(reverseEdge);
		
		try{
			super.removeEdge(id);
			}catch(ElementNotFoundException e){}
		try{
		super.removeEdge(reverseEdge);
		}catch(ElementNotFoundException e){}
	}
	
	
	/**testing get logical graph**/
	
	public MultiGraph getLogicalGraph(){
		MultiGraph m = new MultiGraph("graph");
		Iterator<Edge> e = this.getEdgeSet().iterator();
		Iterator<Node> n = this.getNodeSet().iterator();
		while(n.hasNext()){
			m.addNode(n.next().getId());
		}
		while(e.hasNext()){
			Edge edge = e.next();
			if(edge.getAttribute("stability").equals("stable")){
			m.addEdge(edge.getId(), edge.getNode0().getId(), edge.getNode1().getId()).addAttribute("length", "1");
			}
		}
		return m;
	}
}
