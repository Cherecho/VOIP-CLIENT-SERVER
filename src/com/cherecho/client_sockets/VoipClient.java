package com.cherecho.client_sockets;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.FloatControl;


public class VoipClient implements Runnable {
	
	private static final String HOST = System.getProperty("host", "localhost");
	private static final int PORT = Integer.parseInt(System.getProperty("port", "1049"));
	private Socket socket;
	private Microphone microphone;
	private Speaker speaker;
	
	
	public VoipClient() {
		this.microphone = new Microphone();
		this.speaker = new Speaker();
	}
	
	@Override
	public void run() {
		
		try {
			socket = new Socket(HOST, PORT);
			socket.setKeepAlive(true);
			System.out.println("conected!!");
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						socket.close();
						System.out.println("connection closed");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			System.err.println("Could not connect to " + HOST + "/" + PORT + ":" + e.getMessage());
			return;
		}

		// Reads data received from server
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (speaker.open()) {
					speaker.start();					
					while (socket.isConnected()) {
						try {
							byte[] buffer = new byte[speaker.getBufferSize() / 5];
							int read = socket.getInputStream().read(buffer, 0, buffer.length);
							speaker.write(buffer, 0, read);
						} catch (IOException e) {
							System.err.println("Could not read data from server:" + e.getMessage());
						}
					}
				}
			}
		}).start();

		// Sends data to the server
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (microphone.open()) {
					microphone.start();
					while (socket.isConnected()) {
						try {
							byte[] buffer = new byte[microphone.getBufferSize() / 5];
							int read = microphone.read(buffer, 0, buffer.length);
							socket.getOutputStream().write(buffer, 0, read);
						} catch (Exception e) {
							System.err.println("Could not send data to server:" + e.getMessage());
						}
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		VoipClient client = new VoipClient();
		new Thread(client).start();
	}
}