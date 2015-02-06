package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

public class RightClickMenu extends JPopupMenu implements FocusListener{
	ArrayList<JMenuItem> options = new ArrayList<JMenuItem>();
	private ActionListener _actionListener;
	private JComponent _mote;
	 
	    public RightClickMenu(ActionListener l,JComponent m){
	    	//add an action listener to listen to selections
	    	_actionListener = l;
	    	_mote = m;
	        addFocusListener(this);
	       
	    }
	    
	    public void addOption(String option){
	    	JMenuItem anItem;
	        anItem = new JMenuItem(option);
	        anItem.addActionListener(_actionListener);
	        add(anItem);
	    }
	    
	    
		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			_mote.requestFocus();
			
		}
	    
	   

}
