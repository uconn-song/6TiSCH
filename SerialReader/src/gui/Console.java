package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;

//controller in the MVC setup
public class Console extends JPanel
	{
		private JTextField _console;
		private static ScrollableTextArea _output;
		private ScrollableTextArea _registersArea;
		private ScrollableTextArea _dataMem;
		private GridBagLayout _layout = new GridBagLayout();
		private SerialPacketTester _tester;
		public Console(SerialPacketTester tester)
			{
				_tester = tester;
				this.setLayout(_layout);
				this.setBackground(Color.DARK_GRAY);
				this.setVisible(true);
				setFocusable(true);
				
				GridBagConstraints c = new GridBagConstraints();
				//console output
				c.gridx=0;
				c.gridy=0;
				_output = new ScrollableTextArea(ScrollableTextArea.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollableTextArea.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				_output.setVisible(true);
				_output.setEditable(false);
				_output.setPreferredSize(new Dimension(650, 150));
				this.add(_output,c);
				
				
				c.gridy=1;
				c.fill = GridBagConstraints.HORIZONTAL;
				setupConsole(c);
				Vector<String> commands = new Vector<String>();
				commands.add("port status");
				commands.add("listen [PORT NAME]");
				commands.add("close [PORT NAME]");
				commands.add("close all");
				commands.add("echo_PORTNAME");
				commands.add("set baudrate [Integer Baudrate]");
				c.fill=GridBagConstraints.BOTH;
				c.gridx=1;
				c.gridy=0;
				c.ipadx = 40;
				c.gridheight=3;
				JList<String> j = new JList<String>(commands);
				ListListener l = new ListListener(j, _console);
				j.addListSelectionListener(l);
				add(j,c);
			
			}
		
		 //interpret commands
		 private void handleInput(String text)
				{
					if(text.contains("|"))
						{
							//allow pipelining of commands
							String[] commands = text.split("[|]");
							for(int i = 0 ; i < commands.length;i++)
								{
									
									printString("");
								}
							return;
						}
					
					if(text.equals("clear"))
						{
							_output.setText("");
						}
					else
						{
							_tester.handleConsoleInput(text);
						}
						
				}

		
		



		private void setupConsole(GridBagConstraints c)
			{
			
				 _console = new JTextField(){	
					 @Override	
						public void addNotify() {
					        super.addNotify();
					        requestFocusInWindow();
					    }};
				this.add(_console, c);	
				_console.setVisible(true);
				_console.setSize(650, 20);
				
				
				//event handling for key listener
				_console.addKeyListener(new KeyListener() {
					@Override
					public void keyPressed(KeyEvent e)
						{
							//when return is pressed 
							if(e.getKeyCode()==10)
								{
				                    String text = _console.getText();
				              
				                    		if(!text.equals("")){
				                    		printString(text);
				                    		handleInput(text);
				                    		}                		 
				                   
				                    _console.setText("");      
								}
							
					}//close keylistener

					@Override
					public void keyReleased(KeyEvent arg0){}
					
					@Override
					public void keyTyped(KeyEvent arg0){}
					
		        });
				
			}


		//print to the console
		 public void printString(String text)
			 {
			  _output.append(text);
			  _output.append("\n");
			 }
		 
		 
		

		


		public void update(int[] reg, Map<String, String> data)
			{
				_registersArea.setText("Registers:\n");
				
				for(int i = 0 ; i < reg.length;i++)
					{
						_registersArea.append("$"+i+" = "+ reg[i]+ "\n");
					}
				
				_dataMem.setText("Data Memory in use:\n");
				
				SortedSet<String> keys = new TreeSet<String>(data.keySet());
				for (String key : keys) { 
				   String value = data.get(key);
				   _dataMem.append(key + ": " + value + "\n");
				}
				
			}


		class ListListener implements ListSelectionListener{
			private JList<String> list;
			private JTextField console;
			public ListListener(JList<String> j, JTextField c){
				list = j;
				console =c;
				
			}
			@Override
			public void valueChanged(ListSelectionEvent e) {
				try{
				String selected = list.getSelectedValue();
				if(selected.startsWith("listen")){
					console.setText("listen ");
				}else if(selected.startsWith("set baud")){
					console.setText("set baudrate ");
				}else if(selected.startsWith("echo")){
					console.setText("echo_");
				}else if(selected.equals("close all")){
					console.setText("close all");
				}else if(selected.startsWith("close ")){
					console.setText("close ");
				}else{
					console.setText((String) list.getSelectedValue());
				}
			console.requestFocus();
			list.clearSelection();
				}catch(NullPointerException ex){
					
				}
			}
			
		}
	
	
	}



