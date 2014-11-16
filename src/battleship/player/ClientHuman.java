package battleship.player;

import java.io.IOException;

import battleship.main.*;

public class ClientHuman extends Player {
	
	private BattleServer localServer;
	
	public ClientHuman(BattleServer localServer) {
		this.localServer = localServer;
	}
	
	//both of these methods will prompt the remote client to execute it's run, which will decide which task needs to be completed
	@Override
	public void requestMove() {
		Integer[] xyPos = new Integer[2];
		try {
			xyPos = localServer.requestRemotePlayerMove();
		} catch (IOException e) {
			GUIMain.appendText("Remote Player has disconnected\n");
		}
		
		try {
			hostilePlayer.hitMarker(xyPos[0], xyPos[1]);
		} catch (Exception e) {
			GUIMain.appendText("Hostile Player Move Invalid\n");
		}
	}

	@Override
	public void requestPlaceShips() {
		try {
			localServer.requestRemotePlayerMove();
		} catch (IOException e) {
			GUIMain.appendText("Remote Player has disconnected\n");
		}
	}

}
