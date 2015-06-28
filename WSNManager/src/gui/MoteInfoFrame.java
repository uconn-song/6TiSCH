package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import network_model.CoAPBuilder;
import network_model.Mote;
import network_model.NeighborEntry;
import network_model.NetworkModel;
import network_model.WSNManager;

public class MoteInfoFrame extends JFrame implements ComponentListener,KeyListener{
	
	private JPanel context = new JPanel();
	private JTextField resourceEndpointInput = new JTextField();
	private JTextField payloadAscii = new JTextField();
	private JComboBox<String> CoAPMethodSelector = new JComboBox<String>();
	private WSNManager _manager;
	private String _moteID;
	private Mote _mote;
	private ScrollableTextArea area = new ScrollableTextArea();
	
	//timer to refresh history of messages sent by this mote
	private Timer refreshTimer;
	
	public MoteInfoFrame(String moteID, Mote mote, WSNManager manager ){
		super(moteID);
		addComponentListener(this);
		_manager = manager;
		_moteID = moteID;
		_mote = mote;
		//initialize background JPanel
		context.setVisible(true);
		context.setLayout(new GridBagLayout());
		add(context);
		
		//ContentPanel p = new ContentPanel();
		
		area.setPreferredSize(new Dimension(500,500));
		//p.switchComponent(area);
		String[] choices = {"GET","PUT"};
		CoAPMethodSelector = new JComboBox<String>(choices);
		
		
		//submit button
		JButton sendQuery = new JButton("send");
		sendQuery.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {


			
				String endpoint = resourceEndpointInput.getText();
				byte[] payload=null;
				if(!payloadAscii.getText().equals("")){
					try {
						payload = payloadAscii.getText().getBytes("US-ASCII");
					} catch (UnsupportedEncodingException e) {}
				}
				_manager.send(new CoAPBuilder(_manager.getGraph(), _manager.getNetworkModel(), (String) CoAPMethodSelector.getSelectedItem(), endpoint , _moteID, payload,0).getSerialPacket());
			
			
			}
		});
		
//		JButton refreshHistory = new JButton("Refresh History");
//		
//		refreshHistory.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//			area.setText(_mote.getMessagesSentByThisMote());
//			}
//		});
		
		
		
		
		area.setText(_mote.getMessagesSentByThisMote());
		
		//add a box to query path
		add(new JLabel("Resource Endpoint:"),0,0,0,0,1);
		
		add(CoAPMethodSelector,1,0,0,0,1);
		add(resourceEndpointInput,2,0,0,0,1);
		add(payloadAscii,3,0,0,0,1);
		resourceEndpointInput.setColumns(30);
		resourceEndpointInput.addKeyListener(this);
		payloadAscii.setColumns(30);
		payloadAscii.addKeyListener(this);
		add(sendQuery,4,0,0,0,1);
		add(area,0,1,0,0,4);
		//add(refreshHistory,0,2,0,0,2);
		pack();
		
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
		
		//Timer related
		refreshTimer = new Timer(1000,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				area.setText(_mote.getMessagesSentByThisMote());
			}
		});
		refreshTimer.start();
	}
	
	/**
	 * 
	 * @param c component to add
	 * @param x gridx 
	 * @param y gridy
	 * @param padx
	 * @param pady
	 */
	private void add(JComponent toAdd, int x, int y, int padx, int pady, int gridwidth) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=x;
		c.gridy=y;
		c.ipadx=padx;
		c.ipady=pady;
		c.gridwidth=gridwidth;
		c.insets= new Insets(4,4,4,4);
		c.anchor = GridBagConstraints.WEST;
		context.add(toAdd,c);
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		refreshTimer.stop();
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		refreshTimer.start();
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		//System.out.println(_mote.getDagRank());
		//System.out.println(_mote.getPreferredParent().DAGrank + " " +_mote.getPreferredParent().getiid64Hex());
		if(arg0.getKeyCode()==KeyEvent.VK_ENTER&&!resourceEndpointInput.getText().equals("")){
			String endpoint = resourceEndpointInput.getText();
			byte[] payload=null;
			if(!payloadAscii.getText().equals("")){
				try {
					payload = payloadAscii.getText().getBytes("US-ASCII");
				} catch (UnsupportedEncodingException e) {}
			}
			_manager.send(new CoAPBuilder(_manager.getGraph(), _manager.getNetworkModel(), (String) CoAPMethodSelector.getSelectedItem(), endpoint , _moteID, payload,0).getSerialPacket());
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
