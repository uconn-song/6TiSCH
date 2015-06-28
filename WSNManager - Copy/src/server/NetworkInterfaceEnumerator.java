package server;
import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;
public class NetworkInterfaceEnumerator {
	


	   public void ListInterfaces(){
			try {
	        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netint : Collections.list(nets))
					displayInterfaceInformation(netint);
				} catch (SocketException e) {
					e.printStackTrace();
				}
	   }
	    

	    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
	        out.printf("Display name: %s\n", netint.getDisplayName());
	        out.printf("Name: %s\n", netint.getName());
	        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	            out.printf("InetAddress: %s\n", inetAddress);
	        }
	        out.printf("\n");
	     }
	}  

