package Testing;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class SerialTester extends Thread{
	private boolean alive = true;
	private SerialPort serialPort;
	public SerialTester(SerialPort serial) {
		serialPort = serial;
	}
	@Override
	public void run(){
        try {
        	//Open port
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_115200,//115200, //or 38400
                             SerialPort.DATABITS_8,
                             SerialPort.STOPBITS_1,
                             SerialPort.PARITY_NONE);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //Writes data to port
	        //serialPort.writeBytes("Test");
        
	        while(alive){
	        	byte[] buffer = serialPort.readBytes(1);
	       
		        for( int i = 0 ; i < buffer.length;i++){
		        	//System.out.print(String.format("%16s", Integer.toBinaryString(buffer[i])).replace(' ', '0'));
		        	System.out.println(Integer.toHexString(buffer[i]));
		        }
	        }
	        
	        //Closing the port
	        serialPort.closePort();
	        
        } catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	public void kill(){
		alive = false;
	}     
}
