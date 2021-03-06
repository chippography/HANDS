package HideAndSeek.hider.singleshot.cost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;

/**
 * A hider who's tendency to choose cheap edges over random edges
 * is manually set.
 * 
 * Relies on edges being of variable weight.
 * 
 * @author Martin
 */
public class FixedStartVariableGreedy extends VariableGreedy {

	
	/**
	 * @param graphController
	 * @param numberOfHideLocations
	 * @param bias
	 */
	public FixedStartVariableGreedy(
			GraphController <StringVertex, StringEdge> graphController,
			int numberOfHideLocations, double bias) {
		super(graphController, numberOfHideLocations, bias);
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#startNode()
	 */
	@Override
	public StringVertex startNode() {

		return firstNode();
		
	}

}
