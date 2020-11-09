
/**
 * Spawner is a x and y location from which zombies are spawned from
 * @author matth
 *
 */
public class Spawner {
	private int xLoc;
	private int yLoc;
	
	public Spawner (int x, int y) {
		xLoc = x;
		yLoc = y;
	}
	
	public int getXLoc() {
		return xLoc;
	}
	
	public int getYLoc() {
		return yLoc;
	}
	
}
