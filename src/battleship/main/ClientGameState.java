package battleship.main;

import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.io.*;

import battleship.player.*;


public class ClientGameState extends Thread {
	String hostAddress;
	
	//Player 1 denotes the local player while player two is the remote
	Player player1;
	Player player2;
	
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
		
		this.player1 = player1;
		this.player2 = new RemoteHostHuman(this);
		
		this.player1.setOpponent(player2);
		this.player2.setOpponent(this.player1);
		
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
			
			main: for (;;) {
				switch(getServerRequest()) {
				case "hostMove":
					break;
				case "startMove":
					player1.run();
					break;
				case "placeShips":
					//starts the request for the player to place their ships
					player1.start();
					try {
						player1.join();
					} catch (Exception e) {
						//doing nothing with this as it shouldn't be interupted
					} 
					//lets the host know that the ships have been placed. 
					respondToRemoteHost("placed");
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
