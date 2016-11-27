package battleship.main;

import javax.swing.*;
import java.awt.*;
import battleship.player.*;

public class GUIGameStart extends Thread{
	//Starts the Game
	public void run() {
		
		Player player1 = new Human();
		Player player2 = new AI();

		JInternalFrame gridFrame = new JInternalFrame("Battle Grid");
		
		GUIMain.addFrame(gridFrame);
		//sets the internal frame
		gridFrame.setLayout(new GridLayout(2, 1));
		gridFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
		gridFrame.setSize(500, 500);
		gridFrame.setVisible(true);
		gridFrame.setMaximizable(true);
		gridFrame.setResizable(true);
		
		player2.addAsHostile(gridFrame);
		player1.addToFrame(gridFrame);
		
		//starts the game 
		Thread playerInput = new LocalGameState(player1, player2);
		playerInput.start();
	}
}