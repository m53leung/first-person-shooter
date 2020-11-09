import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * Is used to read a text file
 * @author matth
 *
 */
public class FileReader {
	InputStream stream;
	File file;
	Scanner input;
	int rows, cols;
	char[][] text;

	/**
	 * Set the pathname height and width
	 * @param pathname String representation of the file
	 * @param height rows of text the file contains
	 * @param width columns of text the file contains
	 */
	public FileReader(String pathname, int height, int width) {
		rows = height;
		cols = width;
		
		//https://www.daniweb.com/programming/software-development/threads/266432/how-to-add-a-text-file-to-an-executable-jar-in-eclipse
		stream = FileReader.class.getResourceAsStream(pathname);
		if (stream == null)
			JOptionPane.showMessageDialog(null, "Resource not located.");
		input = null;
		try {
			input = new Scanner(stream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Scanner error");
		}
		
		text = new char[rows][cols];
		for (int r = 0; r < text.length; r++) {
			String line = input.nextLine();
			for (int c = 0; c < text[0].length; c++) {
				text[r][c] = line.charAt(c);
			}
		}
		input.close();
	}

	/**
	 * returns a char array from the text file
	 * @return char array representation of the text file
	 */
	public char[][] readFile() {
		return text;
	}
}