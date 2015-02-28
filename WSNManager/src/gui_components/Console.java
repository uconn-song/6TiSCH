package gui_components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
public class Console extends JPanel {
	private JTextField _consoleInput;
	private ScrollableTextArea _output;
	private ScrollableTextArea _registersArea;
	private ScrollableTextArea _dataMem;
	private GridBagLayout _layout = new GridBagLayout();
	private ConsoleReader _commandListener;

	public Console(ConsoleReader cl) {
		_commandListener = cl;
		this.setLayout(_layout);
		this.setBackground(Color.DARK_GRAY);
		this.setVisible(true);
		setFocusable(true);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		// console output
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=1.0;
		c.ipadx = 5;
		c.ipady = 5;
		c.weighty=1.0;
		c.insets = new Insets(5,5,5,5);
		_output = new ScrollableTextArea(
				ScrollableTextArea.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollableTextArea.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_output.setVisible(true);
		_output.setEditable(false);
		_output.setPreferredSize(new Dimension(550, 100));
		this.add(_output, c);

		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		setupConsole(c);
		Vector<String> commands = new Vector<String>();
		commands.add("port status");
		commands.add("listen [PORT NAME]");
		commands.add("close connection");
		commands.add("echo_PORTNAME");
		commands.add("custom packet");
		commands.add("set root");
		commands.add("coap://[16DigitHexDestination]/[RESOURCE] get");
		//commands.add("set baudrate [Integer Baudrate]");
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.ipadx = 20;
		c.gridheight = 3;
		JList<String> j = new JList<String>(commands);
		ListListener l = new ListListener(j, _consoleInput);
		j.addListSelectionListener(l);
		
		c.weightx=0;
		add(j, c);

	}

	// interpret commands
	private void handleInput(String text) {
		if (text.equals("clear")) {
			_output.setText("");
		} else {
			_commandListener.handleConsoleInput(text);
		}
	}

	private void setupConsole(GridBagConstraints c) {
		c.weighty=0.0;
		_consoleInput = new JTextField() {
			@Override
			public void addNotify() {
				super.addNotify();
				requestFocusInWindow();
			}
		};
		this.add(_consoleInput, c);
		_consoleInput.setVisible(true);
		_consoleInput.setSize(650, 20);

		// event handling for key listener
		_consoleInput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// when return is pressed
				if (e.getKeyCode() == 10) {
					String text = _consoleInput.getText();

					if (!text.equals("")) {
						printString(text);
						handleInput(text);
					}

					_consoleInput.setText("");
				}

			}// close keylistener

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

		});

	}

	// print to the console
	public void printString(String text) {
		_output.append(text);
		_output.append("\n");
	}

	public void update(int[] reg, Map<String, String> data) {
		_registersArea.setText("Registers:\n");

		for (int i = 0; i < reg.length; i++) {
			_registersArea.append("$" + i + " = " + reg[i] + "\n");
		}

		_dataMem.setText("Data Memory in use:\n");

		SortedSet<String> keys = new TreeSet<String>(data.keySet());
		for (String key : keys) {
			String value = data.get(key);
			_dataMem.append(key + ": " + value + "\n");
		}

	}

	class ListListener implements ListSelectionListener {
		private JList<String> list;
		private JTextField console;

		public ListListener(JList<String> j, JTextField c) {
			list = j;
			console = c;

		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			try {
				String selected = list.getSelectedValue();
				if (selected.startsWith("listen")) {
					console.setText("listen ");
				} else if (selected.startsWith("coap://")){
					console.setText("coap://");
				}else if(selected.equals("set root")){
					console.setText("set root");
				}else if (selected.startsWith("set baud")) {
					console.setText("set baudrate ");
				}else if (selected.startsWith("echo")) {
					console.setText("echo_");
				}else if (selected.equals("close all")) {
					console.setText("close all");
				}else if (selected.startsWith("close ")) {
					console.setText("close connection");
				}else {
					console.setText((String) list.getSelectedValue());
				}
				console.requestFocus();
				list.clearSelection();
			} catch (NullPointerException ex) {

			}
		}

	}

}
