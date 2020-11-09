import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Sprite hold an image and has useful methods for rendering the image
 * @author matth
 *
 */
public class Sprite {
	URL url;
	Image img;

	
	public Sprite(String fileName) {
		url = GameMain.class.getResource(fileName);
		img = Toolkit.getDefaultToolkit().getImage(url);

	}
	
	/**
	 * gets the drawable for the image
	 * @param x x location of the drawable
	 * @param y y location of the drawable
	 * @param width width of the drawable
	 * @param distance distance of the drawable
	 * @return Drawable of the image
	 */
	public Drawable getDrawable(int x, int y, int width, double distance) {
		int height = (int)((double)width * (double)img.getHeight(null) / (double)img.getWidth(null) + 0.5);
		return new Drawable(this, x - width / 2, y - height / 2, width, height, distance);
	}
	
	/**
	 * returns the drawable column from a sample
	 * @param sampleCol the location of the sample column to be taken from(0 is very left, 1 is very right)
	 * @param x x location of the drawable	
	 * @param y y location of the drawable
	 * @param height height of the drawable
	 * @param distance distance of the drawable
	 * @return Drawable of the image
	 */
	public Drawable getDrawableCol(double sampleCol, int x, int y, int height, double distance) {
		int col = (int)((sampleCol % 1) * img.getWidth(null));
		return new Drawable(this, x, y - height / 2, x + 1, y + height / 2, col, 0, col + 1, img.getHeight(null), distance);
	}
	
	/**
	 * Draws the column taken from a sample
	 * @param g2d Graphics2D component
	 * @param sampleCol sample column to be from
	 * @param x x location to be drawn at
	 * @param y y location to be drawn at
	 * @param height
	 */
	public void drawCol(Graphics2D g2d, double sampleCol, int x, int y, int height) {
		int col = (int)((sampleCol % 1) * img.getWidth(null));
		g2d.drawImage(img, x, y - height / 2, x + 1, y + height / 2, col, 0, col + 1, img.getHeight(null), null);
	}
	
	/**
	 * draws the image at x and y
	 * @param g2d Graphics2D component
	 * @param x x location to be drawn at
	 * @param y y location to be drawn at
	 */
	public void draw(Graphics2D g2d, int x, int y) {
		g2d.drawImage(img, x, y, x + img.getWidth(null), y + img.getHeight(null), 0, 0, img.getWidth(null), img.getHeight(null), null);
	}
	
	/**
	 * returns the associated image
	 * @return
	 */
	public Image getImage() {
		return img;
	}
}
