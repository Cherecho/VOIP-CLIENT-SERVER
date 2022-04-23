package com.cherecho.server_sockets;

import java.net.*;
import java.io.*;
import java.util.*;

public class server_runable {

	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("shutting down the server...");
			}
		});
		try (ServerSocket serverSocket = new ServerSocket(1049)) {
			while (true) {
				Thread serverThread = new Thread(new ServerThread(serverSocket.accept()));
				serverThread.start();
			}
		}
	}
}

class ServerThread implements Runnable {

	public static Collection<Socket> sockets = new ArrayList<Socket>();
	Socket connection = null;
	int poss;
	DataInputStream dataIn = null;
	DataOutputStream dataOut = null;
	int bytesRead = 0;
	Socket[] connections = new Socket[2];
	public static List<Socket[]> channels = new ArrayList<Socket[]>();
	int channel;

	public ServerThread(Socket conn) throws Exception {

		this.connection = conn;
		this.dataIn = new DataInputStream(connection.getInputStream());
		dataOut = new DataOutputStream(connection.getOutputStream());
		sockets.add(connection);

		if (channels.isEmpty()) {
			connections = new Socket[2];
			channels.add(connections);
			channels.get(0)[0] = connection;
			System.out.println("First channel created and added to slot 1");

			channel = 0;
		} else {
			outerloop: for (int x = 0; x < channels.size(); x++) {
				for (int y = 0; y < 2; y++) {
					if (channels.get(x)[y] == null) {
						channels.get(x)[y] = connection;
						System.out.println("Connection added to channel: " + (x + 1) + " being usser : " + (y + 1));
						channel = x;
						break outerloop;
					}
				}
				if (channels.get(x)[0] != null && channels.get(x)[1] != null) {
					System.out.println("Creating a new channel!");
					channels.add(connections);
				}
			}
		}

	}

	public void run() {
		bytesRead = 0;
		byte[] inBytes = new byte[2];// 1
		if (channels.get(this.channel)[0] == this.connection) {
			this.poss = 0;
		} else {
			this.poss = 1;
		}

		while (this.bytesRead != -1) {
			try {
				this.bytesRead = this.dataIn.read(inBytes, 0, inBytes.length);
			} catch (IOException e) {
			}
			if (this.bytesRead >= 0) {
				sendToChannel(inBytes, bytesRead, this.connection, this.channel);
			}
			try {
				if(connection.getInputStream() == null) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("User on the channel " + (this.channel + 1) + " and pos " + (this.poss + 1) + " has left.");
		channels.get(this.channel)[this.poss] = null;
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void sendToChannel(byte[] byteArray, int q, Socket connectionn, int ch) {
		
		Socket temp = channels.get(ch)[0];
		if (connectionn != channels.get(ch)[0]) {
			DataOutputStream tempOut = null;
			if(temp!=null) {
				try {
					tempOut = new DataOutputStream(temp.getOutputStream());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					tempOut.write(byteArray, 0, q);
				} catch (IOException e) {
				}
			}
			
		} else {
			if (channels.get(ch)[1] != null) {
				temp = channels.get(ch)[1];
				DataOutputStream tempOut = null;
				try {
					tempOut = new DataOutputStream(temp.getOutputStream());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					tempOut.write(byteArray, 0, q);
				} catch (IOException e) {
				}
			}
		}
	}
}
