/*Board.java*/

package player;

import list.*;

/*
 * Represents the full game board for the game of Network to be used by a MachinePlayer.
 */
public class Board {
	
	public static final int SIZE = 8;
	public static final int BLACK = 0;
	public static final int WHITE = 1;
	Space[][] board; //8x8 array of spaces
	PieceSet black;
	PieceSet white;

	/**
	 * Initializes a SIZExSIZE board for the specified player. Sets invalid spaces as appropriate.
	 */
	public Board() {
		this(null, null);
	}
	
	/**
	 * Initializes a SIZExSIZE board with pre-existing PieceSets
	 * @param b
	 * @param w
	 */
	public Board(PieceSet b, PieceSet w) {
		board = new Space[SIZE][SIZE];
		for(int i = 0; i<board.length; i++){ //Iterate horizontally
			for(int j = 0; j<board[0].length; j++){ //Iterate vertically
				board[i][j] = new Space(i, j, this);
			}
		}
		black = b;
		white = w;
	}
	
	/**
	 * Adds a PieceSet to this board. Returns true if successfully added, false otherwise.
	 */
	public boolean addPieceSet(PieceSet ps){
		if (ps.getColor() == WHITE){
			white = ps;
			return true;
		} else if (ps.getColor() == BLACK){
			black = ps;
			return true;
		}
		return false;
	}
	
	
	/**
	 * Performs the Move specified by the parameter. Changes the board accordingly.
	 * @param m		Move to be recorded.
	 * @return		True if move was successfully performed; false otherwise.
	 */
	public boolean makeMove(Move m, int color){		
		PieceSet player = null;
		switch(color) {
			case BLACK:	player = black;
						break;
			case WHITE:	player = white;
						break;
		}
		if(m.moveKind == Move.QUIT) {
			return true;
		} else if(m.moveKind == Move.ADD) {
			player.addPiece(new Piece(player, new Space(m.x1, m.y1, this)));
			return board[m.x1][m.y1].place(color);
		} else if(m.moveKind == Move.STEP){
			if(board[m.x2][m.y2].type() == color){
				player.getPiece(new Space(m.x2, m.y2)).move(new Space(m.x1, m.y1, this));
				return board[m.x1][m.y1].place(board[m.x2][m.y2].remove()); //Removes the previous piece and passes the removed piece as the parameter to Space.place()
			}
		}
		return false; //Something wrong with m
	}
	
	
	/**
	 * CHECK WHETHER A GIVEN MOVE IS LEGAL
	 */
	
	/**
	 * Gets the 8 neighboring spaces to the specified space.
	 * @param x	x-coordinate of center space
	 * @param y	y-coordinate of center space
	 * @return	array of neighboring Spaces
	 */
	private Space[] getNeighborSpaces(int x, int y){
	  Space[] neighbors = new Space[8];
	  try{
		  neighbors[0] = board[x][y-1]; //North
		  neighbors[1] = board[x+1][y-1]; //Northeast
		  neighbors[2] = board[x+1][y]; //East
		  neighbors[3] = board[x+1][y+1]; //Southeast
		  neighbors[4] = board[x][y+1]; //South
		  neighbors[5] = board[x-1][y+1]; //Southwest
		  neighbors[6] = board[x-1][y];  //West
		  neighbors[7] = board[x-1][y-1]; //Northwest
		  return neighbors;
	  } catch(ArrayIndexOutOfBoundsException e){
		  if(x == 0){ //Along west wall;
			  neighbors[0] = board[x][y-1]; //North
			  neighbors[1] = board[x+1][y-1]; //Northeast
			  neighbors[2] = board[x+1][y]; //East
			  neighbors[3] = board[x+1][y+1]; //Southeast
			  neighbors[4] = board[x][y+1]; //South
			  neighbors[5] = null; //Southwest
			  neighbors[6] = null; //West
			  neighbors[7] = null; //Northwest
		  }
		  if(y == 0){ //Along north wall
			  neighbors[0] = null;
			  neighbors[1] = null;
			  neighbors[2] = board[x+1][y];
			  neighbors[3] = board[x+1][y+1];
			  neighbors[4] = board[x][y+1];
			  neighbors[5] = board[x-1][y+1];
			  neighbors[6] = board[x-1][y];
			  neighbors[7] = null;
		  }
		  if(y == SIZE-1){ //Along south wall
			  neighbors[0] = board[x][y-1];
			  neighbors[1] = board[x+1][y-1];
			  neighbors[2] = board[x+1][y];
			  neighbors[3] = null;
			  neighbors[4] = null;
			  neighbors[5] = null;
			  neighbors[6] = board[x-1][y];
			  neighbors[7] = board[x-1][y-1];
		  }
		  if(x == SIZE-1){ //Along east wall
			  neighbors[0] = board[x][y-1];
			  neighbors[1] = null;
			  neighbors[2] = null;
			  neighbors[3] = null;
			  neighbors[4] = board[x][y+1];
			  neighbors[5] = board[x-1][y+1];
			  neighbors[6] = board[x-1][y]; 
			  neighbors[7] = board[x-1][y-1];
		  }
		  return neighbors;
	  }
	}
	
	/**
	 * Checks whether placing a piece would be a violation of rule 4; namely, if placing
	 * a piece of type color would cause a chain of 3 adjacent pieces of type color.
	 * @param x		x-coordinate of location to be checked
	 * @param y		y-coordinate of location to be checked
	 * @param color	type of piece attempting to be placed
	 * @return		true iff moving a piece of type color here cause a rule 4 violation.
	 */
	private boolean isRule4Violation(int x, int y, int color){
		Space[] immediateNeighbors = getNeighborSpaces(x,y);
		int count = 0; //Number of same type pieces
		for(Space s: immediateNeighbors){
			if(s != null && s.type() == color){ //Neighbor of same type found; check neighboring spaces
				count++;
				Space[] neighbors = getNeighborSpaces(s.position()[0], s.position()[1]);
				for(Space n: neighbors){
					if(n != null && n.type()==color && !(x==n.position()[0] && y==n.position()[1])){
						return true;
					}
				}
			}
			if(count >= 2){ //Two or more same type pieces in immediateNeighbors
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether placing a piece here would be a valid move.
	 * @param x		x-coordinate of location to be checked
	 * @param y		y-coordinate of location to be checked
	 * @param color	type of piece attempting to be placed
	 * @return		true iff moving a piece of type color here is a valid move.
	 */
	private boolean isValidLocation(int x, int y, int color){
		if( (color == BLACK && (x == 0 || x == SIZE-1))
		  ||(color == WHITE && (y == 0 || y == SIZE-1))){ //Rules 1 and 2
			return false;
		} else if(!board[x][y].isEmpty()) { //Rule 3
			return false;
		} else {
			return !isRule4Violation(x, y, color); //Valid if rule 4 is not violated
		}
	}
	
	/**
	 * Checks whether the specified move is legal for the specified player.
	 * @param m			move attempting to be made
	 * @param player	player attempting to make the move
	 * @param numPieces	number of pieces the player has left
	 * @return			true iff m is a legal move for player
	 */
	public boolean isLegalMove(Move m, int player) {
		if(m.moveKind == Move.QUIT){ //Always a valid move
			return true;
		} else if(m.moveKind == Move.STEP){
			if((player == BLACK && black.getSize() < 10)
				|| (player == WHITE && white.getSize() < 10)){
				return false;
			}
			if(this.board[m.x2][m.y2].type() != player){ //Piece to be moved is not there or is opponent's	
				return false;
			} else {
				Board test = this.duplicate();
				test.board[m.x2][m.y2].remove();
				if(test.isValidLocation(m.x1, m.y1, player)){
					return true;
				} else{
					return false;
				}
			}
		} else if(m.moveKind == Move.ADD) {
			if((player == BLACK && black.getSize() >= 10)
				|| (player == WHITE && white.getSize() >= 10)){				
				return false;
			}			
			return isValidLocation(m.x1, m.y1, player);
		} else { //Move type not recognized
			return false;
		}
	}
	
	
	/**
	 * GENERATING POSSIBLE MOVES
	 */
	
	/**
	 * Returns a list containing all spaces to which a piece of type color can be moved.
	 * @param color	color of the current player
	 * @return	a list of all valid spaces for this player
	 */
	private LinkedList<Space> emptySpaces(int color){
		LinkedList<Space> spaces = new LinkedList<Space>();
		for(int i = 0; i<board.length; i++){
			for(int j = 0; j<board.length; j++){
				if(isValidLocation(i, j, color)){
					spaces.add(board[i][j]);
				}
			}
		}		
		return spaces;
	}
	
	/**
	 * Generate all legal moves on this board for the player of color color.
	 * @param color	the player for whom to generate legal moves
	 * @return	list containing all legal moves at this point
	 */
	public LinkedList<Move> generateAllMoves(int color){
		LinkedList<Move> out = new LinkedList<Move>();
		LinkedList<Space> spaces = emptySpaces(color);
		Iterator<Space> spacesIt = spaces.iterator();
		PieceSet player = null;
		switch(color) {
			case BLACK:	player = black;
						break;
			case WHITE:	player = white;
						break;
		}
		if(player.getSize() == 10){ //Step move
			Iterator<Piece> pieces = player.getPieces().iterator();
			while(pieces.hasNext()) {
				Space curr = ((Piece)pieces.next()).getSpace();
				while(spacesIt.hasNext()) {
					Space s = (Space)spacesIt.next();
					Move m = new Move(s.position()[0], s.position()[1],
										curr.position()[0], curr.position()[1]);
					out.add(m);
				}
				spacesIt = spaces.iterator();
			}
		} else { //Add move
			while(spacesIt.hasNext()) {
				Space s = (Space)spacesIt.next();
				Move m = new Move(s.position()[0], s.position()[1]);
				out.add(m);
			}
		}
		return out;
	}
	
	
	/**
	 * EVALUATING BOARDS
	 */
	
	/**
	 * Evaluates the score of this board for the given player
	 * @param color  the int color of the player to evaluate for
	 * @return	an int representing the score of the board for the given player.
	 */
	public int evaluate(){
		if (white.victory()){
			return Integer.MAX_VALUE;
		} else if (black.victory()){
			return Integer.MIN_VALUE;
		}
		int whiteConnections = white.countConnections();
		int blackConnections = black.countConnections();
		int whiteEnds = white.hasStartPiece() * white.hasEndPiece() * 100;
		int blackEnds = black.hasStartPiece() * black.hasEndPiece() * 10;
		return 10*(whiteConnections - blackConnections) + (whiteEnds - blackEnds);
	}
	
	/**
	 * UTILITY METHODS
	 */
	
	/**
	 * Returns the type of the piece at the specified location; accounts for wrapping.
	 * Mainly shorthand for this.board[x][y].type().
	 * @param x	x-coordinate of location
	 * @param y	y-coordinate of location
	 * @return	the piece at (x,y) accounting for wrapping around the board.
	 */
	public int getPiece(int x, int y){
		return board[x % board.length][y % board[0].length].type();
	}
	
	/**
	 * Returns a text representation of the board.
	 */
	public String toString(){
		String out = "";
		for(int j = 0; j<board[0].length; j++){
			for(int i = 0; i<board.length; i++){
				switch(getPiece(i,j)){
				case -1: out += "-- ";
						 break;
				case BLACK:	out += "BB ";
							break;
				case WHITE:	out += "WW ";
							break;
				}
			}
			out += "\n";
		}
		out += "\n";
		return out;
	}
	
	/**
	 * Duplicates this board exactly. Meant for testing possible moves.
	 * @return	an exact copy of this board.
	 */
	public Board duplicate(){
		Board out = new Board();
		out.addPieceSet(new PieceSet(BLACK, black.toArray(), out));
		out.addPieceSet(new PieceSet(WHITE, white.toArray(), out));
		for(int i = 0; i<board.length; i++){ //Iterate horizontally
			for(int j = 0; j<board[0].length; j++){ //Iterate vertically
				out.board[i][j].place(getPiece(i,j));
			}
		}
		return out;
	}
	
	/**
	 * Determines whether a given location is within the board.
	 * @param x the x-coordinate to check
	 * @param y the y-coordinate to check
	 * @return	true iff the location is contained within the board
	 */
	public static boolean isValidLocation(int x, int y) {
		return x >= 0 && y >= 0 && x < SIZE && y < SIZE;
	}
}


/*
 * Represents a single space on the board for Network. Keeps track of its position and the
 * type or lack of an occupying piece.
 */
class Space {
	private int x; //x-coordinate
	private int y; //y-coordinate
	private int type; //type of space; -1 = empty, 0 = black, 1 = white
	private Board board; //Board that contains this space
	
	/**
	 * Initializes a Space with specified coordinates; sets this space to invalid if
	 * specified.
	 * @param x
	 * @param y
	 */
	public Space(int x, int y, Board b){
		this.x = x;
		this.y = y;
		this.type = -1;
		this.board = b;
	}
	
	public Space(int x, int y){
		this.x = x;
		this.y = y;
		this.type = -1;
		this.board = null;
	}
	
	
	/**
	 * True iff this space is empty.
	 * @return	true iff this space is empty.
	 */
	public boolean isEmpty(){
		return type == -1;
	}
	
	/**
	 * Gives the position of this space.
	 * @return	an int array; 0th index is x-coordinate, 1st index is y-coordinate
	 */
	public int[] position(){
		int[] pos = {this.x, this.y};
		return pos;
	}
	
	/**
	 * Gives the current type occupying this space.
	 * @return	-2 if invalid space, -1 if empty, 0 if black, 1 if white
	 */
	public int type(){
		return type;
	}
	
	/**
	 * Sets the current type of this space to the parameter. Does nothing if space is occupied.
	 * @param type	int representation of new type of this space.
	 * @return		true iff the piece was placed; false otherwise
	 */
	public boolean place(int type){
		if(this.type == -1){
			this.type = type;
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the piece on this space and returns it; returns empty if this space is empty.
	 * @return	the type of piece removed if occupied; -1 otherwise.
	 */
	public int remove(){
		if(this.type > -1){
			int temp = this.type;
			this.type = -1;
			return temp;
		} else {
			return -1;
		}
	}
	
	/**
	 * Returns the board that contains this Space.
	 */
	public Board getBoard() {
		return board;
	}
	
	/**
	 * Returns true iff another space is "visible" from this one
	 * @param s	other space to be checked
	 * @return	true iff space is in one of the 8 main directions from this
	 */
	public boolean visible(Space s) {
		int xDiff = Math.abs(s.position()[0] - x);
		int yDiff = Math.abs(s.position()[1] - y);
		return (!(xDiff == 0 && yDiff == 0) && (xDiff == 0 || yDiff == 0 || xDiff == yDiff));
	}
	
	/**
	 * Returns true iff this space has the same coordinates as another.
	 */
	public boolean equals(Space s) {
		if (s == null){
			return false;
		}
		return (s.position()[0] == x && s.position()[1] == y);
	}
	
	/**
	 * Returns a string representation of this space
	 */
	public String toString() {
		return "Space at (" + x + ", " + y + ")";
	}
}
