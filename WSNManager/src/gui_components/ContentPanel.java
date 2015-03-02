package gui_components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Almost the same thing as a JPanel just a "frame" for a content area on the
 * program
 * 
 * @author Marcus
 */
public class ContentPanel extends JPanel {
	public ContentPanel() {
		setLayout(new GridBagLayout());
		setVisible(true);
		this.setPreferredSize(new Dimension(400,600));
	}

	public void switchComponent(JComponent p) {
		//clear the panel
		try {
			this.remove(0);
		} catch (ArrayIndexOutOfBoundsException e) {}
		//add a new component which will fill the content panel
		GridBagConstraints fillArea = new GridBagConstraints();
		fillArea.fill = GridBagConstraints.BOTH;
		fillArea.weightx = 1.0;
		fillArea.weighty = 1.0;
		add(p, fillArea);
	}
}
