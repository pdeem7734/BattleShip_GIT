package battleship.player;

import java.util.ArrayList;

import battleship.board.GUIShip;
import battleship.main.GUIMain;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class Human extends Player{
	@Override
	public void requestMove(){
		//threading has happened!
		hostilePlayer.unlockHostileBoard();
		while (!hostilePlayer.myBoard.isLocked()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e){
				//do nothing
			}
		}
	}
		
	@Override
	public void requestPlaceShips(){		
		//nested class to hold the ship placement UI
		//this seems to be quickly becoming to complex for a simple nested class
		class PlacementUI extends Thread {
			JButton orientButton;
			JButton placeShipButton;
			JButton clearButton;
			JButton submitButton;
			
			JPanel xLabelPanel;
			JPanel yLabelPanel;
			
			JButton xUpButton;
			JButton xDownButton; 			
			JButton yUpButton;
			JButton yDownButton;
			
			JLabel xLabel;
			JLabel yLabel;
			JLabel shipLabel;
			JLabel holderLabel;
			JTextField xTextField;
			JTextField yTextField;
			
			JInternalFrame shipPlacementFrame;
			ArrayList<GUIShip> shipAL = new ArrayList<GUIShip>();
			
			int curentShip = 0;
			int[] shipLengths = {6, 4, 3 ,3 ,2, 0};
			boolean shipsPlaced = false;
					
			public void run() {
				shipPlacementFrame = new JInternalFrame("Place Ships");
				JPanel buttonPanel = new JPanel();
				JPanel textPanel = new JPanel();
				
				buttonPanel.setLayout(new GridLayout(2,2));
				textPanel.setLayout(new GridLayout(3,2));
				
				shipPlacementFrame.setLayout(new GridLayout(2,1));
				shipPlacementFrame.setSize(500, 150);
				shipPlacementFrame.setVisible(true);
				shipPlacementFrame.setMaximizable(true);
				
				shipPlacementFrame.add(textPanel);
				shipPlacementFrame.add(buttonPanel);
				GUIMain.addFrame(shipPlacementFrame);
				
				//listener for orientation
				ActionListener orientListener = new ActionListener() {					
					@Override
					public void actionPerformed(ActionEvent e) {
						JButton source = (JButton) e.getSource();
						GUIShip.Orientation currentOrient = GUIShip.Orientation.valueOf(source.getText());
						switch (currentOrient){
						case NORTH:
							source.setText("SOUTH");
							break;
						case SOUTH:
							source.setText("EAST");
							break;
						case EAST:
							source.setText("WEST");
							break;
						case WEST:
							source.setText("NORTH");
							break;					
						}
						updateGridPlacement();
					}
				};
				
				//listener for placement 
				ActionListener placementListener = new ActionListener () {
					@Override
					public void actionPerformed(ActionEvent e){
						//attempt to place the ship with the stuff specified
						placeShip();
					}
				};
				
				//listener for the clear grid
				ActionListener clearListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						clearGrid();
					}
				};
				
				//listener for submit
				ActionListener submitListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						submitShips();
					}
				};
				
				
				//create and add all the buttons to the button panel
				orientButton = new JButton("NORTH");								
				orientButton.addActionListener(orientListener);
				orientButton.setToolTipText("Orientation of the ship to be placed");
				
				placeShipButton = new JButton("Place Ship");
				placeShipButton.addActionListener(placementListener);
				placeShipButton.setToolTipText("Attempt to place ship with current settings");
				
				clearButton = new JButton("Clear Grid");
				clearButton.addActionListener(clearListener);
				clearButton.setToolTipText("Resets your ship Placement");
				
				submitButton = new JButton("Submit Placement");
				submitButton.addActionListener(submitListener);
				submitButton.setToolTipText("Finalizes your ship placement");
				submitButton.setEnabled(false);
				
				buttonPanel.add(orientButton);
				buttonPanel.add(placeShipButton);
				buttonPanel.add(clearButton);
				buttonPanel.add(submitButton);
				
				//create and add all components of the textpanel
				
				ActionListener xListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e){
						JButton source = (JButton) e.getSource();
						//0 is up 1 is down
						if (source.getText().equals("Up")) changeXOrigin(1);
						else changeXOrigin(0);
					}
				};
				
				ActionListener yListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e){
						JButton source = (JButton) e.getSource();
						//0 is right 1 is left
						if (source.getText().equals("Right")) changeYOrigin(0);
						else changeYOrigin(1);
					}
				};
				
				
				
				//label panel will allow adding buttons next to the label
				//create and add all x elements first
				xLabelPanel = new JPanel();
				xLabelPanel.setLayout(new GridLayout(1,3));
				
				xUpButton = new JButton("Up");
				xDownButton = new JButton("Down");				
				xUpButton.addActionListener(xListener);
				xDownButton.addActionListener(xListener);
				
				xLabel = new JLabel("X Start: ");
				xTextField = new JTextField();
				xTextField.setText("1");
				xTextField.setEditable(false);
				
				xLabelPanel.add(xLabel);
				xLabelPanel.add(xUpButton);
				xLabelPanel.add(xDownButton);
				
				//add all y elements after this
				yLabelPanel = new JPanel();
				yLabelPanel.setLayout(new GridLayout(1,3));
				
				yUpButton = new JButton("Left");
				yDownButton = new JButton("Right");
				yUpButton.addActionListener(yListener);
				yDownButton.addActionListener(yListener);
				
				
				yLabel = new JLabel("Y Start: ");
				shipLabel = new JLabel("Selection for ship of length: " + shipLengths[curentShip]);
				holderLabel = new JLabel();				
				yTextField = new JTextField();
				yTextField.setText("1");
				yTextField.setEditable(false);
				
				yLabelPanel.add(yLabel);
				yLabelPanel.add(yUpButton);
				yLabelPanel.add(yDownButton);
				
				textPanel.add(shipLabel);
				textPanel.add(holderLabel);
				textPanel.add(xLabelPanel);
				textPanel.add(xTextField);
				textPanel.add(yLabelPanel);
				textPanel.add(yTextField);
				
				updateGridPlacement();
				shipPlacementFrame.updateUI();
				try {
					while (!shipsPlaced){
						Thread.sleep(500);
					}
					this.close();
				} catch (Exception e) {
					
				}
				updateGridPlacement();
			}
			
			private void changeXOrigin(int dir) {
				//0 moves the origin up
				//1 moves the origin down
				int curentXOrigin = Integer.parseInt(xTextField.getText());	
				switch (dir) {
				case 0:
					if (curentXOrigin < 10)	curentXOrigin ++;
					else curentXOrigin = 1;
					break;
				case 1:
					if (curentXOrigin > 1)	curentXOrigin --;
					else curentXOrigin = 10;
					break;
				default:
					throw new AssertionError("Invaid Argument type");
				}
				xTextField.setText(String.valueOf(curentXOrigin));
				updateGridPlacement();
			}

			private void changeYOrigin(int dir) {
				//0 moves the origin right
				//1 moves the origin left
				int curentYOrigin = Integer.parseInt(yTextField.getText());	
				switch (dir) {
				case 0:
					if (curentYOrigin < 10)	curentYOrigin ++;
					else curentYOrigin = 1;
					break;
				case 1:
					if (curentYOrigin > 1)	curentYOrigin --;
					else curentYOrigin = 10;
					break;
				default:
					throw new AssertionError("Invaid Argument type");
				}
				yTextField.setText(String.valueOf(curentYOrigin));
				updateGridPlacement();
			}
			
			private void updateGridPlacement() {
				int[] temp = new int[] {Integer.parseInt(yTextField.getText()) - 1, Integer.parseInt(xTextField.getText()) - 1};
				myBoard.highlightForPlacement(temp, GUIShip.Orientation.valueOf(orientButton.getText()), shipLengths[curentShip]);
			}
			
			//calls the players clear grid
			public void clearGrid() {
				clearBoard();
				curentShip = 0;
				placeShipButton.setEnabled(true);
				submitButton.setEnabled(false);
				updateGridPlacement();
			}
			
			public void submitShips(){
				ships = shipAL.toArray(new GUIShip[shipAL.size()]);
				shipsPlaced = true;
			}
			
			//creates the ship and adds it to the ship array for this player
			private void placeShip(){
				try {
					//sets the x,y origin for the ship
					int[] temp = new int[] {Integer.parseInt(xTextField.getText()) - 1, Integer.parseInt(yTextField.getText()) - 1};
					
					//attempts to create and place the ship
					GUIShip ship = new GUIShip(shipLengths[curentShip], 
							GUIShip.Orientation.valueOf(orientButton.getText()), temp , "Bob");
					ship.placeShip(myBoard);
					shipAL.add(ship);
					
					curentShip++;
					shipLabel.setText("Selection for ship of length: " + shipLengths[curentShip]);			
					
					//if we have reached the end of the defined ships in the game. 
					if (curentShip == 5) {
						placeShipButton.setEnabled(false);
						submitButton.setEnabled(true);
					}
				} catch (Exception e){
					//do a thing if we can't place the ship	);
					GUIMain.appendText("Unable To place ship\n");
					System.out.println(e);
				}
			}
			
			
			//returns the ship array for this player
			GUIShip[] getShipArray() {
				return shipAL.toArray(new GUIShip[shipAL.size()]);
			}
			
			private void close() {
				shipPlacementFrame.setVisible(false);
				shipPlacementFrame.dispose();
			}
		}
		
		//create a new placement UI
		//wait to exit this thread until the ships have been placed. 
		PlacementUI placeShips = new PlacementUI();
		placeShips.run();
		ships = placeShips.getShipArray();
	}
}
