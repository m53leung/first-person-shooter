import java.awt.Graphics2D;

/**
 * NPC is a Sprite that has an x and y coordinate, and can be rendered in "3d" space
 * @author matth
 *
 */
public class NPC {
	Sprite sprite;
	double radius;
	double x, y;
	boolean rendered;
	
	public NPC(Sprite sprite, double width, double x, double y) {
		this.sprite = sprite;
		this.radius = width;
		this.x = x;
		this.y = y;
		rendered = false;
	}
	
	/**
	 * returns if the npc was rendered this frame
	 * @return boolean if it was rendered
	 */
	public boolean isRendered() {
		return rendered;
	}
	
	/**
	 * sets the npc to rendered status
	 * @param b boolean rendered is to be set to
	 */
	public void setRendered(boolean b) {
		rendered = b;
	}
	
	/**
	 * gets width / 2 of the npc
	 * @return width / 2
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * sets the x coordinate of the npc
	 * @param x x coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * sets the y coordinate of the npc
	 * @param y y coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * returns the x coordinate of the NPC
	 * @return x coordinate of npc
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * returns y coordinate of the npc
	 * @return y coordinate
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Gets associated sprite
	 * @return Sprite
	 */
	public Sprite getSprite() {
		return sprite;
	}
	
	/**
	 * sets the sprite of the npc
	 * @param s Sprite
	 */
	public void setSprite(Sprite s) {
		sprite = s;
	}
	
	/**
	 * draw image of NPC at x, y with specified width
	 * @param g2d Graphics2D component
	 * @param x
	 * @param y
	 * @param width
	 */
	public void draw(Graphics2D g2d, int x, int y, int width) {
		int height = width * sprite.getImage().getHeight(null) / sprite.getImage().getWidth(null);
		g2d.drawImage(sprite.getImage(), x - width / 2, y - height / 2, width, height, null);
	}
	
}
