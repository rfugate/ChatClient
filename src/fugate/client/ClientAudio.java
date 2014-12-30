package fugate.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import fugate.client.gui.ClientGUI;
import fugate.client.gui.window.ClientSettingsWindow;

public class ClientAudio {
	
	private static ByteArrayOutputStream out;
	
	public ClientAudio(){
		getFormat();
	}
	
	public static void captureAudio(){
		try {
			final AudioFormat format = getFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);	//Interface that represents an audio feed from which you can capture audio
			final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);	//Target Data Line is a type of DataLine in which audio data can be read
			
			line.open(format);	//Open the line
			line.start();		//Initialize the line
			
			Runnable runner = new Runnable(){
				
				int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
				byte[] buffer = new byte[bufferSize];
				
				public void run(){
					out = new ByteArrayOutputStream();
					ClientGUI.setCapturing(true);
					try{
						while(ClientGUI.isCapturing()){
							int count = line.read(buffer, 0, buffer.length);
							if(count > 0){
								out.write(buffer, 0, count);
								Byte[] voiceBuffer = new Byte[buffer.length];
								int i = 0;
								for(byte b : buffer){
									voiceBuffer[i++] = b;
								}
								new Thread(new Client.Sender(voiceBuffer)).start();
							}
						}
						line.close();
						out.close();
					} catch (IOException e){
						System.err.println("I/O Problem: " + e);
					}
				}
			};
			Thread captureThread = new Thread(runner);
			captureThread.start();
		} catch (LineUnavailableException e){
			System.err.println("Line Unavailable: " + e);
		}
	}
	
	public static void playAudio(Byte[] voice){
		try{
			int j = 0;
			byte audio[] = new byte[voice.length];
			for(Byte B : voice){
				audio[j++] = B.byteValue();
			}
			InputStream input = new ByteArrayInputStream(audio);
			final AudioFormat format = getFormat();
			final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			
			line.open(format);
			line.start();
			
			Runnable runner = new Runnable(){

				int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
				byte[] buffer = new byte[bufferSize];
				
				public void run() {
					int count;
					try {
						while((count = ais.read(buffer, 0, buffer.length)) != -1){
							if(count > 0){
								line.write(buffer, 0, count);
							}
						}
						line.drain();
						line.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			Thread playThread = new Thread(runner);
			playThread.start();
		} catch (LineUnavailableException e){
			System.err.println("Line Unavailable: " + e);
		}
	}
	
	private static AudioFormat getFormat(){
		/*
		 * Sample Rate: Number of Samples of audio carried per second. Measured in Hz or kHz
		 * 				8,000: Telephone, Wireless intercom, and wireless microphone. Adequate for human speech
		 * 				44,100: Audio CD
		 * 				96,000: DVD-Audio, BD-ROM Audio Tracks, HD DVD Audio Tracks
		 * 
		 * Sample Size: Number of bits of information in each sample.
		 * 				8: Phone
		 * 				16: Audio CD
		 * 				24: DVD-Audio, Blu-ray Disk
		 * 
		 * Channel: 	A way to separate a stream of audio information. 
		 * 				1: Mono:	One Channel 
		 * 				2: Stereo:	Two Channels (Left and Right) 
		 * 
		 * Signed:		Indicates whether the data is signed or unsigned
		 * 
		 * bigEndian:	Indicates whether the data for a single sample is stored big-endian byte order (False for little-Endian)
		 * 				Big-Endian
		 */
		float sampleRate = 8000; 
		int sampleSize = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSize, channels, signed, bigEndian);
	}
}
