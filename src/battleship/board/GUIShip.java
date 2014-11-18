package battleship.board;

public class GUIShip {

	// length of ship
	private int length;
	
	//boolean array representing where the ship has been hit
	private boolean[] hit; 
	private boolean isSunk = false;
	
	// the direction from xyOrigin that the ship faces
	public static enum Orientation {NORTH, SOUTH, EAST, WEST}
	private Orientation facing; 
	
	//the yx pos of the ships origin
	private int[] yxOrigin = new int[2];
	
	//the name of the ship
	public String shipName;
	
	//the ship constructor, unable to construct a ship without these values
	public GUIShip(int length, Orientation or, int[] yxOrigin, String name){
		this.length = length;
		this.facing = or;
		this.yxOrigin = yxOrigin;
		this.shipName = name;
		hit = new boolean[length];
	}
	
	//returns comma separated representation of this ship
	public String toString() {
		StringBuffer returnBuffer = new StringBuffer();
		returnBuffer.append(length + ",");
		returnBuffer.append(facing + ",");
		returnBuffer.append(yxOrigin[0] + ",");
		returnBuffer.append(yxOrigin[1] + ",");
		returnBuffer.append(shipName + ",");
		returnBuffer.append(isSunk + ",");
		
		return returnBuffer.toString();		
	}
	//only a marker should ever call this, but it will hit the ship at the given
	//position and return true if this results in the ship being sunk
	public boolean hitShipAt(int position){
		this.hit[position] = true;
		return this.isShipSunk();
	}
	
	//returns the sunk status of the ship
	public boolean isShipSunk(){
		if (!isSunk){
			for(boolean isHit: hit){
				if (!isHit){
					return false;
				} 
			}
			isSunk = true;
		}		
		return true;
	}
	
	//local class to revert changes made within a game board
	//currently used within the placeShip method
	private class Changes {
		GUIBoardMarker[] changeArray;
		Changes(){
			changeArray = new GUIBoardMarker[length];
		}
		void revert(){
			for(GUIBoardMarker bm: changeArray){
				if (bm != null){
					bm.revertToPrevious();
				}
			}
		}
	}
	
	//first test to see if the ship will fit on the board
	//then fill the markers if it does: may need to change this to a boolean return
	//this will also revert any changes should placing the ship fail
	public void placeShip(GUIGameBoard gb) throws ShipPlacementException{		
		Changes changes = new Changes();
		GUIBoardMarker currentMarker;
		switch(facing){
			case EAST:
				if (yxOrigin[1] + length - 2 < 9){
					try {
						for(int i = 0; i < length; i++){
							changes.changeArray[i] = currentMarker = gb.getMarker(yxOrigin[1] + i, yxOrigin[0]);
							currentMarker.setShip(this, i);
						}
					} catch (ShipPlacementException e){
						changes.revert();
						throw e;
					}
				} else throw new ShipPlacementException("Ship { "+this.shipName+" } Does Not Fit in Grid");
				break;
			case WEST:
				if (yxOrigin[1] - length + 2 > 0){
					try {
						for(int i = 0; i < length; i++){
							changes.changeArray[i] = currentMarker = gb.getMarker(yxOrigin[1] - i, yxOrigin[0]);
							currentMarker.setShip(this, i);
						}
					} catch (ShipPlacementException e){
						changes.revert();
						throw e;
					}
				} else throw new ShipPlacementException("Ship { "+this.shipName+" } Does Not Fit in Grid");
				break;
			case NORTH:
				if (yxOrigin[0] - length + 2 > 0){
					try {
						for(int i = 0; i < length; i++){
							changes.changeArray[i] = currentMarker = gb.getMarker(yxOrigin[1], yxOrigin[0] - i);
							currentMarker.setShip(this, i);
						}
					} catch (ShipPlacementException e){
						changes.revert();
						throw e;
					}
				} else throw new ShipPlacementException("Ship { "+this.shipName+" } Does Not Fit in Grid");
				break;
			case SOUTH:
				if (yxOrigin[0] + length - 2 < 9){
					try {
						for(int i = 0; i < length; i++){
							changes.changeArray[i] = currentMarker = gb.getMarker(yxOrigin[1], yxOrigin[0] + i);
							currentMarker.setShip(this, i);
						}
					} catch (ShipPlacementException e){
						changes.revert();
						throw e; 
					}
				} else throw new ShipPlacementException("Ship { "+this.shipName+" } Does Not Fit in Grid");
				break;
			default: 
		}		
	}
}
