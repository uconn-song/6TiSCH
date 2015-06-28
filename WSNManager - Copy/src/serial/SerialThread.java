package serial;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jssc.SerialPort;
import jssc.SerialPortException;

//Useful JSSC...there's not much of a tutorial
//https://code.google.com/p/java-simple-serial-connector/wiki/jSSC_Start_Working

public class SerialThread extends Thread{
	private boolean alive = true;
	private SerialPort serialPort;
	
	//HDLC assistant holds lower level functions for computing and checking crc conditions
	private HDLCAssistant _HDLC = new HDLCAssistant();
	
	//A packet can only be sent for a very short amount of time after a request
	//frame has been received, so keep a buffer for the next packet to be sent.
	//this will be checked when a request packet is found.
	private byte[] _writeBuffer;
	private Lock _writeBufferLock = new ReentrantLock();
	private boolean _writeBufferEmpty = true;
	private ArrayList<Byte> _readBuffer = new ArrayList<Byte>();
	
	//A list of components to which serial messages will be sent, these components implement the interface SerialListener
	private HashMap<String, SerialListener> _listeningComponents = new HashMap<String,SerialListener>();
	
	public SerialThread(SerialPort serial) {
		serialPort = serial;
	}
	
	/** Terminate the thread cleanly. */
	public void kill(){
		alive = false;
	}
	
	@Override
	public void run(){
		try {
			//eat bytes from the serial stream until full packet detected. captureByte will then
			//decode the frame. (Note that Bytes must be converted to unsigned bytes before printed
			//http://stackoverflow.com/questions/19061544/bitwise-anding-with-0xff-is-important )
	        while(alive){
	        	captureByte(serialPort.readBytes(1)[0]);
	        }
	        
		} catch (SerialPortException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		}
	
	/**
	 * Registering a class implementing SerialListener to receive data frames from the LBR, 
	 * need to give it a name to remove if necessary
	 * @param componentName
	 * @param Name of the component , class implementing SerialListener
	 */
	public void registerComponent(String componentName, SerialListener l){
		_listeningComponents.put(componentName, l);
	}
	
	/**
	 * remove a component listening on the serial input.
	 * @param componentName
	 */
	public void removeComponent(String componentName){
		_listeningComponents.remove(componentName);
	}
	
	
	//===========READ OPERATIONS===========
	//Check for beginning and end of packet flags, and fill up a buffer of serial
	//data.
	private void captureByte(Byte b) throws UnsupportedEncodingException, Exception
	{
		//Serial buffer size is no larger than 256 according to the
		//firmware
		if(_readBuffer.size()> 256) closeFrame();
		if (b!=126){  //byte is not 0x73 so add data
		    _readBuffer.add(b);
			
		}else if(!_readBuffer.isEmpty() && b==126){  //end of frame
				closeFrame();
		}
	}
	
	private void closeFrame() throws UnsupportedEncodingException, Exception {
		try{
		//build a frame from raw data
		Frame collectedFrame = parseInputBuffer();
		//metadata for debugging
		if(collectedFrame.getType().equals("Request")){
			_writeBufferLock.lock();
			checkBuffer();
			_writeBufferLock.unlock();
			//TODO:SEND FRAME TO UPPER LAYERS
		}else{
			//System.out.println(collectedFrame);
			Iterator<SerialListener> it = _listeningComponents.values().iterator();
			while(it.hasNext()){
				it.next().acceptFrame(collectedFrame);
			}
			//TODO: SEND FRAME TO UPPER LAYERS
		}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		_readBuffer = new ArrayList<Byte>();
	}

	/**
	 * Take the collected frame and construct an appropriate representation for its data.
	 * @return instance of Frame
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	private Frame parseInputBuffer() throws Exception, UnsupportedEncodingException
	{
		String type = new String(new byte[]{_readBuffer.get(0)}, "UTF-8");
		//build a frame based on the type flag
			switch(type){
			case "S":
				return new SFrame(_readBuffer);
			case "R":
				return new RFrame(_readBuffer);
			case "E":
				return new EFrame(_readBuffer);
			case "D":
				return new DFrame(_readBuffer);
			case "I":
				return new IFrame(_readBuffer);
			case "N":
				return new RootNeighborFrame(_readBuffer);
			default:
				System.out.println("Frame code "+ _readBuffer.get(0));
				
				_readBuffer.clear();
				throw(new Exception(type + " unsupported"));
			}		
	}
	
	
	//==========WRITE OPERATIONS=============
	
	/**
	 * called when the mote sends a "request" frame
	 */
	private void checkBuffer() {
		if(!_writeBufferEmpty)
		{
			_writeBufferEmpty = true;
			try {
				serialPort.writeBytes(_writeBuffer);
			} catch (SerialPortException e) {
				_writeBufferLock.unlock();//unlock to prevent lock from being stuck in writing state
				e.printStackTrace();
			}
		}
	}


	
	/**Take the message, package it with HDLC and sent to buffer to be read by mote.
	 * Note that this function expects the message to NOT have crc computed in the input.
 	 * @param byte[] frame
	 */
	public synchronized boolean sendToBuffer(byte[] frame) {
		//since multiple threads could potentially write at the same time we need synchronization
		
		byte[] toBuffer = _HDLC.packageBytes(frame);
		_writeBufferLock.lock();
		if(_writeBufferEmpty){
			_writeBuffer = toBuffer;
			_writeBufferEmpty = false;
		_writeBufferLock.unlock();
			return true;
		}
		_writeBufferLock.unlock();
		return false;
	}
}
