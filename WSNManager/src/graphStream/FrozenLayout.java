package graphStream;

import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class FrozenLayout extends SpringBox{
	public FrozenLayout(boolean b) {
		super(b);
	}

	@Override
	protected void chooseNodePosition(NodeParticle n0, NodeParticle n1) {
	
	}
}
