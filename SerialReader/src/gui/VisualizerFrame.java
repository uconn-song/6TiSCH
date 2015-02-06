package gui;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
public class VisualizerFrame extends JFrame{
	public VisualizerFrame(){
		NetworkCanvas canv = new NetworkCanvas();
		getContentPane().add(canv);
		setPreferredSize(canv.getPreferredSize());
		pack();
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);
	}
}
