package gui;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

public class GUIManager extends JFrame {

	private JPanel _contentArea;
	private JPanel _secondaryContentArea;
	private JToolBar _toolbar;
	private ControlPanel _controlPanel;
	public GUIManager(ControlPanel p){
		_controlPanel = p;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.2;
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=2;
		c.fill = GridBagConstraints.BOTH;
		
		_toolbar = new JToolBar();
		add(_toolbar,c);
		
		c.weightx=0.7;
		c.weighty=0.8;
		c.gridwidth=1;
		c.gridy=1;
		c.insets= new Insets(10,10,10,10);
		getContentPane().add(_controlPanel,c);
		c.weightx=0.3;
		c.gridx=1;
		getContentPane().add(new JPanel(),c);
		//f.getContentPane().add(new ControlPanel(this),c);
		getContentPane().setBackground(Color.DARK_GRAY);
		pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
