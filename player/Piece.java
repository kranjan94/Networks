/* Piece.java */

package player;
import list.*;

/**
 *  A class that represents a player's single Piece.
 */
public class Piece{
	
	private PieceSet mySet;
	private Space space;
	
	public Piece(PieceSet set, Space loc) {
		mySet = set;
		space = loc;
	}
	
	/**
	 * Returns the space that this Piece occupies
	 */
	public Space getSpace() {
		return space;
	}
	
	/**
	 * GETTING CONNECTIONS
	 */
	
	/**
	 * Returns a numerical representation of the direction of Piece other from this; i.e.,
	 * {-1, 1} --> northeast
	 * {0, -1} --> south
	 * etc.
	 * @param other	Piece to be compared with this
	 * @return	{x shift of direction, y shift of direction}
	 */
	public int[] getXYShifts(Piece other) {
		if(other == null) {
			int out[] = {0,0};
			return out;
		}
		int xDiff = other.getSpace().position()[0] - space.position()[0];
		int yDiff = other.getSpace().position()[1] - space.position()[1];
		if(xDiff != 0) {
			xDiff /= Math.abs(xDiff); //Scale xDiff to [-1,1]
		}
		if(yDiff != 0) {
			yDiff /= Math.abs(yDiff); //Scale yDiff to [-1,1]
		}
		int[] out = {xDiff, yDiff};
		return out;
	}
	
	/**
	 * Returns a list of all the Pieces that are the same color as and
	 * form connections with this Piece; pieces must not form a line with prev
	 */
	public LinkedList<Piece> getConnections(Piece prev) {
		LinkedList<Piece> connections = new LinkedList<Piece>();
		int myX = space.position()[0];
		int myY = space.position()[1];
		int previousXShift = getXYShifts(prev)[0];
		int previousYShift = getXYShifts(prev)[1];
		int nextXShift = previousXShift * -1;
		int nextYShift = previousYShift * -1;
		
		for(int run = -1; run <= 1; run++) {
			for(int rise = -1; rise <= 1; rise++) { //These for-loops denote the 8 directions in which a piece can lie
				if((rise != 0 || run != 0)//Do not check own square
						&& !(run == previousXShift && rise == previousYShift) //Do not check prev
						&& !(run == nextXShift && rise == nextYShift)) {  //Do not form line with prev
					int checkX = myX + run;
					int checkY = myY + rise;
					while(Board.isValidLocation(checkX, checkY)) {
						
						int checkType = space.getBoard().getPiece(checkX, checkY); //Check current space
						
						if(checkType == mySet.getColor()) { //Connection found
							connections.add(mySet.getPiece(new Space(checkX, checkY)));
							break;
						} else if(checkType != -1) { //Space contains opponent's piece; stop checking this direction
							break;
						} else {
							checkX += run;
							checkY += rise; //Move on to next space
						}
					}
				}
			}
		}
		return connections;
	}
	
	/**
	 * Returns the PieceSet this Piece is in
	 */ 
	public PieceSet getSet() {
		return mySet;
	}
	
	/**
	 * Sets the PieceSet of this piece to set
	 * @param set	PieceSet to which this Piece now belongs
	 */
	public void setSet(PieceSet set) {
		mySet = set;
	}
	
	/**
	 * Changes the location of this piece.
	 * @param s	The space to move this piece to.
	 */
	public void move(Space s) {
		space = s;
	}

	/**
	 * Returns a string representation of this piece.
	 */
	public String toString() {
		String col;
		if(mySet.getColor() == Board.BLACK) {
			col = "Black";
		} else {
			col = "White";
		}
		String out = col + " piece at (" + space.position()[0] + "," + space.position()[1] + ")";
		return out;
	}
}
