package network_model;


import java.util.Arrays;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import graphStream.NetworkGraph;
import gui.ControlPanel;
import gui.GUIManager;


import serial.SerialListener;
import serial.SerialThread;



import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import serial.SerialListener;
import serial.SerialThread;

/**
 *http://www.javaprogrammingforums.com/java-se-api-tutorials/5603-jssc-library-easy-work-serial-ports.html
 * java serial port communication module
 */
public class WSNManager{
	//In order to keep track of multiple serial connections, keep a hash map of portname ("COM7") as key and a pair {port,thread} as value
	private LBRConnection _LBRConnection;
	private ControlPanel _controlPanel;
	private GUIManager _guiManager;
	private  NetworkModel _networkModel = new NetworkModel();
	//hardcode network prefix as 1 1 1 1 1 1 1 1
	public static byte[] NETWORK_PREFIX = new byte[]{1,1,1,1,1,1,1,1};
	public static  byte[] ROOT_ID = new byte[8];
	public static String ROOT_ID_HEX="";
	public WSNManager() throws SerialPortException
	{
		_controlPanel = new ControlPanel(this);
		_guiManager = new GUIManager(_controlPanel,_networkModel,this);	
		_networkModel.addObserver(_guiManager.getGraph());
		
	}
  
	

	public static void main(String[] args) throws SerialPortException {  
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		//new NetworkInterfaceEnumerator().ListInterfaces();
    	new WSNManager();
    }
   
	public NetworkGraph getGraph(){
		return _guiManager.getGraph();
	}
	public NetworkModel getNetworkModel(){
		return _networkModel;
	}

	/**Send message to the DAG root
	 * @param message
	 * @return
	 */
	public boolean send(byte[] message){
		return _LBRConnection.send(message);
	}
	
	/** Open new connection on the given port name
	 * @throws SerialPortException 
	 */
	public void addConnection(String portName) throws SerialPortException{
			
		try{
			//if port we are trying to open is open ignore otherwise shut down port
			//which is currently open
			if(_LBRConnection.getPort().getPortName().equals(portName))
				return;
			_LBRConnection.ShutDown();
		}catch(NullPointerException e){
			//Throws if connection has not been started yet, which is fine
		}
			SerialPort p = new SerialPort(portName);
			p.openPort();
			p.setParams(115200,//115200 or 38400
			        SerialPort.DATABITS_8,
			        SerialPort.STOPBITS_1,
			        SerialPort.PARITY_NONE);
			SerialThread t = new SerialThread(p);
			t.start();
			_LBRConnection= new LBRConnection(p,t);
			//register components to listen to serial data
			addComponentsListeningOnSerial();
			
		
	}
	
	/****************************************************************
	 * Here is where we register components to listen to the data received from LBR
	 * This method is called when adding a connection to a serial port.
	 ***************************************************************/
	private void addComponentsListeningOnSerial() {
		_LBRConnection.addSerialListener("_controlPanel", _controlPanel);
		_LBRConnection.addSerialListener("Network Model", _networkModel);
		
		
	}
	
	
	public void addComponentListeningOnSerial(String id, SerialListener l){
		_LBRConnection.addSerialListener(id, l);
	}
	
	/**
	 * currently used for removing acknowledgment threads which have expired
	 * @param id
	 */
	public void removeComponentListeningOnSerial(String id){
		_LBRConnection.removeSerialListener(id);
	}
	public void closeConnection() {
		
		_LBRConnection.ShutDown();
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
	

	//==============Support Class===============
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
			
			public void addSerialListener(String componentName, SerialListener l){
				_thread.registerComponent(componentName, l);
			}
			
			public void removeSerialListener(String componentName){
				_thread.removeComponent(componentName);
			}
			
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
					_port = null;
					_thread = null;
				} catch (SerialPortException e) {
					
					//throws port not open exception does not matter if already closed  System.out
				}
			}
		}


		public void setRoot(byte[] b) {
			ROOT_ID = b;
			
			String rootid64Hex = "";
			for(int i = 0 ; i<b.length;i++){
				rootid64Hex = rootid64Hex +String.format("%2s", Integer.toHexString((b[i]&0xFF))).replace(' ','0');
			}
			ROOT_ID_HEX = rootid64Hex;
			_networkModel.setRoot(rootid64Hex);
			_guiManager.getGraph().addNode(rootid64Hex);
			
		}



		public void showGraph() {
			_guiManager.showGraph();
		}


		
}  