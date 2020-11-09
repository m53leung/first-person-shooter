/**
 * Stores an x and y coordinate. A corner is essentially just a coordinate
 * @author matth
 *
 */
public class Corner {
	
	private double posX, posY;
	
	public Corner(double x, double y) {
		posX = x;
		posY = y;
	}
	
	public double getX() {
		return posX;
	}
	
	public double getY() {
		return posY;
	}
	
}
