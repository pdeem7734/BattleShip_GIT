package battleship.player;

import java.util.ArrayList;

import battleship.board.*;

import javax.swing.*;

public abstract class Player extends Thread{
	 protected GUIShip[] ships;
	 protected boolean hasPlacedShips;
     protected GUIGameBoard myBoard;
     protected Player hostilePlayer;
     
     
     //constructor creates a game board by default
     public Player(){
    	 myBoard = new GUIGameBoard(this);
     }
     
     //creates a new game board for this player
     public void clearBoard(){
    	 myBoard.generateNewGrid();
     }
     
     public void unlockHostileBoard(){
    	 this.myBoard.unlockHostileBoard();
     }
     //enables the players turn
     public void run(){
    	 if (!hasPlacedShips) {
    		 requestPlaceShips();
    		 hasPlacedShips = true;
    	 } else {
    		 requestMove();
    	 }
     }
     
     //sets the players opponent 
     public void setOpponent(Player hostilePlayer){
    	 if (this.hostilePlayer == null){
    		this.hostilePlayer = hostilePlayer; 
    	 	hostilePlayer.setOpponent(this);
    	 } else return; 
     }     
     
     //all players must have a way to be hit
     public GUIShip hitMarker(int xPos, int yPos) throws GUIBoardMarker.HitMarkerException{ 
		 return myBoard.hitMarker(xPos, yPos);
     }
     
     public boolean hasWon(){
    	 for (GUIShip ship : hostilePlayer.ships){
    		 if (!ship.isShipSunk()) return false;
    	 }
    	 return true;
     }
     
     public void addToFrame(JInternalFrame frame){
    	 frame.add(myBoard);
     }
     
     //adds all ships from a string to the player.
     public void addAllShips(String shipString){
 		String[] shipStrings = shipString.split("|");
 		ArrayList<GUIShip> arrayListShips = new ArrayList<GUIShip>();
 		for (String singleShip : shipStrings) {
 			String[] temp = singleShip.split(",");
 			int[] yxPos = new int[2];
 			yxPos[0] = Integer.parseInt(temp[2]);
 			yxPos[1] = Integer.parseInt(temp[3]);
 			arrayListShips.add(new GUIShip(Integer.parseInt(temp[0]), GUIShip.Orientation.valueOf(temp[1]), yxPos, temp[4]));
 		}
 		
 		ships = arrayListShips.toArray(new GUIShip[arrayListShips.size() -1]);
 		
 		for(GUIShip ship:ships) {
 			try {
 			ship.placeShip(myBoard);
 			} catch (Exception e) {
 				//this should never occur as the remote client should have run the same rule checks. 
 			}
 		}
     }
     
     //returns a string value for the ships the player has.
     //ships will be separated by a '|'
     public String getShipsString() {
    	 StringBuffer returnBuffer = new StringBuffer();
    	 
    	 for(GUIShip ship: ships) {
    		 returnBuffer.append(ship.toString() + "|");
    	 }
    	 return returnBuffer.toString();
     }
     
     public void addAsHostile(JInternalFrame frame){
    	 frame.add(myBoard.getHostileBoard());
     }
     //requests that the player makes their move on the board
     abstract public void requestMove();
     abstract public void requestPlaceShips();
}
