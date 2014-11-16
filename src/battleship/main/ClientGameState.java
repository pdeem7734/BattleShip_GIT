package battleship.main;

import battleship.player.*;
public class ClientGameState extends Thread {
	String hostAddress;
	
	//Player 1 denotes the local player while player two is the remote
	Player player1;
	Player player2;
	
	public ClientGameState(Player player1, String hostAddress) {
		this.hostAddress = hostAddress;
		
		this.player1 = player1;
		this.player2 = new RemoteHostHuman(this);
		
		player1.setOpponent(player2);
		player2.setOpponent(player1);
		
	}
	
	public void run() {
		//TODO: establish the socket connection to the remote host port will be over port 8000
	}
}
