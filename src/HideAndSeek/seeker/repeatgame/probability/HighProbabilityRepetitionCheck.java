package HideAndSeek.seeker.repeatgame.probability;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import Utility.BehaviourPrediction;
import Utility.BehaviourPredictionRepetitionCheck;

/**
 * Extends the standard High Probability Seeker adding the capacity
 * to detect potential attempts to exploit the frequency algorithm used.
 * 
 * Mechanism in @see Utility.BehaviourPredictionRepetitionCheck.
 * 
 * @author Martin
 *
 */
public class HighProbabilityRepetitionCheck extends HighProbability {
	
	/**
	 * 
	 */
	private int numberOfConsecutiveMatches; 
	
	/**
	 * 
	 */
	private int nodesToCompare;
	
	/**
	 * @param graphController
	 * @param numberOfConsecutiveMatches
	 * @param nodesToCompare
	 */
	public HighProbabilityRepetitionCheck(
			GraphController <StringVertex, StringEdge> graphController, int numberOfConsecutiveMatches, int nodesToCompare) {
		super(graphController);
		
		this.numberOfConsecutiveMatches = numberOfConsecutiveMatches;
		
		this.nodesToCompare = nodesToCompare;
		
		behaviourPrediction = new BehaviourPredictionRepetitionCheck(numberOfConsecutiveMatches, nodesToCompare);
		
	}
	
	/* (non-Javadoc)
	 * @see HideAndSeek.seeker.repeatgame.HighProbability#endOfGame()
	 */
	public void endOfGame() {
		
		super.endOfGame();
		
		behaviourPrediction = new BehaviourPredictionRepetitionCheck(numberOfConsecutiveMatches, nodesToCompare);
		
	}
	
}
