import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.Toolkit;

//All written by Amir
public class Mainmenu {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Object[] options = { "Start(480p)", "Start(720p)", "How To Play", "Exit" };
		UIManager.put("OptionPane.maximumSize", new Dimension(2000, 1000));
		int option = JOptionPane.showOptionDialog(null, "", "GunnerZ", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		switch (option) {
		// put code here to run the game and the features
	
		
		case 0: //start the game. when play is pressed
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JFrame frame = new JFrame("GunnerZ (Amir and Matthew");
					// Set the content-pane of the JFrame to an instance of main JPanel
					frame.setContentPane(new GameMain(720, 480)); // main JPanel as content pane
					// frame.setJMenuBar(menuBar); // menu-bar (if defined)
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.pack();
					frame.setLocationRelativeTo(null); // center the application window
					frame.setVisible(true); // show it
				}
			});
			break;
		case 1:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JFrame frame = new JFrame("GunnerZ (Amir and Matthew)");
					// Set the content-pane of the JFrame to an instance of main JPanel
					frame.setContentPane(new GameMain(1080, 720)); // main JPanel as content pane
					// frame.setJMenuBar(menuBar); // menu-bar (if defined)
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.pack();
					frame.setLocationRelativeTo(null); // center the application window
					frame.setVisible(true); // show it
				}
			});
			break;
		case 2:
			instructions.main(null);
			break;
		
		case 3:
			System.exit(0);
			break;
		}
	 
	}

}
