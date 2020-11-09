
/**
 * a vertex used in Dijkstra's algorithm
 * @author matth
 *
 */
public class Vertex extends Corner {

	int previous;
	double shortestDistance;
	boolean visited = false;
	
	/**
	 * each vertex has an x and y and a distance from start
	 * @param x x
	 * @param y y
	 * @param distanceFromStart distance
	 */
	public Vertex(double x, double y, double distanceFromStart) {
		super(x, y);
		shortestDistance = distanceFromStart;
	}
	
	public double getShortestDistance() {
		return shortestDistance;
	}
	
	public void setShortestDistance(double d) {
		shortestDistance = d;
	}
	
	public int getPrevious() {
		return previous;
	}
	
	/**
	 * sets the previous vertex, from which the shortest distance was gotten from traveling from
	 * @param v previous vertex
	 */
	public void setPrevious(int v) {
		previous = v;
	}
	
	public void setVisited() {
		visited = true;
	}

	public boolean visited() {
		return visited;
	}
	
}
