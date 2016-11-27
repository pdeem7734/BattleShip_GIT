package battleship.board;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import battleship.main.GUIMain;
import battleship.player.Player;

@SuppressWarnings("serial")
public class GUIGameBoard extends JPanel{
	
	//this is stored in [y][x]
	private GUIBoardMarker[][] boardGrid = new GUIBoardMarker[10][10];
	private HostileGUIGameBoard hostileBoard;
	private Player playerOwner;
	private boolean isLocked;
	
	public GUIGameBoard(Player playerOwner){
		this.setPlayerOwner(playerOwner);
		this.setLayout(new GridLayout(10,10));
		this.setSize(800,800);
		this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.setVisible(true);
		if (!(this instanceof HostileGUIGameBoard)){			
			generateNewGrid();
			setIsLocked(false);
		}
	}
	
	public void generateNewGrid() {
		//generates a new grid
		//for both the hostile  and the current grid
		setHostileBoard(new HostileGUIGameBoard(getPlayerOwner()));
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
				getBoardGrid()[i][k] = button;
				button.addActionListener(actionListener);
				this.add(button);
				
				//makes the friendly button un-clickable
				//don't want you to be able to shoot your own ship...
				button.setEnabled(false);
				
				//adds the hostile button to the hostile board 
				GUIBoardMarker hostileButton = new GUIBoardMarker();
				getHostileBoard().getBoardGrid()[i][k] = hostileButton;
				hostileButton.addActionListener(actionListener);
				getHostileBoard().add(hostileButton);
				hostileButton.setEnabled(false);
			}
		}
	}
	
	//unlocks the buttons on the board to accept player input
	public void unlockHostileBoard(){
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				if(!this.getHostileBoard().getBoardGrid()[i][k].getText().equals("H") &&
						!this.getHostileBoard().getBoardGrid()[i][k].getText().equals("M")){
					this.getHostileBoard().getBoardGrid()[i][k].setEnabled(true);
					
				}
			}
		}
		setIsLocked(false);
	}
	
	//unlocks buttons on the board to accept player input
	public void lockHostileBoard(){
		for (int i = 0; i < 10; i++ ){
			for (int k =0; k<10 ; k++){
				this.getHostileBoard().getBoardGrid()[i][k].setEnabled(false);
				
			}
		}
		setIsLocked(true);
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
				if (getBoardGrid()[i][k] == button){
					try {
						//this logic will never really be run.
						//button here represents the friendly board gird
						getPlayerOwner().hitMarker(k, i);
						getHostileBoard().lockHostileBoard();
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
				if (getHostileBoard().getBoardGrid()[i][k] == button){
					try {
						//oButton here represents the friendly board grid
						GUIShip tempShip = getPlayerOwner().hitMarker(k, i);
						GUIMain.appendText("Hit at (" + k + "," + i+ ")\n");
						
						if (tempShip != null && tempShip.isShipSunk()){
							GUIMain.appendText(tempShip.shipName + " Has Been Sunk\n");
						}
						
						this.lockHostileBoard();
					} catch (Exception e){
						//doing nothing with this currently
					}					
				}
			}
		}
	}
	
	
	
	GUIBoardMarker getMarker(int xpos, int ypos){
		return getBoardGrid()[ypos][xpos];
	}
	
	//this is only called by the AI to imitate a button click
	public GUIShip hitMarker(int xpos, int ypos) throws GUIBoardMarker.HitMarkerException{
		try {
			GUIShip temp = getBoardGrid()[ypos][xpos].hitMarker();
			getHostileBoard().getBoardGrid()[ypos][xpos].hitMarker();
			getHostileBoard().getBoardGrid()[ypos][xpos].setText(getBoardGrid()[ypos][xpos].toString());
			return temp;
		} catch (ArrayIndexOutOfBoundsException e){
			throw new GUIBoardMarker.HitMarkerException("Invalid GRID");
		}
		
	}
	public HostileGUIGameBoard getHostileBoard(){
		return hostileBoard;
	}

	GUIBoardMarker[][] getBoardGrid() {
		return boardGrid;
	}

	void setBoardGrid(GUIBoardMarker[][] boardGrid) {
		this.boardGrid = boardGrid;
	}

	void setHostileBoard(HostileGUIGameBoard hostileBoard) {
		this.hostileBoard = hostileBoard;
	}

	Player getPlayerOwner() {
		return playerOwner;
	}

	void setPlayerOwner(Player playerOwner) {
		this.playerOwner = playerOwner;
	}

	void setIsLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	//this works surprisingly well...
	static private class HostileGUIGameBoard extends GUIGameBoard{	
		public HostileGUIGameBoard(Player playerOwner){
			super(playerOwner);
		}
	}
	
	public void highlightForPlacement(int[] xyPosition, GUIShip.Orientation orientation, int length) {
		resetBackground();
		//the the ship placement thing colors
		try {
			for (int x = 0; x < length; x++){
				switch (orientation){
				case NORTH:
					getBoardGrid()[xyPosition[1] - x][xyPosition[0]].setBackground(new Color(0,255,0));
					break;
				case SOUTH:
					getBoardGrid()[xyPosition[1] + x][xyPosition[0]].setBackground(new Color(0,255,0));
					break;
				case EAST:
					getBoardGrid()[xyPosition[1]][xyPosition[0] + x].setBackground(new Color(0,255,0));
					break;
				case WEST:
					getBoardGrid()[xyPosition[1]][xyPosition[0] - x].setBackground(new Color(0,255,0));
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
				getBoardGrid()[i][k].setBackground(getHostileBoard().getBackground());
			}
		}
	}
}
