package battleship.main;

import java.awt.BorderLayout;

import javax.swing.*;

//this class exists as static as it is the main entry point and controller for the entire program
public class GUIMain extends Thread{
	private static JDesktopPane desktopPane;
	private static JTextArea textArea;

	@Override
	public void run() {
		JFrame frame = new JFrame("Battle Ship");
				
		//the spaces below are a /bad/ response to the text box for console simulated output not displaying correctly
		textArea = new JTextArea("Welcome To BattleShip!                                \n");
		JScrollPane scrollPane = new JScrollPane(textArea);
		desktopPane = new JDesktopPane();
		JPanel panel = new JPanel();
		
		//add the layout for the panel 
		//add it to the frame and set param's for the frame
		panel.setLayout(new BorderLayout());
		frame.add(panel);
		frame.setVisible(true);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//text area should not be editable
		textArea.setEditable(false);
		
		//add the content to the panel
		panel.add(desktopPane);
		panel.add(scrollPane, BorderLayout.LINE_END);
		
	}
	
	//this is used to add an internal frame to the desktop pane
	public static void addFrame(JInternalFrame com){
		desktopPane.add(com,0);
	}
	
	//this is used to add text to the 'console' output
	public static void appendText(String updateText) {
		textArea.append(updateText);

	}
	
	
	//Entry point for the program
	public static void main (String[] args){
		//creates and starts the program and the containing frame
		GUIMain program = new GUIMain();
		program.run();
		
		//starts the menu for the game 
		GUIMenu menu = new GUIMenu();
		menu.start();
	}

}
