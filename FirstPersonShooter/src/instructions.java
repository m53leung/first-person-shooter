import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

//Written by Amir
public class instructions {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		UIManager.put("OptionPane.minimumSize", new Dimension(150, 150));
		JOptionPane.showMessageDialog(null, "Use WASD to move around in them map. Use SPACE to shoot your gun. Use LEFT KEY and RIGHT KEY to look around Press R KEY to reload your gun if zombies get too close, you will lose your health.");
			Mainmenu.main(null);
	}

}
