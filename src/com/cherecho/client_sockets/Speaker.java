package com.cherecho.client_sockets;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
 
public class Speaker {

    public static final float SAMPLE_RATE = 8000.0f;
    public static final int SAMPLE_SIZE = 16;
    private SourceDataLine line;
    private AudioFormat format;

    public Speaker() {
        format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);
    }
    
    
    public boolean open() {
    	DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        boolean lineOpen = false;
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);            
            lineOpen =  true;
        } catch(LineUnavailableException e) {
        	lineOpen =  false;
        	System.err.println("There was an error loading your speakers:" + e.getMessage());
        }
        return lineOpen;
    }
    
    public SourceDataLine getLine() {
		return (SourceDataLine) line.getControl(FloatControl.Type.MASTER_GAIN);
	}

    public void start() {
        line.start();
    }
 
    public void write(byte[] buf, int off, int len) {
        line.write(buf, off, len);
    }
 
    public void stop() {
        line.stop();
        line.flush();
    }
 
    public int getBufferSize() {
        return line.getBufferSize();
    }
}
