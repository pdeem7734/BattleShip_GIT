package battleship.player;

import java.io.IOException;

import battleship.board.GUIBoardMarker;
import battleship.board.GUIShip;
import battleship.main.*;

public class RemoteHostHuman extends Player {
	ClientGameState gameStateControl;
	
	public RemoteHostHuman(ClientGameState gameStateControl) {
		this.gameStateControl = gameStateControl; 
	}
	
	@Override
	public void requestMove() {
		// TODO Auto-generated method stub
		throw new AssertionError("Meaningless Meathod, move controled by host");
	}
	@Override
	public void requestPlaceShips() {
		// TODO Auto-generated method stub
		throw new AssertionError("Meaningless Meathod, placement controled by host");
	}
	
	public void makeMove(Integer xPos, Integer yPos) {
		try {
			hostilePlayer.hitMarker(xPos, yPos);
		} catch (Exception e) {
			//doing nothing with this currently
		}
	}
	
    //Overriding the default hit placement to communicate to the host where a hit was placed. 
	@Override
    public GUIShip hitMarker(int xPos, int yPos) throws GUIBoardMarker.HitMarkerException{
		GUIShip returnShip = myBoard.hitMarker(xPos, yPos);
		try {
			gameStateControl.respondToRemoteHost(xPos + "," + yPos);
		} catch (IOException e) {
			GUIMain.appendText("Remote Host disconnected\n");
		}
		return returnShip;
    }
}
