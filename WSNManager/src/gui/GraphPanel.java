package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import network_model.CoAPBuilder;
import network_model.NetworkModel;
import network_model.WSNManager;

import org.graphstream.graph.Edge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.View;

import graphStream.NetworkGraph;
/** Main function: Handle mouse interactions
 * JPanel implementation which holds the GraphStream graph. This panel listens for mouse input on the graph and 
 * handles it accordingly. This panel also gets a reference to the network model to allow mouse clicks to modify the
 * network or retrieve data from the network.
 */
public class GraphPanel extends FormattedJPanel implements MouseListener,KeyListener,MouseWheelListener{
	private NetworkGraph _graph;
	private View _view; 
	private NetworkModel _networkModel;
	//private HashMap<String,GraphicElement> _selectedElements  = new HashMap<String,GraphicElement>();
	private WSNManager _manager;
	private boolean[] buttonsDown = new boolean[5];
	private boolean[] keysDown = new boolean[1000];
	
	
	
	private HashMap<String,MoteInfoFrame> _moteFrames = new HashMap<String,MoteInfoFrame>();
	
	
	public GraphPanel(NetworkGraph graph, NetworkModel networkModel, WSNManager manager){
		super(500,500);
		_graph = graph;
		_view = _graph.getView();
		_view.addMouseListener(this);
		switchComponent(_view);
		_networkModel = networkModel;
		
		_view.addMouseWheelListener(this);
		_view.addKeyListener(this);
		_manager =manager;
		
		setVisible(false);
	}

	
	
	/**
	 * Allow interaction with the graph via mouse clicks
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		buttonsDown[e.getButton()] =true;
		
	
		/* DIJKSTRA TESTING STUFF
		if(this.ctrlDown){
			MultiGraph m = _graph.getLogicalGraph();
			m.display();
			
			
			 // Edge lengths are stored in an attribute called "length"
			 // The length of a path is the sum of the lengths of its edges
			 // The algorithm will store its results in attribute called "result"
			 Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "result", "length");
			
		        
			 // Compute the shortest paths in g from A to all nodes
			 dijkstra.init(m);
			 dijkstra.setSource(m.getNode(0));
			 dijkstra.compute();
			        
			 // Print the lengths of all the shortest paths
			 for (Node node : m)
			     System.out.printf("%s->%s:%6.2f%n", dijkstra.getSource(), node, dijkstra.getPathLength(node));
			
		}
		
		*/
		
		
		//check for node or edge press
		GraphicElement element  = _view.findNodeOrSpriteAt(e.getX(), e.getY());
		if(!(element==null)){
			//System.out.println(element.getId() + " " + element.getSelectorType());
			if(element.getSelectorType().toString().equals("NODE")){	
					if(((String) element.getAttribute("ui.label")).contains("ROOT")) {
						return;
					}
					handleNodePress(element.getId(),e.getButton());			
			}else{
					handleEdgePress(element.getId(),e.getButton());
				}
		}
	}

	
	/*
	 * 
	 * currently no support for clicking on edge****
	 */
	private void handleEdgePress(String id, int button) {
		
		Edge e = _graph.getEdge(id);
		if(e != null){
			String stability = e.getAttribute("stability");
			System.out.println("path stability: " + stability);
			if(stability.equals("unstable")){
				_graph.removeEdgeBothDirections(id);
			}	else if(keysDown[KeyEvent.VK_CONTROL]){
				//toggle stability
				if(e.getAttribute("stability").equals("blocked")){
					//unblocking
					e.setAttribute("stability", "stable");
					e.addAttribute("ui.style", "fill-color:green;");
					//reflect this in network model (remove neighbor)
				}else{
					//blocking
				e.setAttribute("stability", "blocked");
				e.addAttribute("ui.style", "fill-color:rgb(64,64,64);");
				
				//_manager.send(new CoAPBuilder(_manager.getGraph(), _manager.getNetworkModel(), "PUT", endpoint , _moteID, payload,0).getSerialPacket());
				
				//reflect this in network model (add neighbor back)
				}
				
			}
		}
	}

	private void handleNodePress(String id, int button) {
		if(button == MouseEvent.BUTTON1){
		_manager.send(new CoAPBuilder(_graph, _networkModel, "GET", "n", id, new byte[]{(byte)0xFF,1},0).getSerialPacket());
		}else{
			try{
				_moteFrames.get(id).setVisible(true);
			}catch(NullPointerException e)
			{
				MoteInfoFrame f = new MoteInfoFrame(id, _networkModel.getMote(id), _manager);
				f.setVisible(true);
				_moteFrames.put(id, f);
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		buttonsDown[arg0.getButton()] =true;
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		buttonsDown[arg0.getButton()] =false;
		
	}



	@Override
	public void keyPressed(KeyEvent e) {
		keysDown[e.getKeyCode()]=true;
		if(keysDown[KeyEvent.VK_EQUALS]&&keysDown[KeyEvent.VK_CONTROL]){
			_view.getCamera().setAutoFitView(false);
			_view.getCamera().setViewPercent(_view.getCamera().getViewPercent()-0.1);
		}
		else if(keysDown[KeyEvent.VK_MINUS]&&keysDown[KeyEvent.VK_CONTROL]){
			_view.getCamera().setAutoFitView(false);
			_view.getCamera().setViewPercent(_view.getCamera().getViewPercent()+0.1);
		}
		else if(keysDown[KeyEvent.VK_R]&&keysDown[KeyEvent.VK_CONTROL]){
			_view.getCamera().resetView();
			_view.getCamera().setAutoFitView(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown[e.getKeyCode()]=false;
		
	}



	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		if(keysDown[KeyEvent.VK_CONTROL]){
			if(arg0.getWheelRotation()>0){
				//wheel scrolled up
				_view.getCamera().setAutoFitView(false);
				_view.getCamera().setViewPercent(_view.getCamera().getViewPercent()-0.1);
			}else{
				_view.getCamera().setAutoFitView(false);
				_view.getCamera().setViewPercent(_view.getCamera().getViewPercent()+0.1);
			}
		}
		
	}
}
