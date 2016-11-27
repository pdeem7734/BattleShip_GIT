package battleship.main.multiplayer;

import battleship.main.GUIMain;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.io.*;

public class BattleServer {
	//create the encoder and decoder
	private CharsetEncoder encoder;
	private CharsetDecoder decoder;
	
	//input buffers for the serversocket channel
	private ByteBuffer clientBytes;
	private CharBuffer clientChar;
	
	//create the server socket channel
	private ServerSocketChannel server;
	private SocketChannel client;
	
	private String clientString = "";
	
	BattleServer() {
		//using basic ASCII encoding for this application
		encoder = Charset.forName("US-ASCII").newEncoder();
		decoder = Charset.forName("US-ASCII").newDecoder();
		
		//buffers to communicate on the socket 2kb should be more then we will ever use
		clientBytes = ByteBuffer.allocateDirect(2048);
		clientChar = CharBuffer.allocate(2048);
	}
	
	public boolean opponentConnected() {
		return client.isConnected();
	}
	
	//String response for a remote request from the client 
	public String requestFromRemote(String request) throws IOException {

		if (client.isConnected()) {
			client.write(encoder.encode((CharBuffer.wrap(request))));
			
			client.read(clientBytes);
			clientBytes.flip();
			decoder.decode(clientBytes, clientChar, true);
			clientChar.flip();
			clientString = clientChar.toString();
			
			return clientString;
		} else {
			throw new AssertionError("A client must be connected");
		}
	}
		
	//this is the main server thread that waits for the client to connect
	//it also checks every second to ensure the remote player is still connected on the socket
	public void run() {
		try {
			//starts the server connection on port 8000
			server = ServerSocketChannel.open();
			server.socket().bind(new java.net.InetSocketAddress(8000));
			
			//waits for a client to connect to the server
			for(;;) {
				System.out.println("Server waiting for connection");
				
				//a client has connected to the server
				client = server.accept();
				System.out.println("Client Connected");
				
				//monitors to ensure client remains connected 
				while (client.isConnected()) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						GUIMain.appendText("BattleServer has crashed\n");
						System.out.println("BattleServer has crashed");
					}
				}
			}
		} catch (IOException e) {
			
		}
	}
}
