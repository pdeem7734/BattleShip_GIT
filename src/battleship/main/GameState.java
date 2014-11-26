package battleship.main;

import java.util.concurrent.*;
import battleship.player.*;

public class GameState extends Thread{
	//the players in this game
	private Player player1;
	private Player player2;
	
	//the current turn order
	ArrayBlockingQueue<Player> turnQueue = new ArrayBlockingQueue<Player>(3);
	
	//Initialize the game state
	public GameState(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
		
		this.player1.setOpponent(player2);
		this.player2.setOpponent(player1);
		
		turnQueue.add(this.player1);
		turnQueue.add(this.player2);
	}
	
	//start the game
	//TODO: create logic to start the game with both players able to place their ships
	public void run() {
		//simply requests that both players place their ships.
		
		GUIMain.appendText("Please place your ships!\n");
		player1.start();
		player2.start();
		
		//waits for each player to finish placing their ships
		try {
			player1.join();
			player2.join();
		} catch (Exception e){
			//doing nothing with this currently
		}
		
		GUIMain.appendText("All players have placed their ships\n");
		//starts the turn cycling
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
