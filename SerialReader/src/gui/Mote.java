package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

public class Mote extends JPanel implements MouseInputListener, FocusListener {
	
	private JLabel _moteID = new JLabel();
	private String _moteName;
	private NetworkCanvas _parent;
	private int _componentNumber;
	private RightClickMenu _menu;
	public Mote(int x, int y, NetworkCanvas parent, int componentNumber){
		_parent = parent;
		_componentNumber = componentNumber;
		this.setLocation(x, y);
		
		this.setBackground(Color.LIGHT_GRAY);
		addMouseMotionListener(this);
		addMouseListener(this);
		_moteID.setVisible(true);
		_moteID.setText("mote " + _componentNumber);
		_moteID.setLocation(2, 2);
		_moteID.setSize(_moteID.getPreferredSize());
		add(_moteID);
		setSize(80,30);
		setVisible(true);
		
		ActionListener l = createListener();
		_menu= new RightClickMenu(l,this);
		_menu.addOption("remove mote");
		//activate right click options
		setComponentPopupMenu(_menu);
		addFocusListener(this);
		setRequestFocusEnabled(true);
		
		
	}
	
	
	//action for the popup menu
	private ActionListener createListener() {
		ActionListener l = new ActionListener() {
  	      public void actionPerformed(ActionEvent event) {
  	        if(event.getActionCommand().equals("remove mote")){
  	        	//search hash map for this mote's id and remove it from the list
  	        	_parent.remove(_parent.moteMap().remove(_componentNumber));
  	        	repaint();
  	        }
  	      }

	
		};
  		return l;
	}
	
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		//_moteID.setText("mote:");
		requestFocus();
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		requestFocus();
		setLocation(e.getXOnScreen()-45, e.getYOnScreen()-55);
		repaint();
	}


	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void focusGained(FocusEvent arg0) {
		System.out.println(_componentNumber+" gained focus");
		setBorder(new LineBorder(Color.DARK_GRAY,2));
		
	}


	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(!_menu.isVisible()){
		System.out.println(_componentNumber+ " lost focus");
		setBorder(null);
		}
		
	}
}
