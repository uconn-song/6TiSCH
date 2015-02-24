package network_model;


import gui.ControlPanel;
import gui.GUIManager;
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
	public WSNManager() throws SerialPortException
	{
		_controlPanel = new ControlPanel(this);
		new GUIManager(_controlPanel);
		
		
		String[] ports = SerialPortList.getPortNames();
		if(ports.length==1){
			addConnection(ports[0]);
		}
	}
  
	public static void main(String[] args) throws SerialPortException
    {
        new WSNManager();
    	//new EchoClient().start();
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
			_LBRConnection.ShutDown();
		}catch(NullPointerException e){//ensure no connection open
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
	
	/**
	 * Here is where we register components to listen to the data received from LBR
	 * This method is called when adding a connection to a serial port.
	 */
	private void addComponentsListeningOnSerial() {
		_LBRConnection.addSerialListener("_controlPanel", _controlPanel);
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
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}
		}
}  