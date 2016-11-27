package battleship.main.multiplayer;

import java.util.concurrent.*;
import java.io.*;

import battleship.main.GUIMain;
import battleship.player.*;
import battleship.player.multiplayer.RemoteClientHuman;

public class HostGameState extends Thread {
	private Player localPlayer;
	private Player remotePlayer;
	
	private BattleServer hostServer;
	
	ArrayBlockingQueue<Player> turnQueue = new ArrayBlockingQueue<Player>(3);
	
	public HostGameState(Player player1) throws IOException{
		hostServer = new BattleServer();
		this.localPlayer = player1;
		this.remotePlayer = new RemoteClientHuman(hostServer);
		
		this.localPlayer.setOpponent(remotePlayer);
		this.remotePlayer.setOpponent(player1);
		
		turnQueue.add(this.localPlayer);
		turnQueue.add(this.remotePlayer);
	}
	
	public void run() {
		//starts the battle server
		hostServer.run();
		GUIMain.appendText("Waiting for opponent to join...\n");
		
		//waits for an opponent to connect
		while (!hostServer.opponentConnected()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {} //doing nothing with this presently 
		}
		GUIMain.appendText("Opponent has connected\n");
		
		//requests both players place their ships		
		GUIMain.appendText("Please place your ships!\n");
		localPlayer.start();
		remotePlayer.start();
		
		//waits for both players to complete placing their ships 
		try {
			localPlayer.join();
			remotePlayer.join();
		} catch (InterruptedException e) {} //doing nothing with this currently
		
		//Adds the ships from host client to the remote client
		//and vice versa
		try {
			String remoteShips = hostServer.requestFromRemote("updateBoard:" + localPlayer.getShipsString());
			remotePlayer.addAllShips(remoteShips);
		} catch(IOException e) {
			GUIMain.appendText("Remote client disconneted ");
		}
		
		GUIMain.appendText("All players have placed their ships\n");
		
		//requests turns from both players and checks for win conditions.  
		while (true){
			try{
				Player currentPlayer = turnQueue.take();
				currentPlayer.run();
				turnQueue.put(currentPlayer);
				if (currentPlayer.hasWon()){
					hostServer.requestFromRemote("gameOver");
					GUIMain.appendText("Some One Won!\n");
					break;
				}
			} catch (Exception e){
				System.out.println(e);
				//do nothing currently
			}
		}
	}
}
