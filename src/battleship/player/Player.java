package battleship.player;

import battleship.board.*;
import javax.swing.*;

public abstract class Player extends Thread{
	 GUIShip[] ships;
	 boolean hasPlacedShips;
     GUIGameBoard myBoard;
     Player hostilePlayer;
     
     
     //constructor creates a game board by default
     public Player(){
    	 myBoard = new GUIGameBoard();
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
     
     //returns ships stored by player. 
     public GUIShip[] getShips() {
    	 return ships;
     }
     
     public void addAsHostile(JInternalFrame frame){
    	 frame.add(myBoard.getHostileBoard());
     }
     //requests that the player makes their move on the board
     abstract public void requestMove();
     abstract public void requestPlaceShips();  
}
