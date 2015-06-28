package Acknowledgements;

import java.util.Timer;
import java.util.TimerTask;

import network_model.WSNManager;
import serial.DFrame;
import serial.Frame;
import serial.SerialListener;
import stack.CoapMessage;

public class CoAPAckTimer implements Runnable, SerialListener {
	private int _retryLimit;
	private int _retryPeriod;
	private int _CoAPMessageID;
	private byte[] _CoAPPacketBytes;
	private WSNManager _manager;
	Timer t = new Timer();
	private boolean _ackReceived=false;
	
	/**
	 * 
	 * @param retryLimit the maximum number of times to send the message if no ack received
	 * @param retryPeriod the time between resending message
	 * @param CoAPMessageID the messageID of the CoAP message sent, the ack packet is expected to have the
	 * same message ID.
	 * @param CoAPPacketBytes the message originally sent, used for retransmission of packet on failure to receive the ack by timeout
	 */
	public CoAPAckTimer(int retryLimit, int retryPeriod, int CoAPMessageID, byte[] CoAPPacketBytes, WSNManager manager){
		//assume that the initial CoAP packet has been sent, this is merely for retransmission and ACK detection
		_retryLimit = retryLimit;
		_retryPeriod = retryPeriod;
		_CoAPMessageID = CoAPMessageID;
		_CoAPPacketBytes = CoAPPacketBytes;
		_manager = manager;
	
		//
		
	}
	
	 class AckCheckTask extends TimerTask {
		 int retryCount =0;
	        public void run() {
	        	if(_ackReceived){
					_manager.removeComponentListeningOnSerial(_CoAPMessageID+"");
					t.cancel();
					return;
				}
	        	System.out.println("CoAP message not acknoweledged, retransmitting("+retryCount+"), " + _CoAPPacketBytes.length + " bytes transmitted");
	        	_manager.send(_CoAPPacketBytes);
	        	
	        	retryCount++;
				if(retryCount>_retryLimit){
					_manager.removeComponentListeningOnSerial(_CoAPMessageID+"");
					//System.out.println("Message failed to acknowledge after" + _retryLimit + " tries");
		            t.cancel(); //Terminate the timer thread
				}
	            
	        }
	    }
	 
	@Override
	public void run() {	
		System.out.println(_CoAPMessageID);
			//add this to the list of listeners to allow 
		_manager.addComponentListeningOnSerial(_CoAPMessageID+"", this);
		t.scheduleAtFixedRate(new AckCheckTask(), 100, _retryPeriod);
	}
	
	@Override
	public void acceptFrame(Frame collectedFrame) {
		if(!collectedFrame.getType().equals("Data")) return;
		
		if(((DFrame)collectedFrame).isCoAPMessage()){
			CoapMessage m = ((DFrame)collectedFrame).getCoAPMessage(); 
			System.out.println("Checking CoAP messageID for recieved message: " + m.getMessageID() + 
					"\n\t against messageID to be acked: " + _CoAPMessageID);
			if(m.getMessageID() == _CoAPMessageID){
				System.out.println("ack received, removing timer, stop retransmissions.");
				_ackReceived=true;
			}
		}
	}
	
}
