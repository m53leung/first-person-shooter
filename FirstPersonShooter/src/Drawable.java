import java.awt.Graphics2D;
import java.awt.Image;

/**
 * A Drawable holds a image 2 source and destination coordinates each, when Drawable.draw() is called it will draw the image from source 1 to source 2 at destination 1 to destination 2.
 * It is comparable so it will be ordered from greatest distance to lowest distance.
 * @author matth
 *
 */
public class Drawable implements Comparable<Drawable> {
	//Image img;
	Sprite sprite;
	int dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2;
	double distance;
	
	/**
	 * Makes a drawable of part of an image, with a specified destination coordinates
	 * @param img image
	 * @param dx1 destination coordinate x
	 * @param dy1 destination coordinate y
	 * @param dx2 desintation coordinate 2 x
	 * @param dy2 destination coordinate 2 y
	 * @param sx1 source coordinate 1 x
	 * @param sy1 source coordinate 1 y
	 * @param sx2 source coordinate 2 x
	 * @param sy2 source coordinate 2 y
	 * @param distance distance of drawable
	 */
	public Drawable(Sprite sprite, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, double distance) {
		//this.img = img;
		this.sprite = sprite;
		this.dx1 = dx1;
		this.dy1 = dy1;
		this.dx2 = dx2;
		this.dy2 = dy2;
		this.sx1 = sx1;
		this.sy1 = sy1;
		this.sx2 = sx2;
		this.sy2 = sy2;
		this.distance = distance;
	}
	
	/**
	 * Make a drawable of an entire image, and only specify the destination coordinates
	 * @param image image
	 * @param dx1 destination coordinate x
	 * @param dy1 destination coordinate y
	 * @param width width of drawn image
	 * @param height height of drawn image
	 * @param distance distance of drawable
	 */
	public Drawable(Sprite sprite, int dx1, int dy1, int width, int height, double distance) {
		// TODO Auto-generated constructor stub
		//img = image;
		this.sprite = sprite;
		this.dx1 = dx1;
		this.dy1 = dy1;
		dx2 = dx1 + width;
		dy2 = dy1 + height;
		sx1 = 0;
		sy1 = 0;
		sx2 = sprite.getImage().getWidth(null);
		sy2 = sprite.getImage().getHeight(null);
		this.distance = distance;
	}

	/**
	 * Draws the drawable
	 * @param g2d Graphics2D component
	 */
	public void draw(Graphics2D g2d) {
		g2d.drawImage(sprite.getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}
	@Override
	public int compareTo(Drawable o) {
		// TODO Auto-generated method stub
		if (distance < o.distance) {
			return 1;
		} else if (distance > o.distance) {
			return -1;
		} else {
			return 0;
		}
	}
	
}
