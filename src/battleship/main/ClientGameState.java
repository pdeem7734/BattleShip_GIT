package battleship.main;

import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.io.*;

import battleship.player.*;


public class ClientGameState extends Thread {
	String hostAddress;
	
	Player localPlayer;
	Player remotePlayer;
	
	//create the encoder and decoder
	private CharsetEncoder encoder;
	private CharsetDecoder decoder;
	
	//input buffers for the serversocket channel
	private ByteBuffer hostBytes;
	private CharBuffer hostChar;
	
	//create the server socket channel
	private SocketChannel host;
	
	public ClientGameState(Player player1, String hostAddress) {
		this.hostAddress = hostAddress;
		
		this.localPlayer = player1;
		this.remotePlayer = new RemoteHostHuman(this);
		
		this.localPlayer.setOpponent(remotePlayer);
		this.remotePlayer.setOpponent(this.localPlayer);
		
		//using basic ASCII encoding for this application
		encoder = Charset.forName("US-ASCII").newEncoder();
		decoder = Charset.forName("US-ASCII").newDecoder();
		
		//buffers to communicate on the socket 2kb should be more then we will ever use
		hostBytes = ByteBuffer.allocateDirect(2048);
		hostChar = CharBuffer.allocate(2048);
		
	}
	
	public void run() {
		//TODO: establish the socket connection to the remote host, port will be over port 8000
		try {
			host = SocketChannel.open(new InetSocketAddress(hostAddress, 8000));
			GUIMain.appendText("Connected to host game.\nWaiting for host to start game.\n");
			String[] request;
			main: for (;;) {
				request = getServerRequest().split(":");
				switch(request[0]) {
				case "updateBoard":
					//Add hostile ships to this board
					remotePlayer.addAllShips(request[1]);
					try {
						respondToRemoteHost(localPlayer.getShipsString());
					} catch (IOException e) {
						GUIMain.appendText("Remote host Invalid Placement\n");
					}
					break;
				case "hostMove":
					//Update a move from host
					try {
						localPlayer.hitMarker(Integer.parseInt(request[1].split(",")[0]), Integer.parseInt(request[1].split(",")[1]));
					} catch (Exception e) {
						GUIMain.appendText("Remote Host Made an Invalid Move\n");
					}
					respondToRemoteHost("updatedBoard");
					break;
				case "startMove":
					//Request the this player make a move
					localPlayer.run();
					break;
				case "placeShips":
					//starts the request for the player to place their ships
					localPlayer.start();
					try {
						localPlayer.join();
					} catch (Exception e) {
						//doing nothing with this as it shouldn't be interupted
					} 
					//lets the host know that the ships have been placed. 
					respondToRemoteHost(localPlayer.getShipsString());
					break;
				case "gameOver":
					break main;
				}
			}
						
		} catch (IOException e) {
			GUIMain.appendText("Disconnected from the host\n");
		}
	}
	
	
	private String getServerRequest() throws IOException {
		//gets the request from the server
		String serverRequest;
		host.read(hostBytes);
		hostBytes.flip();
		decoder.decode(hostBytes, hostChar, true);
		hostChar.flip();
		serverRequest = hostChar.toString();
		return serverRequest;
	}
	
	public void respondToRemoteHost(String response) throws IOException{
		host.write(encoder.encode(CharBuffer.wrap(response)));
	}
}
