package gui;

import graphStream.NetworkGraph;
import gui_components.ContentPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;
import javax.swing.text.View;

import network_model.NetworkObserver;

import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class GUIManager extends JFrame{

	private ContentPanel _contentArea = new ContentPanel();
	private ContentPanel _secondaryContentArea = new ContentPanel();
	private ControlPanel _controlPanel;
	private HashMap<String,JComponent> _panels = new HashMap<String,JComponent>();
	// Menu
	private JMenuBar _menu;
	private NetworkGraph _graph;
	ContentPanel _graphPanel = new ContentPanel();

	
	public GUIManager(ControlPanel p) {
		_controlPanel = p;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.2;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;

		_menu = new JMenuBar();

		_menu.add(new JMenu("SDF"));
		add(_menu, c);

		// add main content area (left)
		c.weightx = 0.5;
		c.weighty = 0.9;
		c.gridwidth = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 10, 10, 10);
		getContentPane().add(_contentArea, c);

		// add secondary content area (right)
		c.weightx = 0.5;
		c.gridx = 1;
		getContentPane().add(_secondaryContentArea, c);
		getContentPane().setBackground(Color.DARK_GRAY);
		
		initializeGraph();
		pack();
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		_panels.put("control panel", _controlPanel);
		switchPanel1("control panel");
		switchPanel2("graph");
		
		    
	}

	
	
	private void initializeGraph() {
		_graph = new NetworkGraph("embedded");
	/*	Layout layout = new SpringBox(false);
		    _graph.addSink(layout);
		    layout.addAttributeSink(_graph);
		    while(layout.getStabilization() < 0.9){
		        layout.compute();
		    }   */
		    
		org.graphstream.ui.swingViewer.View v = _graph.getView();
//		v.getCamera().setViewPercent(1.2);
//		v.getCamera().setViewCenter(0, 0, 0);
//		v.getCamera().setAutoFitView(true);
		_graphPanel.switchComponent(_graph.getView());
		addComponent("graph",_graphPanel);
		
	}


	public void switchPanel2(String component) {
		try{
		_secondaryContentArea.switchComponent(_panels.get(component));
		}
		catch(NullPointerException e){
			//do nothing do not switch 
		}
		
	}

	public void switchPanel1(String component) {
		try{
			_contentArea.switchComponent(_panels.get(component));
		}
			catch(NullPointerException e){
				
			}
	}



	public void addComponent(String string, JComponent c) {
		_panels.put(string, c);
		
	}



	public NetworkGraph getGraph() {
		return _graph;
	}
}
