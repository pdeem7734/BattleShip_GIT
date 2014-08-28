package battleship.main;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class GUIMenu extends Thread{
	private JInternalFrame frame;
	private JButton startButton;
	private JButton exitButton;
	
	//main run command 
	@Override
	public void run() {
		
		frame = new JInternalFrame();
		frame.setLayout(new GridLayout(2,1));
		frame.setVisible(true);
		frame.setResizable(true);
		frame.setSize(200,100);
		
		startButton = new JButton("Start Game");
		exitButton = new JButton("Exit Game");
		
		ActionListener startListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				  startGame();
			}
		};
		startButton.addActionListener(startListener);
		
		ActionListener exitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				exitGame();
			}
		};
		exitButton.addActionListener(exitListener);
		
		frame.add(startButton);
		frame.add(exitButton);
		
		GUIMain.addFrame(frame);
	}
	
	//command to run if we request to start a new game
	private void startGame(){
		GUIGameStart game = new GUIGameStart();
		game.start();
		
		frame.setVisible(false);
		frame.dispose();
	}
	
	//command to run if we want to exit the program
	private void exitGame() {
		System.exit(NORM_PRIORITY);		
	}	
}
