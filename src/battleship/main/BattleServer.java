package battleship.main;

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
	
	private String clientString;
	
	BattleServer() throws IOException {
		encoder = Charset.forName("US-ASCII").newEncoder();
		decoder = Charset.forName("US-ASCII").newDecoder();
		
		clientBytes = ByteBuffer.allocateDirect(2048);
		clientChar = CharBuffer.allocate(2048);
		
		server = ServerSocketChannel.open();
		server.socket().bind(new java.net.InetSocketAddress(8000));
		clientString = "";
	}
	
	public boolean opponentConnected() {
		return client.isConnected();
	}
	
	//this is used to to request a move from the remote client. 
	public Integer[] requestRemotePlayerMove() throws IOException {
		Integer[] xyReturnValues = new Integer[2];
		if (client.isConnected()) {
			client.write(encoder.encode((CharBuffer.wrap("startMove"))));
			
			client.read(clientBytes);
			clientBytes.flip();
			decoder.decode(clientBytes, clientChar, true);
			clientChar.flip();
			clientString = clientChar.toString();
			
			xyReturnValues[0] = Integer.parseInt(clientString.split(",")[0]);
			xyReturnValues[1] = Integer.parseInt(clientString.split(",")[1]);
			
			return xyReturnValues;
		} else {
			throw new AssertionError("A client must be connected");
		}
	}
	
	public void requestRemotePlayerShipPlacement() throws IOException {
		if (client.isConnected()) {
			client.write(encoder.encode((CharBuffer.wrap("startMove"))));
			
			client.read(clientBytes);
			clientBytes.flip();
			decoder.decode(clientBytes, clientChar, true);
			clientChar.flip();
			clientString = clientChar.toString();
			
			if (!clientString.equals("placed")) {
				throw new Error("Remote Ship Placement Failed");
			}
		} else {
			throw new AssertionError("A client must be connected");
		}
	}
	
	public void run() {
		try {
			for(;;) {
				System.out.println("Server waiting for connection");
				
				//a client has connected to the server
				client = server.accept();
				System.out.println("Client Connected");
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

	private void textInputValidation() throws IOException {
		try {
		main: for (;;) {
				//get the input from the client
				if(client.read(clientBytes) != -1) {
					clientBytes.flip();
					decoder.decode(clientBytes, clientChar, true);
					clientChar.flip();
					clientString = clientChar.toString();
					clientBytes.clear();
					clientChar.clear();
					
					//do the thing the client has asked us to
					switch (clientString) {
					case "getDate": //request to get the date-time from the server
						String response = new java.util.Date().toString() + "\r\n";
						client.write(encoder.encode(CharBuffer.wrap(response)));
						break;
					case "exit": //manual disconnection request from the client
						client.close();
						System.out.println("Client disconnected");
						break main;
					}
				//if the client closes the socket
				} else {
					client.close();
					break main;
				}
			}
		//if the client dies or otherwise becomes disconnected
		} catch (IOException e) {
			client.close();
			System.out.println("Client disconnected");
		}
	}
}
