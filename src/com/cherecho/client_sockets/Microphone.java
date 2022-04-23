package com.cherecho.client_sockets;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Microphone {

	public static final float SAMPLE_RATE = 8000.0f;
	public static final int SAMPLE_SIZE = 16;
	private TargetDataLine line;
	private AudioFormat format;
	public Microphone() {
		format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);
	}

	public boolean open() {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		boolean lineOpen = false;

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			lineOpen = true;
		} catch (LineUnavailableException e) {
			lineOpen = false;
			System.err.println("There was an error open your microphone:" + e.getMessage());
		}
		return lineOpen;
	}

	public void start() {
		line.start();
	}

	public int read(byte[] buf, int off, int len) {
		return line.read(buf, off, len);
	}

	public void stop() {
		line.stop();
		line.flush();
	}

	public int getBufferSize() {
		return line.getBufferSize();
	}
}