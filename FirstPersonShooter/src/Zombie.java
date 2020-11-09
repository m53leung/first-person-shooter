

/**
 * A zombie is a special type of NPC that can move and attack the player and finds its own path to the player
 * @author matth
 *
 */
public class Zombie extends NPC {
	// static final Sprite texture = new Sprite("/zombie.png");
	static final double WIDTH = 0.17879746835;
	double speed;
	double health;
	double playerLastX, playerLastY;
	double attackRange;
	double attackDamage;
	int pointsOnKill;

	boolean spawning;
	long spawnStart;
	long spawnTime;
	Sprite[] spawnAnim;
	Sprite idleSprite;

	boolean hitXorY;

	/**
	 * constructor thats creates a zombie with the default speed
	 * @param x x
	 * @param y y
	 * @param zombieImg zombie image
	 * @param spawnAnim spawn animation Sprite array
	 */
	public Zombie(double x, double y, Sprite zombieImg, Sprite[] spawnAnim) {
		super(spawnAnim[0], WIDTH, x, y);
		idleSprite = zombieImg;
		speed = 0.01;
		health = 100;
		attackRange = 1;
		attackDamage = 0.5;
		pointsOnKill = 10;
		playerLastX = -1;
		playerLastY = -1;
		hitXorY = false;

		spawning = true;
		spawnStart = System.currentTimeMillis();
		spawnTime = 2500;
		this.spawnAnim = spawnAnim;

		// TODO Auto-generated constructor stub
	}
	
	/**
	 * constructor that you can make a zombie with a specified speed
	 * @param x x
	 * @param y y
	 * @param speed speed
	 * @param zombieImg zombie image
	 * @param spawnAnim zombie spawn animation sprite array
	 */
	public Zombie(double x, double y, double speed, Sprite zombieImg, Sprite[] spawnAnim) {
		super(spawnAnim[0], WIDTH, x, y);
		idleSprite = zombieImg;
		this.speed = speed;
		health = 100;
		attackRange = 1;
		attackDamage = 0.5;
		pointsOnKill = 10;
		playerLastX = -1;
		playerLastY = -1;
		hitXorY = false;

		spawning = true;
		spawnStart = System.currentTimeMillis();
		spawnTime = 2500;
		this.spawnAnim = spawnAnim;

		// TODO Auto-generated constructor stub
	}

	/**
	 * returns if zombie hit along x or y axis against a wall this update
	 * @return if zombie hit x or y
	 */
	public boolean getHitXorY() {
		return hitXorY;
	}

	/**
	 * setss hit x or y
	 * @param b boolean
	 */
	public void setHitXorY(boolean b) {
		hitXorY = b;
	}

	/**
	 * damage the zombie
	 * @param damage damage dealt to the zombie
	 * @return true if zombie is dead, false if zombie lived
	 */
	public boolean damage(double damage) {
		health -= damage;
		return health <= 0;
	}

	/**
	 * moves the zombie toward specified coordinate
	 * @param targetX target coordinate x
	 * @param targetY target coordinate y
	 */
	private void moveTowards(double targetX, double targetY) {
		// System.out.println("Moving towards: " + targetX + ", " + targetY);
		/*
		 * double angle = GameMain.getAngle(targetX, targetY, getX(), getY());
		 * setX(getX() + GameMain.getXComponent(angle, speed)); setY(getY() +
		 * GameMain.getYComponent(angle, speed));
		 */

		double deltaX = targetX - getX();
		double deltaY = targetY - getY();
		double deltaNet = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

		setX(getX() + speed * deltaX / deltaNet);
		setY(getY() + speed * deltaY / deltaNet);

	}

	public int getPointsOnKill() {
		return pointsOnKill;
	}

	/**
	 * act method of zombie, will run every game update
	 * @param game reference to GameMain class
	 */
	public void act(GameMain game) {

		if (!spawning) {

			// TODO Auto-generated method stub
			if (Math.sqrt(
					Math.pow(game.getPlayerX() - getX(), 2) + Math.pow(game.getPlayerY() - getY(), 2)) <= attackRange) {
				game.damagePlayer(attackDamage);
			}

			if (game.getMap().lineOfSight(getX(), getY(), game.getPlayerX(), game.getPlayerY(), getRadius())) {
				moveTowards(game.getPlayerX(), game.getPlayerY());
			} else {
				/*
				 * if ((int) getX() == (int) game.getPlayerX() && (int) getY() == (int)
				 * game.getPlayerY()) { moveTowards(game.getPlayerX(), game.getPlayerY()); }
				 * else {
				 * 
				 * targetNode = game.getPathFinder().findPath((int) getX(), (int) getY(), (int)
				 * game.getPlayerX(), (int) game.getPlayerY()); if (targetNode != null) {
				 * moveTowards(targetNode); }
				 * 
				 * }
				 */

				Corner target = game.getMap().findPath(getX(), getY(), game.getPlayerX(), game.getPlayerY(), getRadius());
				moveTowards(target.getX(), target.getY());

			}
		} else {
			
			long deltaStart = System.currentTimeMillis() - spawnStart;
			if (deltaStart >= spawnTime) {
				spawning = false;
				setSprite(idleSprite);
			} else {
				setSprite(spawnAnim[(int)((double)deltaStart / ((double)spawnTime / (double)spawnAnim.length))]);
			}
			
		}
	}

}
