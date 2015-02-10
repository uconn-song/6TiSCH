package network_model;


import gui.ControlPanel;
import gui.GUIManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import serial.SerialThread;



import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 *http://www.javaprogrammingforums.com/java-se-api-tutorials/5603-jssc-library-easy-work-serial-ports.html
 * java serial port communication module
 */
public class WSNManager{
	//In order to keep track of multiple serial connections, keep a hash map of portname ("COM7") as key and a pair {port,thread} as value
	private LBRConnection _LBRConnection;
	private ControlPanel _controlPanel;
	public WSNManager() throws SerialPortException
	{
		_controlPanel = new ControlPanel(this);
		new GUIManager(_controlPanel);
		
	}
  
	public static void main(String[] args) throws SerialPortException {   
    	new WSNManager();
    }
    



	
	
		
		
		
		//Class to link thread which listens to and sends to LBR, and port which it is associated with
		private class LBRConnection{
			private SerialPort _port;
			private SerialThread _thread;
			public LBRConnection(SerialPort p, SerialThread t) {
				_port=p;
				_thread=t;
			}
			public SerialThread getThread() {
				return _thread;
			}
			
			/**
			 * 
			 * @param message as byte array
			 * @return
			 */
			public boolean send(byte[] b){
				return _thread.sendToBuffer(b);
			}
			public SerialPort getPort(){
				return _port;
			}
			private void ShutDown(){
				try {
					_thread.kill();
					_port.closePort();
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}
		}





		/**
		 * Send message to the DAG root
		 * @param message
		 * @return
		 */
		public boolean send(byte[] message){
			return _LBRConnection.send(message);
		}
	
		
		/** Open new connection on the given port name
		 */
		public void addConnection(String portName) {
			try {
				SerialPort p = new SerialPort(portName);
				p.openPort();
				setBaudrate(115200,p);
				SerialThread t = new SerialThread(p);
				t.start();
				_LBRConnection= new LBRConnection(p,t);
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}

		public void closeConnection() {
			_LBRConnection.ShutDown();
		}
		
		
		private void setBaudrate(int b, SerialPort p){
			
			try {
				p.setParams(b,//115200, //or 38400
				        SerialPort.DATABITS_8,
				        SerialPort.STOPBITS_1,
				        SerialPort.PARITY_NONE);
				
			} catch (SerialPortException e) {
				
			}	
		
		}
		
		/**
		 * return the port name of the currently open application
		 * @return
		 */
		public String getPortName() {
			try{
			return _LBRConnection.getPort().getPortName();
			}catch(NullPointerException e){
				return "No ports opened";
			}
		}
}



    