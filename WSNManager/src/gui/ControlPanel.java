package gui;

import gui_components.Console;
import gui_components.ConsoleCommandListener;
import gui_components.ScrollableTextArea;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.IllegalFormatException;

import javax.swing.JPanel;


import network_model.CoAPBuilder;
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

	
	public ControlPanel(WSNManager connectionManager){
		_connectionManager = connectionManager;
		guiInit();
	}

	private void guiInit() {
		GridBagLayout l = new GridBagLayout();
		setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill=  GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=0;
		c.ipady = 90;
		_outputTop = new ScrollableTextArea();
		_outputTop.getTextArea().addMouseListener(_outputTop);
		add(_outputTop,c);
		c.gridx=0;
		c.gridy=1;
		_outputBot = new ScrollableTextArea();
		add(_outputBot,c);
		_console = new Console(this);
		c.fill=  GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=2;
		add(_console,c);
		
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
		
    	_connectionManager.send(new CoAPBuilder().getSerialPacket(method, resource, iid, null));
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
			//_outputTop.append(collectedFrame.toString()+ "\n");
			SFrame f = (SFrame)collectedFrame;
			if(f._statusType.startsWith("9")){
				
				NeighborEntry e = f.parseNeighbors();
				//if(e.used==1)
				//_outputBot.append(e.getiid64Hex()+"\n");
			}else if(!SFrame.ROOT_SET&&f._statusType.startsWith("1")){
				f.getRootPrefix(_connectionManager);
			}
		//Application Layer level messages
		}else if(collectedFrame.getType().equals("Data")){
			_outputTop.append(collectedFrame.toString()+"\n");
			
			//if CoAP message
			if(((DFrame)collectedFrame).isCoAPMessage()){
				CoapMessage m = ((DFrame)collectedFrame).getCoAPMessage(); 
				byte[] b = m.getPayload();
				char c = (char)(b[0]&0xFF);
				
				if(c=='n'){
					NeighborEntry e = new NeighborEntry(b);
					String s = e.getiid64Hex();
					_outputBot.append(e.row + " " + s +"\n");
				}else if(c=='u'){
					_outputBot.append("mote table updated" + "\n");					
				}else{
					_outputBot.append(m.getPayloadAsAscii()+ "\n");
				}
			}
			
		}else if(collectedFrame.getType().equals("Request")){
			_outputTop.append(collectedFrame.toString()+ "\n");
		}
		else{
			_outputBot.append(collectedFrame.toString()+"\n");
		}
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
