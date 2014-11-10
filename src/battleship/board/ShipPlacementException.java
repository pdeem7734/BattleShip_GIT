package battleship.board;

public class ShipPlacementException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5332441125520999974L;
	public ShipPlacementException(){}
	public ShipPlacementException(String message){
		super(message);
	}
}
