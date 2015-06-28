package graphStream;

import java.util.Iterator;

import network_model.Mote;
import network_model.NetworkModel;
import network_model.NetworkObserver;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * A visual representation of the RPL DAG constructed based on the latest network model.
 *
 */
public class RPLGraph extends MultiGraph {
	
	View view;
	Viewer viewer;
	Layout layout = new FrozenLayout(false);
	private NetworkModel _model;

public RPLGraph(String name, NetworkModel model) {
	super(name);
	_model = model;
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
			"edge { fill-color: orange; size: 2px; }" +
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
	
	buildGraph();
	
}
private void buildGraph() {
	Mote root = _model.getRootMote();
	MoteNode rootNode = this.addNode(root.getID64());
	rootNode.addAttribute("ui.label", "[ROOT]" + root.getID64() + "  Rank:" + root.getDagRank());
	
	//add all motes
	Iterator<Mote> it = _model.getConnectedMotes();
	while(it.hasNext()){
		Mote mote = it.next();
		if(mote.getID64().equals(root.getID64())) continue;
		addNode(mote.getID64()).addAttribute("ui.label",mote.getID64() + "  Rank:" + mote.getDagRank());
	}
	
	//add all edges
	it = _model.getConnectedMotes();
	while(it.hasNext()){
		Mote mote = it.next();
		if(mote.getID64().equals(root.getID64())) continue;
		String edgeDestination = mote.getPreferredParent().getiid64Hex();
		addEdge(mote.getID64()+edgeDestination, mote.getID64(), edgeDestination,true);
	}
}
public Viewer getViewer(){
	return viewer;
}
public View getView(){
	return view;
}

}
