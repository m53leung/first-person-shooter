import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	// not my code

	static final long ACTIVE_TIME = 10000;
	
	URL url;
	
	long timer = 0;
	
	Clip clip;
	AudioInputStream ais;
	
	public SoundPlayer (URL url) {
		this.url = url;
		try {
			playSoundEffects(url);
		} catch (MalformedURLException ex) {
			Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		} catch (LineUnavailableException ex) {
			Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedAudioFileException ex) {
			Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public boolean addToTimer(long time) {
		timer += time;
		if (timer >= ACTIVE_TIME) {
			clip.close();
			try {
				ais.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Plays a sound file
	 * 
	 * @author Julia and Liina
	 * @param fileName
	 */

	// not my code
	/**
	 * Plays the sound effect
	 * 
	 * @author Julia and Liina
	 * @param fileName
	 * @throws MalformedURLException
	 * @throws LineUnavailableException
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public void playSoundEffects(URL url)
			throws MalformedURLException, LineUnavailableException, UnsupportedAudioFileException, IOException { // Written
																													// by
																													// Julia
																													// and
																													// Liina
																													// from
																													// "Brick
																													// Breaker"
																													// project
		// File url = new File(fileName);
		clip = AudioSystem.getClip();

		ais = AudioSystem.getAudioInputStream(url);
		clip.open(ais);
		clip.start();
	}
}
