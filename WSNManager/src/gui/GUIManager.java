package gui;

import graphStream.NetworkGraph;
import gui_components.ContentPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class GUIManager extends JFrame {

	private ContentPanel _contentArea = new ContentPanel();
	private ContentPanel _secondaryContentArea = new ContentPanel();
	private ControlPanel _controlPanel;

	// Menu
	private JMenuBar _menu;
	private NetworkGraph _graph = new NetworkGraph("embedded");

	
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
		
		
		pack();
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		switchPanel1("cp");
		switchPanel2("gs");
		
		
		 Layout layout = new SpringBox(false);
		    _graph.addSink(layout);
		    layout.addAttributeSink(_graph);
		    while(layout.getStabilization() < 0.9){
		        layout.compute();
		    }   
		    
		    _graph.addNode("cbfc");
		    _graph.addNode("cbfc");
		    _graph.addNode("2616");
		    _graph.addEdge("cbfc2616", "cbfc", "2616");
		    _secondaryContentArea.switchComponent(_graph.getView());
	}

	private void switchPanel2(String component) {
		switch (component) {
		case "cp":
			_secondaryContentArea.switchComponent(_controlPanel);
			pack();
			break;
		case "gs":
			_secondaryContentArea.switchComponent(_graph.getView());
			pack();
		default:
			break;
		}
		
	}

	public void switchPanel1(String component) {
		switch (component) {
		case "cp":
			_contentArea.switchComponent(_controlPanel);
			pack();
			break;
		default:
			break;
		}
	}

}
