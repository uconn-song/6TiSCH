package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;

import gui_components.ContentPanel;
import gui_components.ScrollableTextArea;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import network_model.CoAPBuilder;
import network_model.Mote;
import network_model.NeighborEntry;
import network_model.NetworkModel;
import network_model.WSNManager;

public class MoteInfoFrame extends JFrame{
	public MoteInfoFrame(String moteID, Mote mote, NetworkModel model, WSNManager manager ){
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
		/*HashMap<String, NeighborEntry> neighbors = mote.getNeighborTable();
		NeighborEntry firstEntry = neighbors.values().iterator().next();
		Byte[] addressB = firstEntry.addr_64b;
		byte[] address = new byte[9];
		address[0]=(byte) 0xFF;
		for(int i = 0 ; i < address.length;i++){
			address[i+1] = addressB[i];
		}
		manager.send(new CoAPBuilder(model).getSerialPacket("GET", "b", mote.getID64(), address));
		System.out.println("b");
		*/
	}
}
