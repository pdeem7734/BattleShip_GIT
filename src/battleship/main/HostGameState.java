package battleship.main;

import java.util.concurrent.*;
import java.io.*;

import battleship.player.*;

public class HostGameState extends Thread {
	private Player player1;
	private Player player2;
	
	private BattleServer hostServer; 
	
	ArrayBlockingQueue<Player> turnQueue = new ArrayBlockingQueue<Player>(3);
	
	public HostGameState(Player player1) throws IOException{
		hostServer = new BattleServer();
		this.player1 = player1;
		this.player2 = new RemoteClientHuman(hostServer);
		
		this.player1.setOpponent(player2);
		this.player2.setOpponent(player1);
		
		turnQueue.add(this.player1);
		turnQueue.add(this.player2);
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
		GUIMain.appendText("Opponent has connected");
		
		//requests both players place their ships		
		GUIMain.appendText("Please place your ships!\n");
		player1.start();
		player2.start();
		
		try {
			player1.join();
			player2.join();
		} catch (InterruptedException e) {} //doing nothing with this currently
		
		GUIMain.appendText("All players have placed their ships\n");
		
		while (true){
			try{
				Player currentPlayer = turnQueue.take();
				currentPlayer.run();
				turnQueue.put(currentPlayer);
				if (currentPlayer.hasWon()){
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
