/* PieceSet.java */

package player;
import list.*;

/*
 *  A class that is the set of the player's pieces on the board.
 */
public class PieceSet{
	private int color;
	private int numPieces; //Number of pieces on the board
	private Board board; //The board this pieceset is on
	private LinkedList<Piece> pieces; //List of pieces placed on the board
	
	/**
	 * Constructor for PieceSet of color col with num pieces
	 * @param col the color of this PieceSet
	 * @param num the number of Pieces in this PieceSet
	 */
	public PieceSet(int col, Board b) {
		color = col;
		board = b;
		pieces = new LinkedList<Piece>();
		numPieces = 0;
	}
	
	public PieceSet(int col, int[][] pieceset, Board b) {
		color = col;
		board = b;
		pieces = toList(pieceset, b);
		numPieces = pieces.size();
	}
	
	/**
	 * Returns the color of this set of pieces
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * Returns the number of pieces in this set (on the board)
	 */
	public int getSize() {
		return numPieces;
	}
	
	/**
	 * Returns the board this PieceSet is on
	 */
	public Board getBoard(){
		return board;
	}
	
	/**
	 * Returns the pieces in this set that have been placed
	 * on the board
	 */
	public LinkedList<Piece> getPieces() {
		return pieces;
	}
	
	/**
	 * If s is occupied by a piece of this color, return it; return null otherwise.
	 * @param s		Space to be checked
	 * @return		The piece at s; null if empty or of a different color.
	 */
	public Piece getPiece(Space s) {
		Iterator<Piece> piecesIt = pieces.iterator();
		while(piecesIt.hasNext()) {
			Piece curr = piecesIt.next();
			if(curr.getSpace().equals(s)) { //Piece found
				return curr;
			}
		}
		return null;
	}
	
	/**
	 * USED IN DUPLICATING BOARDS
	 */
	
	/**
	 * Returns the List pieces as an array of the x,y positions
	 * of each piece in the list
	 */
	public int[][] toArray() {
	  int[][] positions = new int[numPieces][2];
	  for (int i=0; i<pieces.size(); i++){
		  int[] pos = pieces.get(i).getSpace().position();
		  positions[i][0] = pos[0];
		  positions[i][1] = pos[1];
	  }
	  return positions;
	}
	
	/**
	 * Returns the array of x,y positions in the given array as a
	 * list of Pieces (the same color as this PieceSet) in those positions
	 * @param positions the 2D array of x,y positions
	 * @param b the board the list of pieces is on
	 */
	public LinkedList<Piece> toList(int[][] positions, Board b) {
	  if (b==null){ //If not specified a board for the pieces, use the board of this pieceset
		  b = board;
	  }
	  LinkedList<Piece> pieceset = new LinkedList<Piece>();
	  for (int i=0; i<positions.length; i++){
		Piece temp = new Piece(this, new Space(positions[i][0], positions[i][1], b));
		pieceset.addLast(temp);
	  }
	  return pieceset;
	}
	
	/**
	 * Adds a piece to this PieceSet.
	 * @param p	the piece to be added
	 */
	public void addPiece(Piece p) {
		pieces.add(p);
		p.setSet(this);
		numPieces++;
	}
	
	/**
	 * EVALUATING A BOARD
	 */
	
	/**
	 * Returns the sum of all connections for all pieces in this pieceset
	 * @return int number of connections total in this pieceset
	 */
	public int countConnections() {
		int total = 0;
		Iterator<Piece> piecesIt = pieces.iterator();
		while(piecesIt.hasNext()) {
			total += piecesIt.next().getConnections(null).size();
		}
		return total;
	}
	
	/**
	 * Returns true if this piece is in the starting goal (left for white, top for black)
	 * for its color
	 * @param p Piece to check
	 * @return  true if this piece is in the start goal for its color
	 */
	private boolean isStartPiece(Piece p){
		return ((color == Board.BLACK && p.getSpace().position()[1] == 0)
				 ||(color == Board.WHITE && p.getSpace().position()[0] == 0));
	}
	
	/**
	 * Returns true if this piece is in the end goal for this color (right for white, bottom for black)
	 * @param p	Piece to check
	 * @return	true if this piece is in the end goal for this color
	 */
	private boolean isEndPiece(Piece p) {
		return ((color == Board.BLACK && p.getSpace().position()[1] == Board.SIZE - 1)
				 ||(color == Board.WHITE && p.getSpace().position()[0] == Board.SIZE - 1));
	}
	
	/**
	 * Returns 1 if this pieceset has at least one piece in its end goal
	 * (right for white, bottom for black), 0 otherwise
	 * @return 1 if there is at least one piece in this pieceset's end goal, 0 otherwise
	 */
	public int hasEndPiece() {
		Iterator<Piece> piecesIt = pieces.iterator();
		while(piecesIt.hasNext()) {
			Piece p = piecesIt.next();
			if(isEndPiece(p)) {
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * Returns 1 if this pieceset has at least one piece in its start goal
	 * (left for white, top for black), 0 otherwise
	 * @return 1 if there is at least one piece in this pieceset's start goal, 0 otherwise
	 */
	public int hasStartPiece() {
		Iterator<Piece> piecesIt = pieces.iterator();
		while(piecesIt.hasNext()) {
			Piece p = piecesIt.next();
			if(isStartPiece(p)) {
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * Returns a list containing the pieces on the starting end of the board (left edge
	 * for white, top edge for black).
	 * @return	list containing starting pieces for this PieceSet
	 */
	private LinkedList<Piece> getStartPieces() {
		LinkedList<Piece> out = new LinkedList<Piece>();
		Iterator<Piece> piecesIt = pieces.iterator();
		while(piecesIt.hasNext()){
			Piece p = piecesIt.next();
			if(isStartPiece(p)){ //Piece is a starting piece
				out.add(p);
			}
		}
		return out;
	}
	
	/**
	 * Recursive method that checks whether a given piece is part of a network.
	 * prev == null indicates that this piece is in the start goal.
	 * @param curr		Piece currently being checked
	 * @param prev		Previous piece checked
	 * @param length	Length of the network at this point
	 * @param seen		Pieces previously seen in this network; repeats are not allowed
	 * @return			true iff this piece is in a network
	 */
	private boolean hasNetwork(Piece curr, LinkedList<Piece> seen) {
		Piece prev;
		if(seen.size() == 0) { //First piece in the network; no previous
			prev = null;
		} else {
			prev = seen.get(0);
		}
		LinkedList<Piece> connections = curr.getConnections(prev); //seen.get(0) is the previous piece
		if(seen.contains(curr)) { //curr already in this network
			return false;
		} else if(seen.size() >= 1 && isStartPiece(curr)){ //check to not add more start goal pieces if seen already has such a piece
			return false;
		} else if(isEndPiece(curr)) {
			if(seen.size() + 1 >= 6){
				return true;
			} else {
				return false; //Only one piece in each goal allowed
			}
		} else if (seen.size() == 10 || connections.size() == 0){ //Negative base case
			return false;
		} else { //Check all connected pieces for completed networks
			Iterator<Piece> connIt = connections.iterator();
			LinkedList<Piece> newSeen = seen.copy();
			newSeen.addFirst(curr); //Add this piece to the list of seen pieces
			while(connIt.hasNext()){
				Piece next = connIt.next();
				if(hasNetwork(next, newSeen)) { //Recursive call
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true iff this PieceSet has a winning network. A winning network:
	 * 		-Is at least of length 6.
	 * 		-Has no 3 consecutive pieces in a row.
	 * 		-Begins and ends in one of the goals for this player.
	 * @return	true if we win
	 */
	public boolean victory() {
		Iterator<Piece> starts = getStartPieces().iterator();
		while(starts.hasNext()){ //Check all pieces in starting goal
			Piece p = starts.next();
			LinkedList<Piece> seen = new LinkedList<Piece>();
			if(hasNetwork(p, seen)){ //Start a network of length 1 containing just this piece.
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Prints all connections within this network.
	 */
	public void printConnections() {
		System.out.print("\n");
		Iterator<Piece> piecesIt = pieces.iterator();
		while(piecesIt.hasNext()){
			Piece p = piecesIt.next();
			LinkedList<Piece> conns = p.getConnections(null);
			System.out.println("Connections to " + p + ":");
			System.out.println(conns);
		}
	}
	
	/**
	 * Returns a string representation of this PieceSet.
	 */
	public String toString() {
		String col;
		if(color == Board.BLACK) {
			col = "Black";
		} else {
			col = "White";
		}
		Iterator<Piece> piecesIt = pieces.iterator();
		String out = col + " pieces:\n";
		while(piecesIt.hasNext()) {
			Piece p = piecesIt.next();
			out += "\t" + p + "\n";
		}
		return out;
	}
}
