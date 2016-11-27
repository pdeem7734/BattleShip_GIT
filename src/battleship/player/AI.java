package battleship.player;

import battleship.board.*;


import java.util.*;

public class AI extends Player{
	//definitions for logical thinking for the AI player
	boolean hitAndNotSunk;
	boolean[] attemptedDirection = new boolean[4]; //boolean result for if a direction was attempted for a shot
	//initilize the array to false 
	int orientationChangeCount;
	int firstX;
	int firstY;
	int lastX;
	int lastY;	
	GUIShip.Orientation expectedDirection;
	
     //default constructor	
     public AI(){
	    //initialize the array of ships
		//not sure i like this version of the constructor            
    	super();
     }

     //this will handle placing the ships on the friendly board
     @Override
     public void requestPlaceShips(){
	     ArrayList<GUIShip> ships = new ArrayList<GUIShip>();
	     int[] lengths = {6, 4, 3 ,3 ,2};
	     Random rand = new Random();
	     
	     for (int length : lengths) {
	         boolean shipPlaced = false;
	         do {
	             //function to place the ship
	             try {
	                 int xPos = rand.nextInt(10);
	                 int yPos = rand.nextInt(10);
	                 GUIShip.Orientation or = GUIShip.Orientation.values()[rand.nextInt(4)];
	                 GUIShip cShip = new GUIShip(length,  or, new int [] {yPos,xPos}, "" + length);
	                 cShip.placeShip(myBoard);
	                 shipPlaced = true;
	                 ships.add(cShip);
	             } catch (ShipPlacementException e) {
	            	 continue;
	             }
	         } while(!shipPlaced);
	     }
	     super.ships = ships.toArray(new GUIShip[ships.size()]);
	     
     }
     
     
     @Override
     //basic start of the AI's logic for move selection
     public void requestMove(){
    	 if (hostilePlayer == null) throw new AssertionError("Hostile player must be set first");
    	 
		 if (!hitAndNotSunk) {
			 newMove();
		 } else {
			 existingMove();
		 } 	 
     }
     
     //request the AI makes a completely new move 
     private void newMove() {
    	GUIShip tempShip;
    	Random rand = new Random();
    	boolean shotFailed = true; 
    	int xPos; 
	 	int yPos;
	 	
	 	//if the game is making this call the AI doens't know where a ship might be
	 	hitAndNotSunk = false;
		do {
	    	xPos = rand.nextInt(10);
		 	yPos = rand.nextInt(10);
			try {
				if ((tempShip = hostilePlayer.hitMarker(xPos, yPos)) != null){
					 //this has hit a ship
					 if (tempShip.isShipSunk()) hitAndNotSunk = false;
					 else hitAndNotSunk = true;
					 lastX = firstX = xPos;
					 lastY = firstY = yPos;
				}
				shotFailed = false;
			} catch (GUIBoardMarker.HitMarkerException e) {
				continue; 
			}
		} while (shotFailed);
     }
     
     //simple check to see if all direction have been attempted for this move
     private boolean attemptedAll() {
    	 for (boolean dir: attemptedDirection){
    		 if (!dir) return false;
    	 }
    	 return true;
     }
     
     //move logic for if the AI has hit a ship previously
     private void existingMove() {
    	 GUIShip tempShip;
    	 Random rand = new Random();
    	 boolean shotFailed = true; 
    	 do {
			//requests the AI make a move on an exsisting play
			try {
				if (attemptedAll()) { //if every adjacent direction has been hit, treat like a new move
					for (int i = 0; i < attemptedDirection.length; i++){
						attemptedDirection[i] = false;
					}
					newMove();
					shotFailed = false;
				} else {
					if (expectedDirection == null){ //we have only just hit the ship
						switch(rand.nextInt(4)){
						case 0:
							//attempt to hit the ship
							attemptedDirection[0] = true;
							if ((tempShip = hostilePlayer.hitMarker(lastX, lastY + 1)) != null) {
								//if we are able to hit a ship at this grid mark it
								lastY ++;
								expectedDirection = GUIShip.Orientation.NORTH; 
								if (tempShip.isShipSunk()) hitAndNotSunk = false;
							}
							shotFailed = false;
							break;
						case 1:
							//attempt to hit the ship
							attemptedDirection[1] = true;
							if ((tempShip = hostilePlayer.hitMarker(lastX, lastY - 1)) != null) {
								//if we are able to hit a ship at this grid mark it
								lastY --;
								expectedDirection = GUIShip.Orientation.SOUTH; 
								if (tempShip.isShipSunk()) hitAndNotSunk = false;
							}
							shotFailed = false;
							break;
						case 2:
							//attempt to hit the ship
							attemptedDirection[2] = true;
							if ((tempShip = hostilePlayer.hitMarker(lastX + 1, lastY)) != null) {
								//if we are able to hit a ship at this grid mark it
								lastX ++;
								expectedDirection = GUIShip.Orientation.EAST; 
								if (tempShip.isShipSunk()) hitAndNotSunk = false;
							}
							shotFailed = false;
							break;								
						case 3:
							//attempt to hit the ship
							attemptedDirection[3] = true;
							if ((tempShip = hostilePlayer.hitMarker(lastX - 1, lastY)) != null) {
								//if we are able to hit a ship at this grid mark it
								lastX --;
								expectedDirection = GUIShip.Orientation.WEST; 
								if (tempShip.isShipSunk()) hitAndNotSunk = false;
							}
							shotFailed = false;
							break;
						}
						
					} else { //we have already hit two spaces in a row
						switch(expectedDirection){
						case NORTH:
							//attempt to hit in the expected direction
							attemptedDirection[1] = true;
							if ((tempShip = hostilePlayer.hitMarker(lastX, ++lastY)) != null){
								//was able to hit ship
								//check to see if the ship has been sunk
								if (tempShip.isShipSunk()) hitAndNotSunk = false;									
							} else {
								//should this fail reset and attempt from the other direction
								expectedDirection = GUIShip.Orientation.SOUTH;
								orientationChangeCount++;
								lastX = firstX;
								lastY = firstY;
							}
							shotFailed = false;
							break;
						case SOUTH:
							//attempt to hit in the expected direction
							attemptedDirection[1] = true;
							if ((tempShip = hostilePlayer.hitMarker(lastX, --lastY)) != null){
								//was able to hit ship
								//check to see if the ship has been sunk
								if (tempShip.isShipSunk()) hitAndNotSunk = false;									
							} else {
								//should this fail reset and attempt from the other direction
								expectedDirection = GUIShip.Orientation.NORTH;
								orientationChangeCount++;
								lastX = firstX;
								lastY = firstY;
							}
							shotFailed = false;
							break;
						case EAST:
							//attempt to hit in the expected direction
							attemptedDirection[3] = true;
							if ((tempShip = hostilePlayer.hitMarker(++lastX, lastY)) != null){
								//was able to hit ship
								//check to see if the ship has been sunk
								if (tempShip.isShipSunk()) hitAndNotSunk = false;									
							} else {
								//should this fail reset and attempt from the other direction
								expectedDirection = GUIShip.Orientation.WEST;
								orientationChangeCount++;
								lastX = firstX;
								lastY = firstY;
							}
							shotFailed = false;
							break;
						case WEST:
							//attempt to hit in the expected direction
							attemptedDirection[3] = true;
							if ((tempShip = hostilePlayer.hitMarker(--lastX, lastY)) != null){
								//was able to hit ship
								//check to see if the ship has been sunk
								if (tempShip.isShipSunk()) hitAndNotSunk = false;									
							} else {
								//should this fail reset and attempt from the other direction
								expectedDirection = GUIShip.Orientation.EAST;
								orientationChangeCount++;
								lastX = firstX;
								lastY = firstY;
							}
							shotFailed = false;
							break;							
						}							
					}
				}
			} catch (GUIBoardMarker.HitMarkerException e) {
				//should hitting the next markerfail set the expected direction to the opposite (if initilized) 
				//do nothing if it is not
				if (expectedDirection !=null){
					switch(expectedDirection){
					case NORTH:
						expectedDirection = GUIShip.Orientation.SOUTH;
						orientationChangeCount++;
						lastX = firstX;
						lastY = firstY;
						break;
					case SOUTH:
						expectedDirection = GUIShip.Orientation.NORTH;
						orientationChangeCount++;
						lastX = firstX;
						lastY = firstY;
						break;
					case EAST:
						expectedDirection = GUIShip.Orientation.WEST;
						orientationChangeCount++;
						lastX = firstX;
						lastY = firstY;
						break;
					case WEST:
						expectedDirection = GUIShip.Orientation.EAST;
						orientationChangeCount++;
						lastX = firstX;
						lastY = firstY;
						break;
					}
				}
			}			
			//checks to see the number of times the current orientation has changed
			//if it has reversed more then once it resets it
			//this will keep the AI from getting stuck in a loop (i hope)
			if (orientationChangeCount >1){
				orientationChangeCount = 0;
				expectedDirection = null;
			}
		} while (shotFailed);    	 
     }
}






