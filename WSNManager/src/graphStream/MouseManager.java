package graphStream;

import java.awt.event.MouseEvent;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.swingViewer.util.DefaultMouseManager;

public class MouseManager extends DefaultMouseManager{

	
	//prevent edge sprite from moving around
	@Override
	protected void elementMoving(GraphicElement element, MouseEvent event) {
		if(element instanceof GraphicSprite) return;
//		if(element instanceof GraphicNode){
//			element.addAttribute("layout.frozen");
//		}
		super.elementMoving(element,event);
	}
}
