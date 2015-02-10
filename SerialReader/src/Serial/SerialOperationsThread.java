package Serial;

import gui.ScrollableTextArea;
import gui.SerialPacketTester;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

//Useful JSSC...there's not much of a tutorial
//https://code.google.com/p/java-simple-serial-connector/wiki/jSSC_Start_Working

public class SerialOperationsThread extends Thread{
	private boolean alive = true;
	private SerialPort serialPort; //from JSSC
	
	//used to assist in deriving the frame which is stored in a
	//class implementing Frame, i.e. SFrame holds status information.
	private FrameParser _frameParser = new FrameParser();
	
	//A packet can only be sent for a very short amount of time after a request
	//frame has been recieved, so keep a buffer for the next packet to be sent.
	//this will be checked when a request packet is found.
	private byte[] buffer;
	private boolean bufferEmpty = true;
	
	//deals with the parsed frames
	private SerialPacketTester _outputHandler;
	
	public SerialOperationsThread(SerialPort serial, SerialPacketTester outputHandler) {
		_outputHandler = outputHandler;
		serialPort = serial;
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
	
	
	public void captureByte(Byte b) throws UnsupportedEncodingException, Exception
	{
		if (b!=126){  //byte is not 0x73 so add data
			_frameParser.addData(b);
		}else if(!_frameParser.isEmpty() && b==126){  //end of frame
				closeFrame();
		}
	}
	
	private void closeFrame() throws UnsupportedEncodingException, Exception {
		//build a frame from raw data
		Frame collectedFrame = _frameParser.parse();
		//metadata for debugging
		if(collectedFrame.getType().equals("Request")){
			checkBuffer();
			_outputHandler.acceptFrame(collectedFrame);
		}else{
			_outputHandler.acceptFrame(collectedFrame);
		}
		_frameParser = new FrameParser();
		
	}

	private void checkBuffer() {
		if(!bufferEmpty)
		{
			bufferEmpty = true;
			try {
				serialPort.writeBytes(buffer);
				System.out.println("wrote bytes");
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
		
	}

	//to kill thread from outside
	public void kill(){
		alive = false;
	}
	
	
	public boolean sendToBuffer(byte[] frame) {
		if(bufferEmpty){
			buffer = frame;
			bufferEmpty = false;
			return true;
		}
		return false;
	}
}
