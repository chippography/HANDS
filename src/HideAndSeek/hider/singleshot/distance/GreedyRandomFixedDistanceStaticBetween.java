package HideAndSeek.hider.singleshot.distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import HideAndSeek.seeker.SeekingAgent;

/**
 * @author Martin
 *
 */
public class GreedyRandomFixedDistanceStaticBetween extends GreedyRandomFixedDistance {

	/**
	 * @param graph
	 */
	public GreedyRandomFixedDistanceStaticBetween(GraphController <StringVertex, StringEdge> graphController, int hideLocations) {

		super(graphController, hideLocations);

	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#startNode()
	 */
	@Override
	public StringVertex startNode() {

		return currentNode();
		
	}

}
