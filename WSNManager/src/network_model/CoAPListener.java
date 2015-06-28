package network_model;

import serial.DFrame;
import stack.CoapMessage;

public interface CoAPListener {
	//send D frame along to retrieve source mote information
public void handleCoAP(CoapMessage m, DFrame d);
}
