package gui;

import gui_components.Console;
import gui_components.ConsoleReader;
import gui_components.CustomPacketBuilder;
import gui_components.ScrollableTextArea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.IllegalFormatException;

import javax.swing.JFrame;
import javax.swing.JPanel;


import network_model.WSNManager;

import serial.Frame;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import serial.SerialListener;
import stack.CoapMessage;
import stack.IPHC_Data;
import stack.UDP_Datagram;

public class ControlPanel extends JPanel implements ConsoleReader, SerialListener{
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
			else if(text.startsWith("listen ")){
				String portName = text.split(" ")[1];
				
				//now we have a port which is opened and a thread which is listening
			
				try {
					_connectionManager.addConnection(portName);
				} catch (SerialPortException e) {
					_console.printString( e.getMessage());
				}
				
			}
			//need to add port to the following commands
			else if(text.startsWith("set baudrate "))
			{
				_console.printString("not yet implemented");
				//int baud = Integer.parseInt(text.split(" ")[2]);
				//SerialPort p = _ports.get(text.split(" ")[3]).getPort();
				//setBaudrate(baud,p);
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
			}else if(text.startsWith("echo_")){
				String portName = text.split(" ")[0].split("_")[1];
				buildEchoPacket(text.substring(text.indexOf(' ')+1),portName);
			}
			else if(text.equals("custom packet")){
				customMessage();
			}else if(text.equals("clear all")){
				_outputTop.setText("");
				_outputBot.setText("");
			} else if (text.startsWith("coap://"))
			{
				//String d = "coap://151592000016c076/i GET";
				handleCoAP(text);
			}
			else{
				_console.printString("Unrecognized Command {" + text+"}");
			}
		}catch(NullPointerException e){
			printSerialDevicePorts();
			printOpenPorts();
		}catch(IllegalFormatException e){
		
			_console.printString(e.getMessage());
		}catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
			_console.printString("Error executing command {" + text+"}");
		} 
	}
	private void handleCoAP(String text) {
		// coap://151592000016c076/i GET
		
		String[] iidResourcePair = text.split("//")[1].split("/");
		String iid = iidResourcePair[0];
		//"141592000016c076"
		
		String resource = iidResourcePair[1].split(" ")[0];
		String method = iidResourcePair[1].split(" ")[1];
		CoapMessage m = new CoapMessage(method,resource, iid);
		
		//m.printRaw(m.getMessage());
		UDP_Datagram d = new UDP_Datagram(m);
		//d.printRaw(d.getMessage());
		IPHC_Data hc = new IPHC_Data(d);
		hc.printRaw(hc.getMessage());
		
		//now we have iphc all we need to do is append 64 bit address and send
		byte[] hcArr = hc.getMessage();
		byte[] dest = hc.destAddr64;
		byte[] finalMessage = new byte[hcArr.length+9];
		finalMessage[0] = (byte) ("D".charAt(0)&0xFF);
		for(int i = 0;i<8;i++){
			finalMessage[i+1] = dest[i];
		}
    	for(int i = 0; i< hcArr.length;i++){
    		finalMessage[i+9] = hcArr[i];
    	}
    	_connectionManager.send(finalMessage);
		
	}

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
	

	
	/**
	 * send an arbitrary message stored in a byte array to the LBR
	 * @param data
	 */
	private void sendToBuffer(byte[] message) {
		if(!_connectionManager.send(message))_console.printString("Buffer Full");
	}

	//Receive from Mote
	public void acceptFrame(Frame collectedFrame) {
		if(collectedFrame.getType().equals("Status")){
			_outputTop.append(collectedFrame.toString()+ "\n");
		}else if(collectedFrame.getType().equals("Data")){
			_outputBot.append(collectedFrame.toString()+"\n");
		}else if(collectedFrame.getType().equals("Request")){
			_outputTop.append(collectedFrame.toString()+ "\n");
		}
		else{
			_outputBot.append(collectedFrame.toString()+"\n");
		}
	}
	
}
