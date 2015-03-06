package HideAndSeek.hider.repeatgame.deceptive;

import java.util.ArrayList;
import java.util.List;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import HideAndSeek.hider.singleshot.random.RandomSet;
import Utility.Utils;

/**
 * Chooses to hide in a certain 'bias set' according to a factor
 * Epsilon.
 * 
 * @author Martin
 *
 */
public class EpsilonDeceptive extends Deceptive {

	private double epsilon;
	
	/**
	 * @param graphController
	 * @param numberOfHideLocations
	 * @param deceptiveNodes
	 * @param deceptionDuration
	 * @param epsilon percentage of time to choose to hide using the a deceptive set
	 */
	public EpsilonDeceptive(
			GraphController <StringVertex, StringEdge> graphController,
			int numberOfHideLocations, int deceptiveNodes, int deceptionDuration, double epsilon) {
		
		super(graphController, numberOfHideLocations, deceptiveNodes, deceptionDuration);
		
		this.epsilon = epsilon;

	}
	
	/**
	 * @param graphController
	 * @param numberOfHideLocations
	 * @param deceptiveNodes
	 * @param deceptionDuration
	 * @param repeatInterval
	 * @param startRound
	 * @param refreshDeceptiveSet
	 */
	public EpsilonDeceptive(
			GraphController <StringVertex, StringEdge> graphController,
			int numberOfHideLocations, int deceptiveNodes, int deceptionDuration, int repeatInterval, int repeatDuration, boolean refreshDeceptiveSet) {
		
		super(graphController, numberOfHideLocations, deceptiveNodes, deceptionDuration, repeatInterval, repeatDuration, refreshDeceptiveSet);
		
	}
	

	/**
	 * 
	 */
	@Override
	public void endOfRound() {
		
		super.endOfRound();
		
		double randomValue = Math.random() ;
		
		Utils.talk(this.toString(), "Random value: " + randomValue + " | " + "Epsilon: " + epsilon);
		
		// If random value falls under epsilon, play the deceptive set
		if (randomValue < epsilon) {
			
			Utils.talk(this.toString(), "Using deceptive set.");
			
			populateDeceptiveSet(deceptiveSet);
			
		} else {
			
			Utils.talk(this.toString(), "Hiding randomly.");
			
			if ( refreshDeceptiveSet ) createDeceptiveSet(deceptiveNodes);
			
			populateHideSet(createRandomSet(numberOfHideLocations, nodesUsedDeceptively));
			
		}
	
	}
	
}
