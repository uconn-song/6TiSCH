package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class EchoClient extends Thread{
	@Override
	public void run(){
		try {
			DatagramSocket datagramSocket = 
				    new DatagramSocket(5000,Inet6Address.getByName("::1"));
			NetworkInterface ni = NetworkInterface.getByName("eth0");
			Socket socket = new Socket();
			//socket.bind);
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	
	