package HideAndSeek.hider.repeatgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import HideAndSeek.hider.Hider;

/**
 * A hider who's tendency to choose pre-explored nodes (cheaper)
 * over unexplored nodes is manually set.
 * 
 * Relies on EITHER edges being of variable weight OR the % cost 
 * reduction for exploring an edge to be greater than 0.
 * 
 * @author Martin
 */
public class VariableBiasHider extends Hider {

	/**
	 * Hider in which the tendency to take pre-traversed edges
	 * can be set.
	 * 
	 * Hides in first K locations.
	 * 
	 * 
	 * @param graph
	 * @param numberOfHideLocations
	 * @param bias
	 */
	public VariableBiasHider(
			GraphController <StringVertex, StringEdge> graphController,
			int numberOfHideLocations, double bias) {
		super(graphController, numberOfHideLocations);
		
		setBias(bias);
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#hideHere(HideAndSeek.graph.StringVertex)
	 */
	@Override
	protected boolean hideHere(StringVertex vertex) {
		
		return true;
		
	}

	/**
	 *  Percentage of original cost that an edge must reach in order
	 *  to be considered 'well traversed'
	 */
	protected final static double WELLTRAVERSEDPERCENTAGE = 0.5;
	
	/**
	 * A percentage of the maximum value below which an edge weight must 
	 * fall under to be considered a cheap edge
	 */
	protected final static double CHEAPTHRESHOLD = 0.5;
	
	/**
	 * 
	 */
	protected double tendencyToBias = 0.0;
	
	/**
	 * 
	 */
	protected double tendencyToExplore = 1.0;
	
	/**
	 * @param bias
	 */
	public void setBias(double bias) {
		
		if (bias > 1.0) { bias = 1.0; }
		
		else if (bias < 0.0) { bias = 0.0; }
		
		tendencyToBias = bias;
		
		tendencyToExplore = 1.0 - bias;
		
	}
	
	/**
	 * 
	 */
	protected void incrementBias() {
		
		if (tendencyToBias < 1.0 && tendencyToExplore > 0.0) {
			
			tendencyToBias += 0.1;
			
			tendencyToExplore -= 0.1;
			
		}
		
	}
	
	/**
	 * 
	 */
	protected void decrementBias() {

		if (tendencyToBias > 0.0 && tendencyToExplore < 1.0) {
			
			tendencyToBias -= 0.1;
			
			tendencyToExplore += 0.1;
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#nextNode(HideAndSeek.graph.StringVertex)
	 */
	@Override
	protected StringVertex nextNode(StringVertex currentNode) {
		
		List<StringEdge> connectedEdges = getConnectedEdges(currentNode);
		
		ArrayList<StringEdge> biasEdges = new ArrayList<StringEdge>();
		
		ArrayList<StringEdge> explorativeEdges = new ArrayList<StringEdge>();
		
		for ( StringEdge edge : connectedEdges ) {
			
			// If no edge traversal decrement has been set 
			if ( graphController.getEdgeTraverselDecrement() == 1.0 ) {
				
				if ( graphController.getEdgeWeight(edge) < graphController.getFixedOrUpperValue() * CHEAPTHRESHOLD) {
					
					biasEdges.add(edge);
					
				} else {
					
					explorativeEdges.add(edge);
					
				}
				
			} else {
			
				if ( graphController.traverserEdgeCost(this, edge.getSource(), edge.getTarget()) < ( graphController.getEdgeWeight(edge) *  WELLTRAVERSEDPERCENTAGE ) ) {
					
					biasEdges.add(edge);
					
				} else {
					
					explorativeEdges.add(edge);
					
				}
				
			}
		
		}
			
		// If there is no information on the proportion of biased edges, select node at random
		if ( biasEdges.size() == 0 ) {
			
			return connectedNode(currentNode);
			
		} else {
			
			if (Math.random() < tendencyToBias) {
				
				// Get *least* weighted unvisisted (most bias) edge
				Collections.sort(biasEdges);
				
				return exploreEdges(currentNode, biasEdges);
				
			} else {
				
				// Get *most* weighted unvisited (most explorative) edge
				Collections.sort(explorativeEdges);
				Collections.reverse(explorativeEdges);
				
				return exploreEdges(currentNode, explorativeEdges);
				
			}
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#getConnectedEdge(HideAndSeek.graph.StringVertex, java.util.List)
	 */
	@Override
	protected StringEdge getConnectedEdge(StringVertex currentNode, List<StringEdge> connectedEdges) {
		
		for (StringEdge edge : connectedEdges ) {

			if ( uniquelyVisitedNodes().contains(edgeToTarget(edge, currentNode)) ) continue;
			
			return edge;
			
		}
		
		return connectedEdges.get((int)(Math.random() * connectedEdges.size()));
		
	}

	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#getConnectedEdges(HideAndSeek.graph.StringVertex)
	 */
	@Override
	protected List<StringEdge> getConnectedEdges(StringVertex currentNode) {
		
		ArrayList<StringEdge> edges = new ArrayList<StringEdge>(graphController.edgesOf(currentNode));
		
		Collections.sort(edges);
		
		return edges;
		
	}
	
	/**
	 * @param currentNode
	 * @param biasEdges
	 * @return
	 */
	private StringVertex exploreEdges(StringVertex currentNode, ArrayList<StringEdge> edges) {
		
		StringVertex target;
		
		for ( StringEdge edge : edges ) {
			
			if ( !uniquelyVisitedNodes().contains(edgeToTarget(edge, currentNode)) ) {
				
				return edgeToTarget(edge, currentNode);
				
			}
			
		};
		
		return connectedNode(currentNode);
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#startNode()
	 */
	@Override
	protected StringVertex startNode() {

		//StringVertex[] vertices = new StringVertex[graphController.vertexSet().size()];
		
		//return graphController.vertexSet().toArray(vertices)[0];
		
		return randomNode();
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#toString()
	 */
	public String toString() {
		
		return super.toString(); //+ " " + tendencyToBias;
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#printRoundStats()
	 */
	@Override
	public String printRoundStats() {
		
		return super.printRoundStats() + "," + tendencyToBias;
	
	}


	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#printGameStats()
	 */
	@Override
	public String printGameStats() {
		// TODO Auto-generated method stub
		return super.printGameStats() + graphController.requestAverageHiderScore(this);
	}

}
