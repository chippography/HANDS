package HideAndSeek.seeker;

import java.util.ArrayList;
import java.util.HashSet;

import HideAndSeek.GraphTraverser;
import HideAndSeek.TraverserLocalGraph;
import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import Utility.Utils;

/**
 * @author Martin
 *
 */
public abstract class SeekingAgent extends TraverserLocalGraph implements Runnable, Seeker {
	
	/**
	 * All hide locations encountered in a game.
	 */
	protected ArrayList<StringVertex> allHideLocations;

	/**
	 * @return
	 */
	public ArrayList<StringVertex> allHideLocations() {
		
		return allHideLocations;
	
	}
	
	/**
	 * 
	 */
	protected HashSet<StringVertex> uniqueHideLocations;
	
	/**
	 * @return
	 */
	public HashSet<StringVertex> uniqueHideLocations() {
		
		return uniqueHideLocations;
		
	}
	
	/**
	 * We assume a default number of hide locations as 5.
	 */
	private int estimatedNumberOfHideLocations = 5;
	
	/**
	 * @return
	 */
	protected int estimatedNumberOfHideLocations() {
		
		return estimatedNumberOfHideLocations;
		
	}

	/**
	 * @param graphController
	 */
	public SeekingAgent(GraphController <StringVertex, StringEdge> graphController) {
		
		this(graphController, "", null);
		
	}
	
	/**
	 * @param graphController
	 */
	public SeekingAgent(GraphController <StringVertex, StringEdge> graphController, String name) {
		
		this(graphController, name, null);
		
	}
	
	/**
	 * @param graphController
	 */
	public SeekingAgent(GraphController <StringVertex, StringEdge> graphController, GraphTraverser responsibleAgent) {
		
		this(graphController, "", responsibleAgent);
		
	}
	
	/**
	 * @param graph
	 */
	public SeekingAgent(GraphController<StringVertex, StringEdge> graphController, String name, GraphTraverser responsibleAgent) {
		
		super(graphController, name, responsibleAgent);
		
		// Record of where hidden items have been found (in the whole game)
		allHideLocations = new ArrayList<StringVertex>();
		
		//
		uniqueHideLocations = new HashSet<StringVertex>();
		
	}
	
	
	/**
	 * Default: changing the number of expected locations each time.
	 */
	private int hideLocationEstimateInterval = 1;
	
	/** 
	 * If a seeker is not *permitted* access to the actual number of hide locations
	 * by the controller, it must estimate this, on a specified basis, based
	 * upon the number of hide locations most recently recorded.
	 */
	private void updateNumberOfHideLocationsEstimate() {
		
		if ( graphController.numberOfHideLocations(responsibleAgent) == -1 ) {
			
			if ( roundsPassed % hideLocationEstimateInterval == 0 && hideLocations().size() > 0 ) estimatedNumberOfHideLocations = hideLocations().size();
			
		} else {
			
			estimatedNumberOfHideLocations = graphController.numberOfHideLocations(responsibleAgent);
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return "s" + getName();
	
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		Utils.talk(toString(), "Running " + getID() + " " + getName());
		
		updateNumberOfHideLocationsEstimate();
		
		search();
		
	}
	
	/**
	 * What happens when a node is found
	 * 
	 * @param location
	 */
	public void addHideLocation(StringVertex location) {
		
		Utils.talk(toString(), "----------------------------------Found " + location);
		
		hideLocations().add(location); 
		
		allHideLocations.add(location);
		
		uniqueHideLocations.add(location);
		
	}
	
	/**
	 * @return
	 */
	public boolean searchCriteria() {
		
		return hideLocations().size() != estimatedNumberOfHideLocations;
		
	}
	
	/**
	 * @return
	 */
	public void search() {
		
		StringVertex startNode = startNode();
		
		if ( currentNode != null && currentNode != startNode ) {
			
			graphController.walkPathFromVertexToVertex(responsibleAgent, currentNode, startNode);
			
		} 
		
		atStart(startNode);
		
		StringVertex nextNode = null;
		
		while ( searchCriteria() ) {
			
			atNode();
			
			if ( graphController.isHideLocation(responsibleAgent, currentNode) && !hideLocations().contains(currentNode) ) { 
	        		
        		addHideLocation(currentNode);
				
				if (hideLocations().size() == estimatedNumberOfHideLocations) break;
			
        	}
		
			nextNode = nextNode(currentNode);
			
			if ( !graphController.fromVertexToVertex(responsibleAgent, currentNode, nextNode) ) { 
				
				Utils.talk(toString(), "Error traversing supplied path: " + currentNode + " to " + nextNode + getStatus());
				
			} else {

				atNextNode(nextNode);
				
			}
			
		}
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraversingAgent#printRoundStats()
	 */
	@Override
	public String printRoundStats() {
		
		return super.printRoundStats() + "Cost," + graphController.latestRoundCosts(responsibleAgent, false) + ",Explored," + exploredNodes.size() + ",Path," + exploredNodes.toString().replace(",", "");
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraversingAgent#printGameStats()
	 */
	@Override
	public String printGameStats() {
		
		return super.printGameStats() +
			   "Cost, " + graphController.averageGameCosts(responsibleAgent)  +  
			   ", PathLength, " + graphController.averagePathLength(responsibleAgent);
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#endOfRound()
	 */
	@Override
	public void endOfRound() {
		
		updateNumberOfHideLocationsEstimate();
		
		super.endOfRound();
		
		exploredNodes.clear();
		
		hideLocations().clear();
		
		queuedNodes.clear();
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#endOfGame()
	 */
	@Override
	public void endOfGame() {
		
		super.endOfGame();
		
		allHideLocations.clear();
		
		uniqueHideLocations.clear();
		
	}
	
	/**
	 * @param traverser
	 */
	public void mergeOtherTraverser(Seeker traverser) {
		
		super.mergeOtherTraverser(traverser);
		
		this.allHideLocations.addAll(traverser.allHideLocations());
		
		this.uniqueHideLocations.addAll(traverser.uniqueHideLocations());
		
	}

}