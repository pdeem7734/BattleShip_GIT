package battleship.player;

import java.io.IOException;
import java.util.*;

import battleship.board.*;
import battleship.main.*;

public class RemoteClientHuman extends Player {
	
	private BattleServer localServer;
	
	public RemoteClientHuman(BattleServer localServer) {
		this.localServer = localServer;
	}
	
	
	@Override
	public void requestMove() { //requests a move from the remote client
		String xyPos = "";
		try {
			xyPos = localServer.requestFromRemote("startMove");
		} catch (IOException e) {
			GUIMain.appendText("Remote Player has disconnected\n");
		}
		
		try {
			hostilePlayer.hitMarker(Integer.parseInt(xyPos.split(",")[0]), Integer.parseInt(xyPos.split(",")[1]));
		} catch (Exception e) {
			GUIMain.appendText("Hostile Player Move Invalid\n");
		}
	}

	@Override
	public void requestPlaceShips() { //requests that the remote client place a ship
		String responce = "";
		try {
			responce = localServer.requestFromRemote("placeShips");
		} catch (IOException e) {
			GUIMain.appendText("Remote Player has disconnected\n");
		}
		
		//From here is where we will need to instantiate and place each ship to match from client and host
		if(!responce.equals("placed")) {
			throw new Error ("Remote Placement Failed");
		}
		
		addAllShips(responce);
	}
	
	
	@Override
    public GUIShip hitMarker(int xPos, int yPos) throws GUIBoardMarker.HitMarkerException{
		GUIShip returnShip = myBoard.hitMarker(xPos, yPos);
		try {
			String response = localServer.requestFromRemote("hostMove:" + xPos + "," + yPos);
			if (!response.equals("updatedBoard")) {
				throw new Error("Invalid Response from Client");
			}
		} catch (IOException e) {
			GUIMain.appendText("Remote Host disconnected\n");
		}
		return returnShip;
    }
}
