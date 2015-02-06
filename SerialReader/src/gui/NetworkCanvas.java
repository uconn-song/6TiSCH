package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;


public class NetworkCanvas extends JPanel implements ActionListener {
	private  ArrayList<Mote> _motes= new ArrayList<Mote>();
	private HashMap<Integer,Mote> _moteMap = new HashMap<Integer,Mote>();
	private int _componentNumber = 0; //for uniquely identifying components for the hashmap
	
	
	public NetworkCanvas()
	{
		this.setPreferredSize(new Dimension(700,500));
		setVisible(true);
		
		
		ActionListener l = this;
		RightClickMenu rm = new RightClickMenu(l,this);
		rm.addOption("add new Mote");
		setComponentPopupMenu(rm);//add the action listener
		setLayout(null);
		 
	}
	
	
	

	
	 public void actionPerformed(ActionEvent event) {
	        if(event.getActionCommand().equals("add new Mote")){
	        	
	        	Mote m =new Mote(getMousePosition().x,getMousePosition().y,this,_componentNumber);
	        	_motes.add(m);
	        	_moteMap.put(_componentNumber, m);
	        	add(m);
	        	m.requestFocus();
	        	repaint();
	        	_componentNumber++;
	        }
	      }
	 
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getPreferredSize().width, getPreferredSize().height);
		paintConnections(g2);
	}
	
	
	//override to make sure panel is redrawn when component is removed
	@Override
	public void remove(Component c){
		super.remove(c);
		repaint();
	}

	public int moteCount(){
		return _motes.size();
	}
	
	public HashMap<Integer, Mote> moteMap(){
		return _moteMap;
	}
	
	private void paintConnections(Graphics g) {
		// TODO Auto-generated method stub
		
	}
}
