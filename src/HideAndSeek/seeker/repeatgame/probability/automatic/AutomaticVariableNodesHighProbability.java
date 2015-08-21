package HideAndSeek.seeker.repeatgame.probability.automatic;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import HideAndSeek.seeker.repeatgame.probability.VariableNodesHighProbability;
import Utility.Utils;

/**
 * @author Martin
 */
public class AutomaticVariableNodesHighProbability extends VariableNodesHighProbability {
	
	public AutomaticVariableNodesHighProbability(
			GraphController <StringVertex, StringEdge> graphController) {
		
		super(graphController, Integer.MAX_VALUE, true);
		
	}
	
	/**
	 * 
	 */
	private static double CONFIDENCELEVEL = 0.5;
	
	/* (non-Javadoc)
	 * @see HideAndSeek.seeker.repeatgame.HighProbabilitySeeker#endOfRound()
	 */
	@Override
	public void endOfRound() {
		
		super.endOfRound();
		
		likelyNodes = behaviourPrediction.rankLikelyHideLocations(Integer.MAX_VALUE);
		
		for ( int i = 1; i < likelyNodes.size(); i++ ) {
			
			// Guess the number of nodes hidden with bias according to the disparity
			// between the probability of the 'last' node hidden with bias and the next
			// (NB. Do Syso. behaviourPrediction to see)
			if ( behaviourPrediction.getProbability(likelyNodes.get(i)) < 
					( behaviourPrediction.getProbability(likelyNodes.get(i-1)) * CONFIDENCELEVEL ) )  {
			
				Utils.talk(toString(), "Assuming " + i + " objects hidden with bias");
				
				predictiveNodes = i;
				
				likelyNodes = behaviourPrediction.rankLikelyHideLocations(i);
				
				return;
				
			}
			
		}
		
		Utils.talk(toString(), "Cannot discern number of nodes hidden with bias");
		
		likelyNodes.clear();
		
	}

	/**
	 * @return
	 */
	@Override
	public String printRoundStats() {
	
		return super.printRoundStats() + " " + getPredictiveNodes();
		
		
	}
	
}
