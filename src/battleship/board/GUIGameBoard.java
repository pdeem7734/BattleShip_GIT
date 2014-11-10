package battleship.board;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import battleship.main.GUIMain;

@SuppressWarnings("serial")
public class GUIGameBoard extends JPanel{
	
	//this is stored in [y][x]
	GUIBoardMarker[][] boardGrid = new GUIBoardMarker[10][10];	
	HostileGUIGameBoard hostileBoard;
	private boolean isLocked;
	
	public GUIGameBoard(){
		this.setLayout(new GridLayout(10,10));
		this.setSize(800,800);
		this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.setVisible(true);
		if (!(this instanceof HostileGUIGameBoard)){			
			generateNewGrid();
			isLocked = false;
		}
	}
	
	public void generateNewGrid() {
		//generates a new grid
		//for both the hostile  and the current grid
		hostileBoard = new HostileGUIGameBoard(this);
		ActionListener actionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent actionEvent){
				GUIBoardMarker temp = (GUIBoardMarker) actionEvent.getSource();
				try{
					hitButton(temp);
				} catch (Exception e){
					//not doing anything with it at this time
				}
			}
		};
		this.removeAll();
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				//adds the friendly button
				GUIBoardMarker button = new GUIBoardMarker();
				boardGrid[i][k] = button;					
				button.addActionListener(actionListener);
				this.add(button);
				
				//makes the friendly button un-clickable
				//don't want you to be able to shoot your own ship...
				button.setEnabled(false);
				
				//adds the hostile button to the hostile board 
				GUIBoardMarker hostileButton = new GUIBoardMarker();
				hostileBoard.boardGrid[i][k] = hostileButton;
				hostileButton.addActionListener(actionListener);
				hostileBoard.add(hostileButton);
				hostileButton.setEnabled(false);
			}
		}
	}
	
	//unlocks the buttons on the board to accept player input
	public void unlockHostileBoard(){
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				if(!this.hostileBoard.boardGrid[i][k].getText().equals("H") && 
						!this.hostileBoard.boardGrid[i][k].getText().equals("M")){
					this.hostileBoard.boardGrid[i][k].setEnabled(true);
					
				}
			}
		}
		isLocked = false;
	}
	
	//unlocks buttons on the board to accept player input
	public void lockHostileBoard(){
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				this.hostileBoard.boardGrid[i][k].setEnabled(false);
				
			}
		}
		isLocked = true;
	}
	
	public boolean isLocked() {
		return isLocked;
	}
	
	public void hitButton(GUIBoardMarker button){
		//cycle though the elements looks for the correct button
		//in the friendly board only
		this.setEnabled(false);
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				if (boardGrid[i][k] == button){
					try {
						//this logic will never really be run.
						//button here represents the friendly board gird
						GUIBoardMarker oButton = hostileBoard.boardGrid[i][k];
						button.hitMarker();
						oButton.hitMarker();
						oButton.setText(button.toString());
						hostileBoard.lockHostileBoard();
						return;
					} catch (Exception e){
						//doing nothing with it for the time being 
					}
				}
			}
		}
		//if it can't find the specific button it came from the hostile board instance
		//locate it and update both views. 
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				if (hostileBoard.boardGrid[i][k] == button){
					try {
						//oButton here represents the friendly board grid
						GUIBoardMarker oButton = this.boardGrid[i][k];
						GUIShip tempShip = oButton.hitMarker();
						button.hitMarker();
						GUIMain.appendText("Hit at (" + k + "," + i+ ")\n");
						
						if (tempShip != null && tempShip.isShipSunk()){
							GUIMain.appendText(tempShip.shipName + " Has Been Sunk\n");
						}
						
						button.setText(this.boardGrid[i][k].toString());
						this.lockHostileBoard();
					} catch (Exception e){
						//doing nothing with this currently
					}					
				}
			}
		}
	}
	
	
	
	GUIBoardMarker getMarker(int xpos, int ypos){
		return boardGrid[ypos][xpos];
	}
	
	//this is only called by the AI to imitate a button click
	public GUIShip hitMarker(int xpos, int ypos) throws GUIBoardMarker.HitMarkerException{
		try {
			GUIShip temp = boardGrid[ypos][xpos].hitMarker();
			hostileBoard.boardGrid[ypos][xpos].hitMarker();
			hostileBoard.boardGrid[ypos][xpos].setText(boardGrid[ypos][xpos].toString());
			return temp;
		} catch (ArrayIndexOutOfBoundsException e){
			throw new GUIBoardMarker.HitMarkerException("Invalid GRID");
		}
		
	}
	public HostileGUIGameBoard getHostileBoard(){
		return hostileBoard;
	}
	
	//this works surprisingly well...
	static private class HostileGUIGameBoard extends GUIGameBoard{	
		public HostileGUIGameBoard(GUIGameBoard baseBoard){
		}
	}
	
	public void highlightForPlacement(int[] xyPosition, GUIShip.Orientation orientation, int length) {
		resetBackground();
		//the the ship placement thing colors
		try {
			for (int x = 0; x < length; x++){
				switch (orientation){
				case NORTH:
					boardGrid[xyPosition[1] - x][xyPosition[0]].setBackground(new Color(0,255,0));
					break;
				case SOUTH:
					boardGrid[xyPosition[1] + x][xyPosition[0]].setBackground(new Color(0,255,0));
					break;
				case EAST:
					boardGrid[xyPosition[1]][xyPosition[0] + x].setBackground(new Color(0,255,0));
					break;
				case WEST:
					boardGrid[xyPosition[1]][xyPosition[0] - x].setBackground(new Color(0,255,0));
					break;
				}
			}
		} catch (Exception e) {
			//doing nothing with this. 
		}
	}
	public void resetBackground() {
		//revert all prior colors 
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				boardGrid[i][k].setBackground(hostileBoard.getBackground());
			}
		}
	}
}
