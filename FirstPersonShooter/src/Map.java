import java.util.ArrayList;

/**
 * A map for the game
 * @author matth
 *
 */
public class Map {
	
	//Character legend
	static final char SPAWNER = '*';
	static final char WALL = '|';
	static final char EMPTY = ' ';
	static final char RIFLE_BUY = 'r';
	static final char PISTOL_BUY = 'p';
	static final char REVOLVER_BUY = 'R';
	static final char JUGGERNOG = 'J';
	static final char STAMIN_UP = 'S';
	static final char DOUBLE_TAP = 'D';
	static final char SPEED_COLA  = 's';
	
	//Radius used when calculating line of sights between corners of the map
	static final double STANDARD_RADIUS = 0.4;
	
	int width, height; //width and height of map
	char[][] map; //character array representation of the map
	ArrayList<Corner> corners; //All corners of the map
	double[][] cornerNetwork; //network matrix of corners, for paths with no los value is -1
	Path[][] cornerPaths; //network of next corner to travel to for the fastest path

	ArrayList<Perk> perks;
	
	//Wall buys
	Wallbuy rifleBuy;
	Wallbuy pistolBuy;
	Wallbuy revolverBuy;

	public Map(String textFile, int rows, int cols, Wallbuy rifleBuy, Wallbuy pistolBuy, Wallbuy revolverBuy) {
		FileReader mapReader = new FileReader(textFile, rows, cols);
		map = mapReader.readFile();

		width = cols;
		height = rows;

		this.rifleBuy = rifleBuy;
		this.pistolBuy = pistolBuy;
		this.revolverBuy = revolverBuy;
		
		//Add any perks found on the map to perks
		perks = new ArrayList<Perk>();
		
		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y ++) {
				if (map[y][x] == JUGGERNOG) {
					perks.add(new Juggernog(x, y));
				} else if (map[y][x] == STAMIN_UP) {
					perks.add(new StaminUp(x, y));
				} else if (map[y][x] == DOUBLE_TAP) {
					perks.add(new DoubleTap(x, y));
				} else if (map[y][x] == SPEED_COLA) {
					perks.add(new SpeedCola(x, y));
				}
			}
		}
			
		corners = new ArrayList<Corner>();

		// Find all corners in the map and add it to the arraylist
		for (int r = 0; r < map.length; r++) {
			for (int c = 0; c < map[0].length; c++) {
				if (isWall(map[r][c]) == false) {
					if (r - 1 >= 0 && c + 1 < map[0].length && isWall(map[r - 1][c + 1])
							&& isWall(map[r - 1][c]) == false && isWall(map[r][c + 1]) == false) {
						corners.add(new Corner(c + 0.5, r + 0.5));
					} else if (r + 1 < map.length && c + 1 < map[0].length && isWall(map[r + 1][c + 1])
							&& isWall(map[r][c + 1]) == false && isWall(map[r + 1][c]) == false) {
						corners.add(new Corner(c + 0.5, r + 0.5));
					} else if (r + 1 < map.length && c - 1 >= 0 && isWall(map[r + 1][c - 1])
							&& isWall(map[r + 1][c]) == false && isWall(map[r][c - 1]) == false) {
						corners.add(new Corner(c + 0.5, r + 0.5));
					} else if (r - 1 >= 0 && c - 1 >= 0 && isWall(map[r - 1][c - 1]) && isWall(map[r][c - 1]) == false
							&& isWall(map[r - 1][c]) == false) {
						corners.add(new Corner(c + 0.5, r + 0.5));
					}
				}
			}
		}

		cornerNetwork = new double[corners.size()][corners.size()];
		cornerPaths = new Path[corners.size()][corners.size()];

		// For each corner, see what corners it has line of sight to and store the data
		// in the network
		for (int i = 0; i < corners.size(); i++) {
			for (int c = 0; c < corners.size(); c++) {

				// if corners are the same then there is no path
				if (i == c) {
					cornerNetwork[i][c] = -1.0;
				} else if (lineOfSight(corners.get(i).getX(), corners.get(i).getY(), corners.get(c).getX(),
						corners.get(c).getY(), STANDARD_RADIUS)) {
					cornerNetwork[i][c] = Math.sqrt(Math.pow(corners.get(c).getX() - corners.get(i).getX(), 2)
							+ Math.pow(corners.get(c).getY() - corners.get(i).getY(), 2));
				} else {
					cornerNetwork[i][c] = -1.0;
				}


			}

		}

		// For each corner run dijkstra's algorithm for every other corner to find what
		// corner to travel to directly to start the path to the target corner
		for (int s = 0; s < corners.size(); s++) {
			for (int e = 0; e < corners.size(); e++) {
				if (s == e) {
					cornerPaths[s][e] = new Path(e, 0);
				} else {
					cornerPaths[s][e] = dijkstra(s, e);
				}
			}
		}


	}

	/**
	 * Returns the Corner you should travel to for the fastest path x2, y2
	 * @param x1 start coordinate x
	 * @param y1 start coordinate y
	 * @param x2 end coordinate x
	 * @param y2 end coordinate y
	 * @return Target corner
	 */
	public Corner findPath(double x1, double y1, double x2, double y2) {
		ArrayList<Integer> startCorners = new ArrayList<Integer>(corners.size());
		ArrayList<Integer> endCorners = new ArrayList<Integer>(corners.size());

		// generate the list of corners in LOS of start point and end point
		for (int i = 0; i < corners.size(); i++) {
			if (lineOfSight(x1, y1, corners.get(i).getX(), corners.get(i).getY())) {
				startCorners.add(i);
			}
			if (lineOfSight(x2, y2, corners.get(i).getX(), corners.get(i).getY())) {
				endCorners.add(i);
			}
		}

		// find the shortest combination of start and end corner
		int fastestStart = 0;
		int fastestEnd = 0;
		for (int s = 0; s < startCorners.size(); s++) {
			for (int e = 0; e < endCorners.size(); e++) {
				if (cornerPaths[startCorners.get(s)][endCorners.get(e)].getDistance()
						+ GameMain.getDistance(x1, y1, corners.get(startCorners.get(s)).getX(),
								corners.get(startCorners.get(s)).getY())
						+ GameMain.getDistance(x2, y2, corners.get(endCorners.get(e)).getX(),
								corners.get(endCorners.get(e))
										.getY()) < cornerPaths[startCorners.get(fastestStart)][endCorners
												.get(fastestEnd)].getDistance()
												+ GameMain.getDistance(x1, y1, corners.get(startCorners.get(s)).getX(),
														corners.get(startCorners.get(s)).getY())
												+ GameMain.getDistance(x2, y2, corners.get(endCorners.get(e)).getX(),
														corners.get(endCorners.get(e)).getY())) {
					fastestStart = s;
					fastestEnd = e;
				}
			}
		}

		return corners.get(startCorners.get(fastestStart));
	}

	/**
	 * Returns the corner to travel to for the fastest path to x2, y2, with a specified width
	 * @param x1 start coordinate x
	 * @param y1 start coordinate y
	 * @param x2 end coordinate x
	 * @param y2 end coordinate y
	 * @param radius width / 2
	 * @return Target corner
	 */
	public Corner findPath(double x1, double y1, double x2, double y2, double radius) {
		ArrayList<Integer> startCorners = new ArrayList<Integer>(corners.size());
		ArrayList<Integer> endCorners = new ArrayList<Integer>(corners.size());

		// generate the list of corners in LOS of start point and end point
		for (int i = 0; i < corners.size(); i++) {
			if (lineOfSight(x1, y1, corners.get(i).getX(), corners.get(i).getY(), radius)) {
				startCorners.add(i);
			}
			if (lineOfSight(x2, y2, corners.get(i).getX(), corners.get(i).getY(), radius)) {
				endCorners.add(i);
			}
		}

		// find the shortest combination of start and end corner
		int fastestStart = 0;
		int fastestEnd = 0;
		for (int s = 0; s < startCorners.size(); s++) {
			for (int e = 0; e < endCorners.size(); e++) {
				if (cornerPaths[startCorners.get(s)][endCorners.get(e)].getDistance()
						+ GameMain.getDistance(x1, y1, corners.get(startCorners.get(s)).getX(),
								corners.get(startCorners.get(s)).getY())
						+ GameMain.getDistance(x2, y2, corners.get(endCorners.get(e)).getX(),
								corners.get(endCorners.get(e))
										.getY()) < cornerPaths[startCorners.get(fastestStart)][endCorners
												.get(fastestEnd)].getDistance()
												+ GameMain.getDistance(x1, y1,
														corners.get(startCorners.get(fastestStart)).getX(),
														corners.get(startCorners.get(fastestStart)).getY())
												+ GameMain.getDistance(x2, y2,
														corners.get(endCorners.get(fastestEnd)).getX(),
														corners.get(endCorners.get(fastestEnd)).getY())) {
					fastestStart = s;
					fastestEnd = e;
				}
			}
		}

		return corners.get(startCorners.get(fastestStart));
	}

	/**
	 * returns if two points have line of sight
	 * @param x1 point one x
	 * @param y1 point one y
	 * @param x2 point two x
	 * @param y2 point two y
	 * @return boolean if they have line of sight
	 */
	public boolean lineOfSight(double x1, double y1, double x2, double y2) {

		// System.out.println("(" + x1 + ", " + y1 + ")");

		double smallNumber = 0.0000000001;

		if (isWall((int) x1, (int) y1) || isWall((int) x2, (int) y2)) { // If start tile or end tile is a wall then
																		// there is no line of sight
			return false;
		} else if ((int) x1 == (int) x2 && (int) y1 == (int) y2) { // If start tile or end tile are the same tile then
																	// there is a line of sight
			return true;
		} else if ((int) x2 - (int) x1 == 0) { // Need to have a case if x2 - x1 == 0 or else an error will occur when
												// trying to calculate slope in the next case
			if (y2 - y1 > 0) {
				return lineOfSight(x1, (int) y1 + 1, x2, y2);
			} else {
				return lineOfSight(x1, (int) y1 - 1, x2, y2);
			}
		} else {
			double slope = (y2 - y1) / (x2 - x1);
			if (x2 - x1 > 0) {
				if (slope >= 0) {
					// line is going to the right and down
					// check if line intersects tile below or tile to the right
					// Calculate y when x is at the border of the tile to the right
					double yNext = y1 + slope * ((int) x1 + 1 - x1);
					if ((int) y1 == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the right
						return lineOfSight((int) x1 + 1, yNext, x2, y2);
					} else { // else line must intersect with tile below
						double xNext = x1 + ((int) y1 + 1 - y1) / slope;
						return lineOfSight(xNext, (int) y1 + 1, x2, y2);
					}
				} else {
					// line is going to the right and up
					// check if line intersects tile to the right or above
					// calculate y when x is at the border of the tile to the right
					double yNext = y1 + slope * ((int) x1 + 1 - x1);
					if ((int) y1 == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the right
						return lineOfSight((int) x1 + 1, yNext, x2, y2);
					} else { // else line must intersect with tile above
						double xNext = x1 + ((int) y1 - smallNumber - y1) / slope;
						return lineOfSight(xNext, (int) y1 - smallNumber, x2, y2);
					}
				}
			} else {
				if (slope >= 0) {
					// line is going to the left and up
					// check if line intersects tile above or tile to the left
					// Calculate y when x is at the border of the tile to the left
					double yNext = y1 + slope * ((int) x1 - smallNumber - x1);
					if ((int) y1 == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the left
						return lineOfSight((int) x1 - smallNumber, yNext, x2, y2);
					} else { // else line must intersect with tile above
						double xNext = x1 + ((int) y1 - smallNumber - y1) / slope;
						return lineOfSight(xNext, (int) y1 - smallNumber, x2, y2);
					}
				} else {
					// line is going to the left and down
					// check if line intersects tile to the left or below
					// calculate y when x is at the border of the tile to the left
					double yNext = y1 + slope * ((int) x1 - smallNumber - x1);
					if ((int) y1 == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the left
						return lineOfSight((int) x1 - smallNumber, yNext, x2, y2);
					} else { // else line must intersect with tile below
						double xNext = x1 + ((int) y1 + 1 - y1) / slope;
						return lineOfSight(xNext, (int) y1 + 1, x2, y2);
					}
				}
			}
		}

	}

	/**
	 * Returns if object one with a certain width can travel in a straight line two point two without hitting any walls
	 * @param x1 start coordinate x
	 * @param y1 start coordinate y
	 * @param x2 end coordinate x
	 * @param y2 end coordinate y
	 * @param radius width / 2 of object
	 * @return boolean if there is a straight path
	 */
	public boolean lineOfSight(double x1, double y1, double x2, double y2, double radius) {

		//if the two corresponding corners have line of sight the object has line of sight
		/*
		 * -------.
		 * |     | .
		 * |     |  .
		 * -------   .
		 * .          .
		 *  .          .
		 *   .   -------
		 *    .  |     | 
		 *     . |     |  
		 *      .------- 

		 * 
		 */
		
		Corner cOne;
		Corner cTwo;
		Corner cThree;
		Corner cFour;
		if (x2 - x1 < 0) {
			if (y2 - y1 < 0) {
				cOne = new Corner(x1 - radius, y1 + radius);
				cTwo = new Corner(x1 + radius, y1 - radius);
				cThree = new Corner(x2 - radius, y2 + radius);
				cFour = new Corner(x2 + radius, y2 - radius);
			} else {
				cOne = new Corner(x1 - radius, y1 - radius);
				cTwo = new Corner(x1 + radius, y1 + radius);
				cThree = new Corner(x2 - radius, y2 - radius);
				cFour = new Corner(x2 + radius, y2 + radius);
			}
		} else {
			if (y2 - y1 < 0) {
				cOne = new Corner(x1 - radius, y1 - radius);
				cTwo = new Corner(x1 + radius, y1 + radius);
				cThree = new Corner(x2 - radius, y2 - radius);
				cFour = new Corner(x2 + radius, y2 + radius);
			} else {
				cOne = new Corner(x1 - radius, y1 + radius);
				cTwo = new Corner(x1 + radius, y1 - radius);
				cThree = new Corner(x2 - radius, y2 + radius);
				cFour = new Corner(x2 + radius, y2 - radius);
			}
		}

		return lineOfSight(cOne.getX(), cOne.getY(), cThree.getX(), cThree.getY())
				&& lineOfSight(cTwo.getX(), cTwo.getY(), cFour.getX(), cFour.getY());

	}

	// https://www.youtube.com/watch?v=pVfj6mxhdMw
	/**
	 * returns the shortest path from vertex s to vertex e
	 * @param s index of start vertex
	 * @param e index of end vertex
	 * @return shortest path
	 */
	public Path dijkstra(int s, int e) {
		Vertex[] vertexList = new Vertex[corners.size()];
		// ArrayList<Vertex> unvisited = new ArrayList<Vertex>(corners.size());
		// ArrayList<Vertex> visited = new ArrayList<Vertex>(corners.size());

		// generate list of vertices, distance from s to s is 0, any other is infinity
		for (int i = 0; i < corners.size(); i++) {
			if (i == s) {
				vertexList[i] = new Vertex(corners.get(i).getX(), corners.get(i).getY(), 0);
			} else {
				vertexList[i] = new Vertex(corners.get(i).getX(), corners.get(i).getY(), Double.POSITIVE_INFINITY);
			}
		}

		for (int c = 1; c <= vertexList.length; c++) {
			// Visit the unvisited vertex with the smallest distance from start
			int currentIndex = -1;
			for (int i = 0; i < vertexList.length; i++) {
				if (vertexList[i].visited() == false) {
					currentIndex = i;
				}
			}
			
			for (int i = 0; i < vertexList.length; i++) {
				if (vertexList[i].visited() == false
						&& vertexList[i].getShortestDistance() < vertexList[currentIndex].getShortestDistance()) {
					currentIndex = i;
				}
			}

			//visit unvisited neighbors and update shortest distance if new distance is shorter
			for (int i = 0; i < vertexList.length; i++) {
				if (vertexList[i].visited() == false && cornerNetwork[currentIndex][i] >= 0) {
					double newDistance = vertexList[currentIndex].getShortestDistance()
							+ cornerNetwork[currentIndex][i];
					if (newDistance < vertexList[i].getShortestDistance()) {
						vertexList[i].setShortestDistance(newDistance);
						vertexList[i].setPrevious(currentIndex);
					}
				}
			}

			// add the current vertex to the list of visited vertex
			vertexList[currentIndex].setVisited();
		}

		// Start at the end vertex and find the first vertex after the starting vertex
		boolean found = false;
		int current = e;
		while (!found) {
			if (vertexList[current].getPrevious() == s) {
				break;
			} else {
				current = vertexList[current].getPrevious();
			}
		}

		return new Path(current, vertexList[e].getShortestDistance());

	}

	/**
	 * returns the list of perks
	 * @return perks arraylist
	 */
	public ArrayList<Perk> getPerks() {
		return perks;
	}
	
	/**
	 * returns true if character is associated with a wall buy
	 * @param c character
	 * @return if character represents a wall buy
	 */
	public boolean isWallbuy(char c) {
		return c == RIFLE_BUY || c == PISTOL_BUY || c == REVOLVER_BUY;
	}

	/**
	 * checks if character represents a wall
	 * @param c character
	 * @return if character represents a wall
	 */
	public boolean isWall(char c) {
		return c == WALL;
	}

	/**
	 * check if specified coordinate is a wallbuy
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return if (x, y) is a wallbuy
	 */
	public boolean isWallbuy(int x, int y) {
		return isWallbuy(map[y][x]);
	}

	/**
	 * Check if specified coordinate is a wall
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return if (x, y) is a wall
	 */
	public boolean isWall(int x, int y) {
		return isWall(map[y][x]);
	}

	/**
	 * check if specified coordinate is a spawner
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return true if (x, y) is a spawner, false if not
	 */
	public boolean isSpawner(int x, int y) {
		return map[y][x] == SPAWNER;
	}

	/**
	 * returns the character at specified coordinate
	 * @param r row of map character array
	 * @param c column of map character array
	 * @return returns map[r][c]
	 */
	public char getTile(int r, int c) {
		if (r >= 0 && r < map.length && c >= 0 && c < map[0].length) {
			return map[r][c];
		} else {
			return EMPTY;
		}
	}

	/**
	 * gets the wallbuy thats adjacent to the wall that is hit at specified corner (corner is just a coordinate not an actual 'corner')
	 * @param c Corner
	 * @return null if there is no wallBuy, Wallbuy if there is an adjacent wallbuy
	 */
	public Wallbuy hitWallbuy(Corner c) {
		if (c.getX() % 1 == 0) {
			return getWallbuy((int) c.getX() - 1, (int) c.getY());
		} else if (c.getX() % 1 > 1 - GameMain.SMALL_NUMBER - GameMain.SMALL_NUMBER / 2
				&& c.getX() % 1 < 1 - GameMain.SMALL_NUMBER + GameMain.SMALL_NUMBER / 2) {
			return getWallbuy((int) c.getX() + 1, (int) c.getY());
		} else if (c.getY() % 1 < 0.5) {
			return getWallbuy((int) c.getX(), (int) c.getY() - 1);
		} else if (c.getY() % 1 > 0.5) {
			return getWallbuy((int) c.getX(), (int) c.getY() + 1);
		} else {
			return null;
		}
	}

	/**
	 * gets the Wallbuy at specified coordinate
	 * @param x coordinate x 
	 * @param y coordinate y
	 * @return null if there is not wall buy, Wallbuy if there is a wall buy
	 */
	public Wallbuy getWallbuy(int x, int y) {
		if (x >= 0 && x < width && y >= 0) {
			if (map[y][x] == RIFLE_BUY) {
				return rifleBuy;
			} else if (map[y][x] == PISTOL_BUY){
				return pistolBuy;
			} else if (map[y][x] == REVOLVER_BUY) {
				return revolverBuy;
			}
		}
		return null;
	}

	/**
	 * gets the map width
	 * @return map width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * gets map height
	 * @return map height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * gets character represenation of a wall
	 * @return WALL
	 */
	public static char getWall() {
		return WALL;
	}

	/**
	 * gets character representation of an empty space
	 * @return EMPTY
	 */
	public static char getEmpty() {
		return EMPTY;
	}
}
