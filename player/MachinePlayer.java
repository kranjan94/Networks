/* MachinePlayer.java */

package player;
import list.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
	
	int color; //Color of this player
	Board board; //Internal representation of the game board
	int searchDepth; //Depth of search for game tree traversal

  /**
   * Creates a machine player with the given color.  Color is either 0 (black)
   * or 1 (white).  (White has the first move.)
   * @param color the color of the player
   */
  public MachinePlayer(int color) {
	  this.color = color;
	  board = new Board(new PieceSet(Board.BLACK, board), new PieceSet(Board.WHITE, board));
	  searchDepth = 3;
  }

  /**
   * Creates a machine player with the given color and search depth.  Color is
   * either 0 (black) or 1 (white).  (White has the first move.)
   * @param color the color of the player
   * @param searchDepth the searchDepth of the player
   */
  public MachinePlayer(int color, int searchDepth) {
	  this(color);
	  this.searchDepth = searchDepth;
  }

  /**
   * Returns a new move by "this" player. Internally records the move (updates the 
   * internal game board) as a move by "this" player.
   */
  public Move chooseMove() {
	  Decision best = minimax(color, board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
	  board.makeMove(best.move, color);
	  return best.move;
  } 
  
  /**
   * Helper method for chooseMove implementing the minimax search algorithm 
   * with alpha-beta pruning
   * 
   * @param color the color of the player
   * @param b the board to be evaluated
   * @param depth the search depth
   * @param alpha the alpha element of alpha-beta pruning
   * @param beta the beta element of alpha-beta pruning
   * @return
   */
  private Decision minimax(int color, Board b, int depth, int alpha, int beta) {	  
	  Decision myBest = new Decision();
	  Decision reply;
	  
	  if(b.black.victory() || b.white.victory()) {
		  return new Decision(new Move(), b.evaluate());
	  }
	  if(depth >= searchDepth) { //because depth counts each minimax run but searchdepth counts pairs of runs
		  int score = b.evaluate();
		  return new Decision(null, score);
	  }
	  if(color == this.color){
		  myBest.score = Integer.MIN_VALUE;
	  } else {
		  myBest.score = Integer.MAX_VALUE;
	  }
	  
	  Iterator<Move> moves = b.generateAllMoves(color).iterator();
	  while(moves.hasNext()) {
		  Move m = moves.next();
		  Board test = b.duplicate();
		  test.makeMove(m, color);
		  reply = minimax((color+1)%2, test, depth+1, alpha, beta);
		  if(color == this.color && reply.score >= myBest.score){
			  myBest.score = reply.score;
			  myBest.move = m;
			  alpha = reply.score;
		  } else if(color != this.color && reply.score <= myBest.score) {
			  myBest.score = reply.score;
			  myBest.move = m;
			  beta = reply.score;
		  }
		  if(alpha >= beta) {
			  return myBest;
		  }
	  }
	  return myBest;
  }
  

  
  /**
   *  If the Move m is legal, records the move as a move by the opponent
   *  (updates the internal game board) and returns true.  If the move is
   *  illegal, returns false without modifying the internal state of "this"
   *  player.  This method allows your opponents to inform you of their moves.
   *  
   *  @param m the move to be recorded as opponent's move
   *  @return true if the move was successfully recorded, false otherwise
   */
  public boolean opponentMove(Move m) {
	//Assume this Player's color to be WHITE because WHITE has first move
	int oppColor = Board.BLACK;
	//If this Player's color is actually BLACK, set opponent's color to WHITE
	if (color == Board.BLACK){
		oppColor = Board.WHITE;
	}
	
	if (board.isLegalMove(m, oppColor)) {		
		return board.makeMove(m, oppColor);
	}
	return false;
  }

  /**
   * If the Move m is legal, records the move as a move by "this" player
   * (updates the internal game board) and returns true.  If the move is
   * illegal, returns false without modifying the internal state of "this"
   * player.  This method is used to help set up "Network problems" for your
   * player to solve.
   * 
   * @param m the move to be made
   * @return true if the move is successfully made, false otherwise
   */
  public boolean forceMove(Move m) {
	if (board.isLegalMove(m, color)) {
		return board.makeMove(m, color);
	}
    return false;
  }


}
