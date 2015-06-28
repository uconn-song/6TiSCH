package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class Server extends Thread {

	private int _port;

	public Server(int port) {
		_port = port;
	}

	@Override
	public void run() {

		try {
			
			ServerSocket serverSocket = new ServerSocket(_port);
			
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in2 = new BufferedReader(new InputStreamReader(
			                whatismyip.openStream()));
			String ip = in2.readLine();
			System.out.println(ip);
			// listen to clientSocket
			Socket clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
			String inputLine, outputLine = "";
			// Initiate conversation with client
			out.println("ok");

			while ((inputLine = in.readLine()) != null) {

				System.out.println(inputLine);
				if (inputLine.equals("Bye.")){
					
					clientSocket.close();
					serverSocket.close();
					break;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("connection closed");

	}

}
