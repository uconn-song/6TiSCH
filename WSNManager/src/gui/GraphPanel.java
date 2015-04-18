package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import network_model.CoAPBuilder;
import network_model.NetworkModel;
import network_model.WSNManager;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.View;

import graphStream.NetworkGraph;
import gui_components.ContentPanel;
/** Main function: Handle mouse interactions
 * JPanel implementation which holds the GraphStream graph. This panel listens for mouse input on the graph and 
 * handles it accordingly. This panel also gets a reference to the network model to allow mouse clicks to modify the
 * network or retrieve data from the network.
 */
public class GraphPanel extends ContentPanel implements MouseListener,KeyListener{
	private NetworkGraph _graph;
	private View _view; 
	private NetworkModel _networkModel;
	private boolean ctrlDown = false;
	//private HashMap<String,GraphicElement> _selectedElements  = new HashMap<String,GraphicElement>();
	private String[] _selectedElements = {"",""};
	private int selectedCount = 0;
	private WSNManager _manager;
	public GraphPanel(NetworkGraph graph, NetworkModel networkModel, WSNManager manager){
		super();
		_graph = graph;
		_view = _graph.getView();
		_view.addMouseListener(this);
		switchComponent(_view);
		_networkModel = networkModel;
		_view.addKeyListener(this);
		_manager =manager;
	}

	
	
	/**
	 * Allow interaction with the graph via mouse clicks
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		GraphicElement element  = _view.findNodeOrSpriteAt(e.getX(), e.getY());
		
		
		if(!(element==null)){
			
			
			
			
			
			System.out.println(element.getId() + " " + element.getSelectorType());
			if(element.getSelectorType().toString().equals("NODE"))
			{
				if(this.ctrlDown &&selectedCount <2){
					_selectedElements[selectedCount] = element.getId();
					selectedCount ++;
					
					if(selectedCount ==2){
						//search for an edge
						try{
						System.out.println(_graph.getEdge(_selectedElements[0]+_selectedElements[1]).getAttribute("stability"));
						}catch (NullPointerException ex){
							//System.out.println("no edge found for " +_selectedElements[0]+_selectedElements[1] );
						}
						try{
						System.out.println(_graph.getEdge(_selectedElements[1]+_selectedElements[0]).getAttribute("stability"));
						}catch(NullPointerException ex){
							//System.out.println("no edge found for " +_selectedElements[1]+_selectedElements[0] );
						}
					}
				}else{
					//if this is the only element selected
					handleNodePress(element.getId(),e.getButton());
				}
			}
			//edges cannot be pressed
			
		}else{
			_selectedElements[0] = "";
			_selectedElements[1] = "";
			selectedCount = 0;
		}
		
	}

	
	/*
	 * 
	 * currently no support for clicking on edge****
	 */
	private void handleEdgePress(String id, GraphicElement element, int button) {
		// TODO Auto-generated method stub
		System.out.println("Handle edge clicks in GraphPanel");
		System.out.println(element.getAttribute("stability"));
		
	}

	private void handleNodePress(String id, int button) {
		_manager.send(new CoAPBuilder(_networkModel, "GET", "n", id, new byte[]{(byte)0xFF,1}).getSerialPacket());
		
		MoteInfoFrame f = new MoteInfoFrame(id, _networkModel.getMote(id), _networkModel, _manager);
		f.setVisible(true);
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()== KeyEvent.VK_CONTROL){
			ctrlDown=true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()== KeyEvent.VK_CONTROL){
			ctrlDown=false;
		}
		
	}



	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
