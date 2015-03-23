package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import network_model.NetworkModel;
import network_model.WSNManager;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.View;

import graphStream.NetworkGraph;
import gui_components.ContentPanel;
/**
 * JPanel implementation which holds the GraphStream graph. This panel listens for mouse input on the graph and 
 * handles it accordingly. This panel also gets a reference to the network model to allow mouse clicks to modify the
 * network or retrieve data from the network.
 */
public class GraphPanel extends ContentPanel implements MouseListener {
	private NetworkGraph _graph;
	private View _view; 
	private NetworkModel _networkModel;
	
	public GraphPanel(NetworkGraph graph, NetworkModel networkModel){
		super();
		_graph = graph;
		_view = _graph.getView();
		_view.addMouseListener(this);
		switchComponent(_view);
		_networkModel = networkModel;
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
			handleNodePress(element.getId(),e.getButton());
		}else if(element.getSelectorType().toString().equals("EDGE")){
			
			handleEdgePress(element.getId(), e.getButton());
		}
		
		}
		
	}

	private void handleEdgePress(String id, int button) {
		// TODO Auto-generated method stub
		System.out.println("Handle clicks in GraphPanel");
		
	}

	private void handleNodePress(String id, int button) {
		// TODO Auto-generated method stub
		System.out.println("Handle clicks in GraphPanel");
		MoteInfoFrame f = new MoteInfoFrame(id, _networkModel.getMote(id));
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
}
