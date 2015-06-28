package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Almost the same thing as a JPanel just a "frame" for a content area on the
 * program
 * 
 */
public class FormattedJPanel extends JPanel {
	GridBagConstraints fillArea = new GridBagConstraints();
	public FormattedJPanel(int preferredWidth, int preferredHeight) {
		setLayout(new GridBagLayout());
		setVisible(true);
		this.setPreferredSize(new Dimension(preferredWidth,preferredHeight));
		fillArea.fill = GridBagConstraints.BOTH;
		fillArea.weightx = 1.0;
		fillArea.weighty = 1.0;
	}

	public void switchComponent(JComponent p) {
		//clear the panel
		try {
			this.remove(0);
		} catch (ArrayIndexOutOfBoundsException e) {}
		//add a new component which will fill the content panel
		add(p, fillArea);
	}
}
