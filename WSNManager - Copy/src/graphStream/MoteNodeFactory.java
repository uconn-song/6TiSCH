package graphStream;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;


public class MoteNodeFactory implements NodeFactory<MoteNode>{

	@Override
	public MoteNode newInstance(String id, Graph graph) {
		return new MoteNode((AbstractGraph) graph,id);
	
	}

}
