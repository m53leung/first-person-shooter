
/**
 * Path holds the next corner in a path and the total distance of the path
 * @author matth
 *
 */
public class Path {
	int nextCorner;
	double distance;
	
	public Path(int i, double d) {
		nextCorner = i;
		distance = d;
	}

	public int getNext() {
		return nextCorner;
	}
	
	public double getDistance() {
		return distance;
	}
}
