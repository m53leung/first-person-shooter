import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Mini map GUI element
 * @author matth
 *
 */
public class MiniMap {
	
	static final char WALL = '|';
	
	int x, y; //x and y coordinate the mini map is to be drawn at
	int playerX, playerY;
	double playerAngle; //angle player is looking at
	int playerFOVDistance; //distance which the fov cone extends out to
	double playerFOVAngle; //players fov
	int ppu; //pixels per unit (one unit is one grid on the map)
	int borderSize;
	
	Corner[] enemyList;
	
	Color bgColor; //background color
	Color borderColor;
	Color wallColor;
	Color playerColor;
	Color FOVColor;
	Color enemyColor;
	
	Map map;
	
	public MiniMap(Map map, int pixelsPerUnit, int borderSize, int x, int y, int FOVDistance, double FOVAngle) {
		this.x = x;
		this.y = y;
		this.map = map;
		ppu = pixelsPerUnit;
		this.borderSize = borderSize;
		bgColor = Color.WHITE;
		borderColor = new Color(143, 108, 93);
		wallColor = Color.GRAY;
		playerColor = Color.BLUE;
		FOVColor = new Color(0, 0, 0, 100);
		enemyColor = Color.RED;
		playerFOVDistance = FOVDistance;
		playerFOVAngle = FOVAngle;
	}
	
	/**
	 * gets width of the mini map gui element
	 * @return mini map width
	 */
	public int getWidth() {
		return 2 * borderSize + ppu * map.getWidth();
	}
	
	/**
	 * set enemy list
	 * @param a enemy array
	 */
	public void setEnemies(Corner[] a) {
		enemyList = a;
	}
	
	/**
	 * set player position on the mini map
	 * @param x x location of player
	 * @param y y location of player
	 */
	public void setPlayerPos(int x, int y) {
		playerX = x;
		playerY = y;
	}
	
	/**
	 * Sets player look angle
	 * @param angle player looking angle
	 */
	public void setPlayerAngle(double angle) {
		playerAngle = angle;
	}
	
	/**
	 * Draws a dot on the mini map
	 * @param g2d Graphics2D component
	 * @param color color of the dot
	 * @param posX x coordinate of dot
	 * @param posY y coordinate of dot
	 */
	public void drawDot(Graphics2D g2d, Color color, int posX, int posY) {
		g2d.setColor(color);
		g2d.fillRect(x + borderSize + ppu * posX, y + borderSize + ppu * posY, ppu, ppu);
	}
	
	/**
	 * Draws the minimap
	 * @param g2d Graphics2D components
	 */
	public void draw(Graphics2D g2d) {
		//Draw border
		g2d.setColor(borderColor);
		g2d.fillRect(x, y, ppu * map.getWidth() + 2 * borderSize, ppu * map.getHeight() + 2 * borderSize);
		
		//Draw background
		g2d.setColor(bgColor);
		g2d.fillRect(x + borderSize, y + borderSize, ppu * map.getWidth(), ppu * map.getHeight());
		
		//Draw walls
		g2d.setColor(wallColor);
		for (int r = 0; r < map.getHeight(); r ++) {
			for (int c = 0; c < map.getWidth(); c ++) {
				if(map.getTile(r, c) == WALL) {
					g2d.fillRect(x + borderSize + ppu * c, y + borderSize + ppu * r, ppu, ppu);
				}
			}
		}
		
		//Draw enemies
		if (enemyList != null) {
			g2d.setColor(enemyColor);
			for (int i = 0; i < enemyList.length; i ++) {
				g2d.fillRect(x + borderSize + ppu * (int)enemyList[i].getX(), y + borderSize + ppu * (int)enemyList[i].getY(), ppu, ppu);
			}
		}
		
		//Draw player
		g2d.setColor(playerColor);
		g2d.fillRect(x + borderSize + ppu * playerX, y + borderSize + ppu * playerY, ppu, ppu);
		
		//Draw player fov
		int x1 = x + borderSize + ppu * playerX + ppu / 2;
		int y1 = y + borderSize + ppu * playerY + ppu / 2;
		int x2 = x1 + (int)(playerFOVDistance * Math.sin(playerAngle - playerFOVAngle / 2) + 0.5);
		int y2 = y1 - (int)(playerFOVDistance * Math.cos(playerAngle - playerFOVAngle / 2) + 0.5);
		int x3 = x1 + (int)(playerFOVDistance * Math.sin(playerAngle + playerFOVAngle / 2) + 0.5);
		int y3 = y1 - (int)(playerFOVDistance * Math.cos(playerAngle + playerFOVAngle / 2) + 0.5);
		
		g2d.setColor(FOVColor);
		g2d.fillPolygon(new int[] {x1, x2, x3}, new int[] {y1, y2, y3}, 3);
	}
}
