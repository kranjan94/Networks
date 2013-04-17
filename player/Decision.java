package player;

/**
 * Storage class for a move and a score associated with that move; to be used for gametrees.
 */
public class Decision {
	public Move move;
	public int score;
	
	public Decision(){
		move = new Move();
		score = 0;
	}
	
	public Decision(Move m, int s) {
		move = m;
		score = s;
	}
}