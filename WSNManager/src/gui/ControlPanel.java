package gui;


import graphStream.RPLGraph;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.graphstream.ui.swingViewer.Viewer;

import Acknowledgements.CoAPAckTimer;


import network_model.CoAPBuilder;
import network_model.Mote;
import network_model.NeighborEntry;
import network_model.WSNManager;

import serial.DFrame;
import serial.Frame;
import serial.SFrame;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import serial.SerialListener;
import stack.CoapMessage;
import stack.IPHC_Data;
import stack.UDP_Datagram;

public class ControlPanel extends JPanel implements ConsoleCommandListener, SerialListener{
	SerialPort _port;
	//holds a map with portname as key and a pair {port,thread} as value
	//private HashMap<String,PortListenerPair> _ports = new HashMap<String,PortListenerPair>();
	private Console _console;
	private ScrollableTextArea _outputTop;
	private ScrollableTextArea _outputBot;
	//handles communications to and from motes
	private WSNManager _connectionManager;
	private JComboBox<String> openPorts;

	
	public ControlPanel(WSNManager connectionManager){
		_connectionManager = connectionManager;
		guiInit();
	}

	private void guiInit() {
		GridBagLayout l = new GridBagLayout();
		setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
			c.weightx = 1.0;  c.weighty = 1.0;
			c.fill=  GridBagConstraints.BOTH;
			c.gridx=0;  c.gridy=0;
			c.gridwidth=4;
			c.ipady = 90;
			_outputTop = new ScrollableTextArea();
			_outputTop.getTextArea().addMouseListener(_outputTop);
		add(_outputTop,c);  //0,0
		
			c.gridx=0;	c.gridy=1;
			_outputBot = new ScrollableTextArea();
			_outputBot.getTextArea().addMouseListener(_outputBot);
		add(_outputBot,c);  //0,1
		
			_console = new Console(this);
			c.fill=  GridBagConstraints.BOTH;
			c.gridx=0;	c.gridy=2; c.gridwidth=3;
		add(_console,c);
		
		
		c.ipady=0;c.anchor = GridBagConstraints.SOUTH;
		JButton showGraph = new JButton("visualize network"); //x=3,y=3
		c.gridx=3;c.gridy=2;c.fill=GridBagConstraints.NONE;
		add(showGraph,c);
		
		showGraph.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_connectionManager.showGraph();
				}
		});
		
		
		
		c.gridwidth=1; c.ipady=0;
		
		c.weighty=0.0;
		c.anchor = GridBagConstraints.EAST;
		//label for following drop down menu
		JLabel portNameLabel = new JLabel("Port:"); //x=0, y=3
			portNameLabel.setForeground(Color.WHITE);
			c.gridx=0; c.gridy=3;
			c.fill=GridBagConstraints.NONE;
		add(portNameLabel,c);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.anchor=GridBagConstraints.WEST;
		//drop down menu of available port names
		String[] portNames = SerialPortList.getPortNames();
		openPorts = new JComboBox<String>(portNames); //x=1, y=3
			c.gridx=1; c.gridy=3;
		add(openPorts,c);

		//submit button
		c.gridx=2;c.gridy=3;
		JButton connect = new JButton("connect"); //x=2,y=3
		add(connect,c);
		
		
		connect.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					_connectionManager.addConnection((String) openPorts.getSelectedItem());
				} catch (SerialPortException e) {	_console.printString( e.getMessage());	}
			}
			});
		
		JButton refreshPorts = new JButton("refresh"); //x=3,y=3
		c.gridx=3;c.gridy=3;
		add(refreshPorts,c);
		
		
		refreshPorts.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//refresh the list of open ports
				String[] portNames = SerialPortList.getPortNames();
				openPorts.setModel(new DefaultComboBoxModel(portNames));
				//openPorts = new JComboBox<String>(portNames); //x=1, y=3
				}
		});
		
	
		
		
		revalidate();
		setVisible(true);
		this.setBackground(Color.DARK_GRAY);
		
		
		//try to listen on com port
		printSerialDevicePorts();
	
		
	}

	public void handleConsoleInput(String text) {
		try{
			if(text.equals("port status"))
			{
				printSerialDevicePorts();
				printOpenPorts();
			}
			else if(text.startsWith("listen "))
			{
				String portName = text.split(" ")[1];
				try {
					_connectionManager.addConnection(portName);
				} catch (SerialPortException e) {	_console.printString( e.getMessage());	}
			}
			else if(text.equals("refresh")){
				refreshGraph();
			}
			else if(text.equals("close connection"))
			{
				String openPort = _connectionManager.getPortName();
				if(!openPort.equals("No ports opened"))
				{
					_connectionManager.closeConnection();
				}else{
					_console.printString("No connections were open\n");
				}	
			}
			else if(text.equals("set root"))
			{
				setRoot();
			}else if(text.equals("DAG")){
				RPLGraph g =new RPLGraph("RPL", _connectionManager.getNetworkModel());
				g.display().setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
			
			}
			else if(text.equals("clear all")){
				_outputTop.setText("");
				_outputBot.setText("");
			} else if (text.startsWith("coap://"))
			{
				//String d = "coap://151592000016c076/i GET";
				handleCoAP(text);
			}else{
				_console.printString("Unrecognized Command {" + text+"}");
			}
		}catch(NullPointerException e){
		//	System.out.println(" Null Pointer Exception");
			printSerialDevicePorts();
			printOpenPorts();
		}catch(IllegalFormatException e){
			_console.printString(e.getMessage());
		}catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
			_console.printString("Error executing command {"+ text+"}");
		} 
	}
	
	
	/*********
	 * 
	 * 
	 * TODO
	 * 
	 */
	private void refreshGraph() {
		//build a list of neighbors to solicit new information from
		ArrayList<String> visitedNodes = new ArrayList<String>();
		String rootid = _connectionManager.getNetworkModel().getRootMote().getID64();
		Iterator<Mote> it = _connectionManager.getNetworkModel().getConnectedMotes();
		while(it.hasNext()){
			String id = it.next().getID64();
			if(!id.equals(rootid)){
				visitedNodes.add(id);
			}
			
		}
		
		for(int i =0;i<visitedNodes.size();i++){
			//send an acknowledgeable packet to each mote
			CoAPBuilder b = new CoAPBuilder(_connectionManager.getGraph(), _connectionManager.getNetworkModel(), "GET", "n", visitedNodes.get(i), null, 0);
			_connectionManager.send(b.getSerialPacket());
			//new CoAPAckTimer(5, 110, b.getMessageID(), b.getSerialPacket(), _connectionManager).run();
		}
	}

	private void setRoot() {
		byte[] data = new byte[]{(byte) "R".charAt(0),(byte)"Y".charAt(0),1,1,1,1,1,1,1,1};
		_connectionManager.send(data);
	}

	/**
	 * Accept a coap string with the form coap://[8B address in hex (16 characters)] /[resource path] GET
	 * TODO: add support for put
	 * @param text
	 */
	private void handleCoAP(String text) {
		// coap://151592000016c076/i GET
		
		String[] iidResourcePair = text.split("//")[1].split("/");
		String iid = iidResourcePair[0];
		//"141592000016c076"
		
		String resource = iidResourcePair[1].split(" ")[0];
		String method = iidResourcePair[1].split(" ")[1];
		CoAPBuilder message = new CoAPBuilder(_connectionManager.getGraph(), _connectionManager.getNetworkModel(),method, resource, iid, null,0);
		
		new CoAPAckTimer(5, 2000, message.getMessageID(), message.getSerialPacket(), _connectionManager).run();
    	//_connectionManager.send(message.getSerialPacket());
	}


	
	//print a list of ports which are active
	private void printOpenPorts(){
			_console.printString("listening on:");
			_console.printString(_connectionManager.getPortName());
	}
	
	//print a list of locations for currently attached devices
	private void printSerialDevicePorts() {
		_console.printString("all devices available:");
		String[] portNames = SerialPortList.getPortNames();
		if(portNames.length==0){
			_console.printString("No serial devices detected");
		}
        for(int i = 0; i < portNames.length; i++){
           _console.printString(portNames[i]);
        }
		
	}

	
	//This class implements SerialListener, this is where the serial frames are handled and printed
	@Override
	public void acceptFrame(Frame collectedFrame) {
		//Serial Frame level messages
		if(collectedFrame.getType().equals("Status")){
			_outputTop.append(collectedFrame.toString()+ "\n");
			SFrame f = (SFrame)collectedFrame;
			if(f._statusType.startsWith("9")){
				//Root neighbor entry
				NeighborEntry e = f.parseNeighbors();
			}else if(!SFrame.ROOT_SET&&f._statusType.startsWith("1"))
			{
				f.getRootPrefix(_connectionManager);
			}
			else if(!SFrame.ROOT_SET&& f._statusType.startsWith("0")){
				_connectionManager.showGraph();
				setRoot();
			}
		//Application Layer level messages
		}else if(collectedFrame.getType().equals("Data")){
			_outputTop.append(collectedFrame.toString()+"\n");
			
			//if CoAP message
			if(((DFrame)collectedFrame).isCoAPMessage()){
				
				//byte[] payloads = {'2'};
				// CoAPBuilder builders = new CoAPBuilder(_connectionManager.getGraph(),_connectionManager.getNetworkModel(), "PUT", "l", ((DFrame)collectedFrame).getSrcMoteId64Hex(), payloads,0);
				//_connectionManager.send(builders.getSerialPacket());
				
				
				
				CoapMessage m = ((DFrame)collectedFrame).getCoAPMessage();
				_outputBot.append("CoAP("+m.getMessageID()+"): ");
				byte[] b = m.getPayload();
				char c = (char)(b[0]&0xFF);
				
				if(c=='n'){	
					NeighborEntry e = new NeighborEntry(b);
					String s = e.getiid64Hex();
					if(s==null){
						s = "{empty}";
					}
					_outputBot.append("'n', Neighbor entry from " + ((DFrame)collectedFrame).getSrcMoteId64Hex() + ", row: " + e.row + ", reporting neighbor:" + s +"\n");
				}else if(c=='a'){
					NeighborEntry e = new NeighborEntry(b);
					_outputBot.append("'a' message, mote table updated" + "\n");
					byte[] payload = new byte[1];
					payload[0] = e.row;
					
					CoAPBuilder builder = new CoAPBuilder(_connectionManager.getGraph(),_connectionManager.getNetworkModel(), "PUT", "a", ((DFrame)collectedFrame).getSrcMoteId64Hex(), payload,0);
					
					
					_connectionManager.send(builder.getSerialPacket());
					
					
					_outputBot.append("ack replied to resource 'a' with payload ["+payload[0]+"]\n");
					
				}else if(c=='r'){
					_outputBot.append("'r' message, notification of mote removal" + "\n");
					NeighborEntry e = new NeighborEntry(b);
					_connectionManager.send(new CoAPBuilder(_connectionManager.getGraph(),_connectionManager.getNetworkModel(), "PUT", "r", ((DFrame)collectedFrame).getSrcMoteId64Hex(), new byte[]{e.row},0).getSerialPacket());
				}else{
					_outputBot.append("Raw payload:  " + 	m.getPayloadAsAscii()+ "\n");

					String s = "";
					for(int i = 0 ; i<m.getPayload().length;i++){
					s=s+ " " +	Integer.toBinaryString((	m.getPayload()[i]&0xFF));
					}
					_outputBot.append("binary "+ s + "\n");
				}
				_outputBot.append("\n");
			}
			
		}else if(collectedFrame.getType().equals("Request")){
			_outputTop.append(collectedFrame.toString()+ "\n");
		}
		else{
			_outputBot.append(collectedFrame.toString()+"\n");
		}
	}
	
	
	public static String byteToString(byte b){
		return String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ', '0');
	}
	/*
	private void customMessage() {
		JFrame f = new JFrame();
		f.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx=1.0;
		c.weighty=1.0;
		f.add(new CustomPacketBuilder(_connectionManager),c);
		f.pack();
		f.setVisible(true);
	}
	
	/*
	 * send an arbitrary message stored in a byte array to the LBR
	 * @param data
	 /
	private void sendToBuffer(byte[] message) {
		if(!_connectionManager.send(message))_console.printString("Buffer Full");
	}
	
	  	private void buildEchoPacket(String mess, String portName) {
		//size of message bytes = #characters + 1 for "S" flag
		byte[] data = new byte[mess.length()+1];
		data[0]=(byte) "S".charAt(0);			
		//convert string to byte[]
		for(int i = 0; i < mess.length();i++)
		{
			data[i+1]=(byte)mess.charAt(i);
		}
		sendToBuffer(data);
	}
	 */
}
