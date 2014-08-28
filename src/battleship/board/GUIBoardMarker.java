package battleship.board;

import javax.swing.*;


@SuppressWarnings("serial")
public class GUIBoardMarker extends JButton{
	public enum MarkerType {OCEAN, SHIP, MISS, HIT}
	
	private MarkerType markerType;
	private GUIBoardMarker previousMark;
	private GUIShip thisShip;
	private int shipPosition;
	
	//constructor defaults to ocean
	public GUIBoardMarker(){
		this.markerType = MarkerType.OCEAN;
		this.updateText();
	}
	
	//sets this marker to a specific ship and the position of the ship it represents
	protected void setShip(GUIShip newShip, int shipPosition) throws ShipPlacementException{
		this.storePrevious();
		if(this.markerType != MarkerType.SHIP){
			this.thisShip = newShip;
			this.shipPosition = shipPosition;
			this.markerType = MarkerType.SHIP;
			this.updateText();
		} else throw new ShipPlacementException("Could not place ship: " + newShip.shipName
				+ " Intersected with ship: "+ thisShip.shipName);
	}
	
	//Hit this marker and return string value indicating what was hit
	public GUIShip hitMarker() throws HitMarkerException{ 
		switch(this.markerType){
		case HIT:
			throw new HitMarkerException("You have already Hit here");
		case MISS:
			throw new HitMarkerException("You have aldready hit here");
		case OCEAN:
			storePrevious();
			markerType = MarkerType.MISS;
			this.updateText();
			return null;
		case SHIP:	
			//marks the ship hit, if the ship has been sunk it returns that
			storePrevious();
			markerType = MarkerType.HIT;
			thisShip.hitShipAt(shipPosition);
			this.updateText();
			return thisShip;
		default:
			throw new AssertionError("Unexpected Marker type");
		}
	}

	
	//returns the previous mark for the event that a hit needs to be reverted
	void revertToPrevious(){
		this.thisShip = previousMark.thisShip;
		this.markerType = previousMark.markerType;
		this.shipPosition = previousMark.shipPosition;
	}
	
	//store previous
	private void storePrevious(){
		GUIBoardMarker ret = new GUIBoardMarker();
		if (this.markerType == MarkerType.SHIP){
			try{
				ret.setShip(thisShip, shipPosition);
			} catch (ShipPlacementException e){
				throw new AssertionError("Marker Constructor Altered");
			}
		} else ret.markerType = this.markerType;
		previousMark = ret;
	}
	
	
	//helps print a valid display of the gameboard to the console. 
	//may overwrite the tostring of the defining enum at some point
	public String toString(){
		switch(markerType){
		case HIT:
			return "H";
		case MISS:
			return "M";
		case OCEAN:
			return "";
		case SHIP:
			return "S";
		default: 
			return "NULL";		
		}		
	}
	
	public void updateText(){
		switch(markerType){
		case OCEAN:
			this.setText("");
			break;
		case SHIP:
			this.setText("S");
			break;
		case HIT:
			this.setText("H");
			break;
		case MISS:
			this.setText("M");
			break;
		default:
			break;
		}
	}
	
	//mmarker exception thrown if the marker has already been hit
	public static class HitMarkerException extends Exception {
		private static final long serialVersionUID = 1L;
		public HitMarkerException(){}
		public HitMarkerException(String msg){
			super(msg);
		}
	}
}
