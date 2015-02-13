package gui_components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import network_model.WSNManager;

public class CustomPacketBuilder extends JPanel {
	private JTextField _destinationPort = new JTextField();
	private JTextField _asciiByte = new JTextField();
	private JTextField _integerByte = new JTextField();
	private JTextField _binaryByte = new JTextField();
	private JTextField _hexByte = new JTextField();
	private JTextArea _message = new JTextArea();
	private JTextArea _messageHex = new JTextArea();
	private JTextArea _messageBin = new JTextArea();
	private WSNManager _manager;
	public CustomPacketBuilder(WSNManager m) {
		_manager = m;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//column 0, labels
		c.gridx=0;
		c.gridy=0;
		//add(new JLabel("Destination Port:"),c);
		c.gridy=1;
		add(new JLabel("Ascii Byte:"),c);
		c.gridy=2;
		add(new JLabel("Integer Byte:"),c);
		c.gridy=3;
		add(new JLabel("Binary Byte:"),c);
		c.gridy=4;
		add(new JLabel("Hex Byte:"),c);
		//column 1, byte inputs
		c.ipadx=25;
		c.gridx=1;
		c.gridy=0;
		//add(_destinationPort,c);
		c.gridy=1;
		add(_asciiByte,c);
		c.gridy=2;
		c.ipadx=40;
		add(_integerByte,c);
		c.gridy=3;
		c.ipadx=80;
		add(_binaryByte,c);
		c.gridy=4;
		add(_hexByte,c);
		
		c.gridx=2;
		c.gridy=0;
		c.gridheight=5;
		
		add(_message,c);
		
		setVisible(true);
		
		addListeners();
	}
	private void addListeners() {
		_asciiByte.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
					if(_asciiByte.getText().length()==1){
					_message.append((int)(char)_asciiByte.getText().charAt(0)+ " ");
					_asciiByte.setText("");
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
	
			
		_integerByte.addKeyListener(new KeyListener(){
	
			@Override
			public void keyPressed(KeyEvent arg0) {
				try{
				int toConvert = Integer.parseInt(_integerByte.getText());
				
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER && toConvert>=0 && toConvert <256){
					_message.append(toConvert + " ");
				}
				}catch(NumberFormatException e)
				{
					
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
			
		
		_binaryByte.addKeyListener(new KeyListener(){
		
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
					
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
		_hexByte.addKeyListener(new KeyListener(){
		
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
					
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
		
		_message.addKeyListener(new KeyListener(){
		
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
					String[] dataString = _message.getText().split(" ");
					byte[] data = new byte[dataString.length];
					for(int i = 0 ; i < data.length;i++){
						data[i] = (byte) Integer.parseInt(dataString[i]);
					}
					
					_manager.send(data);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
		}
			

}
