package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import gui_components.ContentPanel;
import gui_components.ScrollableTextArea;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import network_model.Mote;

public class MoteInfoFrame extends JFrame{
	public MoteInfoFrame(String moteID, Mote mote){
		super(moteID);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		ContentPanel p = new ContentPanel();
		ScrollableTextArea area = new ScrollableTextArea();
		p.switchComponent(area);
		add(p,c);
		pack();
		area.append(mote.getID64() +"\n");
		area.append(mote.getNeighborTable().toString());
		area.append("hello");
	}
}
