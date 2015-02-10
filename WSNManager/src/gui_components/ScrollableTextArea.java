package gui_components;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class ScrollableTextArea extends JScrollPane implements MouseListener
	{
		//a text area within a scroll pane
		private JTextArea _textArea = new JTextArea();
		private int pause =0;
		public static final int VERTICAL_SCROLLBAR_ALWAYS = 22;
		public static final int HORIZONTAL_SCROLLBAR_NEVER = 31;
		//set other constants
		public ScrollableTextArea(int vBar, int hBar)
			{
				this.setViewportView(_textArea);
				this.setVerticalScrollBarPolicy(vBar);
				setVisible(true);
				_textArea.setVisible(true);
				
				
			}
		
		public ScrollableTextArea(){
			this.setViewportView(_textArea);
			setVisible(true);
			_textArea.setVisible(true);
			_textArea.setEditable(false);
		}
		
		public void setText(String s)
					{
						_textArea.setText(s);	
					}
		public void append(String s)
			{
				_textArea.append(s);
				if(pause==0){
				_textArea.setCaretPosition(_textArea.getText().length());
				}else{
					
				}
			}

		public void setEditable(Boolean b)
			{
				_textArea.setEditable(b);
			}
		
		@Override
		public void setSize(int width, int height)
			{
				super.setSize(width, height);
				_textArea.setSize(width, height);
			}

		public DefaultCaret getCaret()
			{
				return (DefaultCaret) _textArea.getCaret();
			}
		
		public JTextArea getTextArea(){
			return _textArea;
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			pause=1;
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			pause=0;
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

	
	
	}
