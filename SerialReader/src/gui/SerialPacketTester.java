package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;


import Serial.Frame;
import Serial.HDLC;
import Serial.SerialOperationsThread;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class SerialPacketTester extends JPanel {
	SerialPort _port;
	//holds a map with portname as key and a pair {port,thread} as value
	private HashMap<String,PortListenerPair> _ports = new HashMap<String,PortListenerPair>();
	private Console _console;
	private ScrollableTextArea _outputTop;
	private ScrollableTextArea _outputBot;

	
	public SerialPacketTester(){
		guiInit();
	}

	private void guiInit() {
		
		this.setMinimumSize(new Dimension(700,500));
		GridBagLayout l = new GridBagLayout();
		setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;
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
	}

	public void handleConsoleInput(String text) {
		try{
			if(text.equals("port status"))
			{
				serialDevicePorts();
				printOpenPorts();
			}
			else if(text.startsWith("listen ")){
				String portName = text.split(" ")[1];
				SerialPort p = openPort(portName);	
				SerialOperationsThread t = new SerialOperationsThread(p, this);
				t.start();
				//now we have a port which is opened and a thread which is listening
				_ports.put(portName, new PortListenerPair(p,t));
			}
			//need to add port to the following commands
			else if(text.startsWith("set baudrate "))
			{
				int baud = Integer.parseInt(text.split(" ")[2]);
				SerialPort p = _ports.get(text.split(" ")[3]).getPort();
				setBaudrate(baud,p);
			}
			else if(text.startsWith("close "))
			{
				if(text.equals("close all"))
				{
					closeAllPorts();
				}
				String portName = text.split(" ")[1];//COM2 for example
				_ports.get(portName).ShutDown();
				_ports.remove(portName);
			}
			else if(text.startsWith("echo_")){
				// echo_COM7 hello, retrieve COM7
				String portName = text.split(" ")[0].split("_")[1];
				buildEchoPacket(text.substring(text.indexOf(' ')+1),portName);
			}
			else if(text.equals("c")){
				customMessage();
			}else if(text.equals("clear all")){
				_outputTop.setText("");
				_outputBot.setText("");
			}
			else{
				_console.printString("Unrecognized Command {" + text+"}");
			}
		}catch(NullPointerException e){
			serialDevicePorts();
			printOpenPorts();
		}catch(IllegalFormatException e){
		
			_console.printString(e.getMessage());
		}catch(NumberFormatException e) {
            _console.printString("Error executing command {" + text + "}");
        }catch(ArrayIndexOutOfBoundsException e){
            _console.printString("Error executing command {" + text+"}");
		} catch (SerialPortException e) {
			_console.printString(e.getExceptionType() + ": " + e.getMessage());
		}
	}
	private void customMessage() {
		JFrame f = new JFrame();
		f.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx=1.0;
		c.weighty=1.0;
		f.add(new CustomPacketBuilder(this),c);
		f.pack();
		f.setVisible(true);
		
	}

	private void closeAllPorts() {
		Iterator<PortListenerPair> it = _ports.values().iterator();
		while(it.hasNext()){
			it.next().ShutDown();
		}
		_ports = new HashMap<String,PortListenerPair>();
	}

	//print a list of ports which are active
	private void printOpenPorts(){
		
			Iterator<String> it = _ports.keySet().iterator();
			_console.printString("listening on:");
			while(it.hasNext()){
				_console.printString(it.next());
			}
		
	}
	//print a list of locations for currently attached devices
	private void serialDevicePorts() {
		_console.printString("all devices available:");
		String[] portNames = SerialPortList.getPortNames();
		if(portNames.length==0){
			_console.printString("No devices detected on COM ports");
		}
        for(int i = 0; i < portNames.length; i++){
           _console.printString(portNames[i]);
        }
		
	}

	
	
	
	//open a port with the given name (COM5) and add it to a map of ports
	private SerialPort openPort(String portName) throws SerialPortException {
			SerialPort p = new SerialPort(portName);
			p.openPort();
			setBaudrate(115200,p);
			_console.printString("port " + portName + " opened");
			return p;
		
	}

	private void buildEchoPacket(String mess, String portName) {
		
		byte[] data = new byte[mess.length()+1];
		data[0]=(byte) "S".charAt(0);
		for(int i = 0; i < mess.length();i++)
		{
			data[i+1]=(byte)mess.charAt(i);
		}
		byte[] frame = HDLC.packageBytes(data);
		
		SerialOperationsThread t = _ports.get(portName).getThread();
		sendToBuffer(frame,t);
		
	}
	/**
	 * send an arbitrary message stored in a byte array to given port name
	 * @param data
	 * @param portName
	 */
	public void send(byte[] data, String portName) {
		try{
		byte[] frame = HDLC.packageBytes(data);
		SerialOperationsThread t = _ports.get(portName).getThread();
		sendToBuffer(frame,t);
		}catch(NullPointerException e){
			_console.printString(portName + e.getMessage());
		}
		
		
	}

	private void sendToBuffer(byte[] frame, SerialOperationsThread t) {
		if(!t.sendToBuffer(frame))
		{
			_console.printString("Buffer Full");
		}
		
		
	}

	
	//set baudrate for a specified port
	public void setBaudrate(int b, SerialPort p){
		
		try {
			p.setParams(b,//115200, //or 38400
			        SerialPort.DATABITS_8,
			        SerialPort.STOPBITS_1,
			        SerialPort.PARITY_NONE);
			_console.printString("Baudrate set to " + b);
		} catch (SerialPortException e) {
			
			_console.printString(e + " Baudrate unchanged");
		}	
	
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
	
	
	//Class to link thread and port
	private class PortListenerPair{
		private SerialPort _port;
		private SerialOperationsThread _thread;
		public PortListenerPair(SerialPort p, SerialOperationsThread t) {
			_port=p;
			_thread=t;
		}
		public SerialOperationsThread getThread() {
			return _thread;
		}
		private SerialPort getPort(){
			return _port;
		}
		private void ShutDown(){
			_thread.kill();
			try {
				_port.closePort();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
		
	}
}
