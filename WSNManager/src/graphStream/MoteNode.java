package graphStream;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.MultiNode;
import org.graphstream.graph.implementations.SingleNode;

public class MoteNode extends MultiNode {
	protected MoteNode(AbstractGraph graph, String id) {
		super(graph, id);
		super.addAttribute("ui.label",id);
		//addAttribute("ui.text-size",20);
	}
}
