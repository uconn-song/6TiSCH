package gui;

import gui_components.ContentPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

public class GUIManager extends JFrame {

	private ContentPanel _contentArea = new ContentPanel();
	private ContentPanel _secondaryContentArea = new ContentPanel();
	private ControlPanel _controlPanel;

	// Menu
	private JMenuBar _menu;

	
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
		c.weightx = 0.7;
		c.weighty = 0.9;
		c.gridwidth = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 10, 10, 10);
		getContentPane().add(_contentArea, c);

		// add secondary content area (right)
		c.weightx = 0.3;
		c.gridx = 1;
		getContentPane().add(_secondaryContentArea, c);
		getContentPane().setBackground(Color.DARK_GRAY);
		
		
		pack();
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		switchPanel1("cp");
		//switchPanel2("cp");
	}

	private void switchPanel2(String component) {
		switch (component) {
		case "cp":
			_secondaryContentArea.switchComponent(_controlPanel);
			pack();
			break;
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
