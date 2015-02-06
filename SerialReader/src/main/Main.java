package main;

import gui.SerialPacketTester;
import gui.VisualizerFrame;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import Serial.*;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 *http://www.javaprogrammingforums.com/java-se-api-tutorials/5603-jssc-library-easy-work-serial-ports.html
 * java serial port communication module
 */
public class Main{

	public Main() throws SerialPortException
	{
		//Testing.CRCTester.crc();
		
		//Serial Testing
		startSerialDebugger();
		//GUI Testing
		//new VisualizerFrame();
		
		

	}
  
	public static void main(String[] args) throws SerialPortException {   
    	new Main();
    }
    


	public void startSerialDebugger() throws SerialPortException{
    	
		
        //In the constructor pass the name of the port with which we work
       
    
		JFrame f = new JFrame();
		f.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx=0;
		c.gridy=0;
		//c.ipadx = 600;
		//c.ipady = 500;
		c.fill = GridBagConstraints.BOTH;
		c.insets= new Insets(10,10,10,10);
		f.getContentPane().add(new SerialPacketTester(),c);
		f.getContentPane().setBackground(Color.DARK_GRAY);
		f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}



    