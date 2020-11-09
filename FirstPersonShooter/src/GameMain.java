import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class of the game
 * 
 * @author https://www.ntu.edu.sg/home/ehchua/programming/java/J8d_Game_Framework.html
 *
 */
public class GameMain extends JPanel { // main class for the game

	//Constants for development and testing, these should never be changed for the actual game
	static final boolean INVINCIBILITY = false;
	static final boolean ZOMBIE_WALL_COLLISIONS = true;
	static final boolean MINI_MAP_ENABLED = false;

	//small number that is used throughout the project
	static final double SMALL_NUMBER = 0.0000000001;

	// Define constants for the game
	//last minute changed CANVAS_WIDTH and CANVAS_HEIGHT to variables so that the game can be run at different resolutions
	int CANVAS_WIDTH; // width and height of the game screen
	int CANVAS_HEIGHT;
	static final int UPDATES_PER_SEC = 60; // number of game update per second
	static final long UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC; // nanoseconds
	// ......
	static final String TITLE = "GunnerZ";
	static final double RAY_INCREMENT = 0.05; //length to increment ray for checking if shooting a zombie (NOT used for the ray tracing in the rendering aspect)
	static final Color COLOUR_WALL = Color.WHITE;
	static final Color COLOUR_FLOOR = new Color(161, 161, 161);
	static final Color COLOUR_CEILING = new Color(69, 69, 69);

	static final double WALL_HEIGHT = 1; //height of walls with each unit being one "grid" on the map

	//constants for the crosshair gui drawing
	static final Color CROSSHAIR_COLOUR = Color.white;
	static final int CROSSHAIR_THICKNESS = 1;
	static final int CROSSHAIR_LENGTH = 20;

	//constants for hitmarkers gui drawing
	static final Color HIT_MARKER_COLOUR = Color.red;
	static final int HIT_MARKER_SIZE = 10;
	static final int HIT_MARKER_THICKNESS = 2;
	static final int HIT_MARKER_FRAMES = 5;

	//constants for health symbol gui drawing
	static final int HEALTH_SYMBOL_PADDING = 20;
	static final int HEALTH_SYMBOL_LENGTH = 80;
	static final int HEALTH_SYMBOL_WIDTH = 30;
	static final int HEALTH_SYMBOL_OUTLINE = 15;

	//constants for ammo count gui drawing
	static final int AMMO_COUNT_OPACITY = 100;
	static final int AMMO_COUNT_WIDTH = 10;
	static final int AMMO_COUNT_HEIGHT = 30;
	static final int AMMO_COUNT_PADDING = 10;

	//constants for score and wave gui drawing
	static final int SCORE_PADDING = 20;
	static final int WAVE_PADDING = SCORE_PADDING;
	static final int WAVE_Y_OFFSET = 80;

	//constants for damage outline gui drawing (damage outline appears when player is being damaged)
	static final int DAMAGE_OUTLINE_WIDTH = 20;
	static final Color DAMAGE_OUTLINE_COLOUR = Color.red;

	//constants for message gui drawing
	static final int MESSAGE_X_OFFSET = 10;
	static final int MESSAGE_Y_OFFSET = 50;

	static final int MAX_ZOMBIES = 30; // the variable int maxZombies can not exceed MAX_ZOMBIES, MAX_ZOMBIES is to
										// ensure the game does not get laggy
	static final double ZOMBIE_MAX_SPEED = 0.04; // the variable zombieMaxSpeed cannot exceed ZOMBIE_MAX_SPEED or else
													// game will just get impossible
	//static final double ZOMBIE_MIN_SPEED = 0.01;
	static final int SPAWN_DELAY_MIN = 200; //minimum time between zombie spawn, enemySpawnDelay cannot be lower than SPAWN_DELAY_MIN
	static final int GRACE_PERIOD = 10000; //time between rounds
	
	//Constants for text that shows up during new waves
	static final int NEW_WAVE_X_OFFSET = 70;
	static final int NEW_WAVE_Y_OFFSET = -60;
	static final int NEW_WAVE_FONT_SIZE = 200;
	static final int NEW_WAVE_R = 200;
	static final int NEW_WAVE_G = 0;
	static final int NEW_WAVE_B = 0;
	
	//default player values
	static final int DEFAULT_HEALTH = 50;
	static final double DEFAULT_SPEED = 0.05;
	
	//range player can buy from perk machines
	static final double INTERACT_RANGE = 1;
	
	//hit marker sound effect url
	static final URL HIT_MARKER_SOUND_EFFECT = GameMain.class.getResource("/HitMarkerSoundEffect.wav");

	// Enumeration for the states of the game.
	static enum GameState {
		INITIALIZED, PLAYING, PAUSED, GAMEOVER, DESTROYED
	}

	static GameState state; // current state of the game

	long lastUpdate;
	static ArrayList<SoundPlayer> sounds = new ArrayList<SoundPlayer>();
	
	// Define instance variables for the game objects
	//Variables for "pre loading" the images
	//All images in imageLoadList are just drawn right away when the game starts so they don't lag when drawn later
	boolean loadingScreen;
	boolean loading;
	ArrayList<Sprite> imageLoadList;

	Random r; //Random object

	//ArrayList<Drawable> zBuffer; // holds the drawables of all walls this frame

	Sprite wall; //Sprite for walls

	Map map;
	MiniMap miniMap; //mini map object, apart of the gui
	//Originally I used the PathFinder class to find paths for the zombies, but I added a more optimized and better pathfinding algorithm to the Map class.

	String message; //Message displayed in the top left to prompt player for wallbuys and perks

	// Guns
	Gun currentGun; //Currently equipped gun
	Gun[] gunList; //list of all guns in the game

	Gun pistol;
	Sprite pistolIdleImage; //Sprite for the gun when not doing anything
	Sprite[] pistolFireAnim; //contains all sprites for the firing animation
	Sprite[] pistolReloadAnim; //sprites for reloading animation
	Sprite pistolWallbuyImg; //sprite representing the wall buy of this gun

	Gun revolver;
	Sprite revolverIdleImage;
	Sprite[] revolverFireAnim;
	Sprite[] revolverReloadAnim;
	Sprite revolverWallbuyImg;

	Gun rifle;
	Sprite rifleIdleImage;
	Sprite[] rifleFireAnim;
	Sprite[] rifleReloadAnim;
	Sprite rifleWallbuyImg;

	// game variables
	boolean gameOver;

	int wave; //wave number
	int maxZombies; // number of zombies in the world at one time
	int enemySpawnDelay; //time between zombie spawns
	double zombieSpeed; //speed new zombies are spawned with
	int zombiesThisWave; // number of zombies in total in this wave
	int zombiesSpawned; // number of zombies spawned so far in this wave
	boolean gracePeriod;
	long gracePeriodStart; //start time of this grace period
	URL roundStartSE = GameMain.class.getResource("/RoundStart.wav");//sound to play when new round starts
	Color newWaveColor; // color of the text that shows during a new round

	int hmCurrentFrame; //hit marker current frame, can count how many frames the hitmarker has displayed for
	boolean damagedThisTick; //if player was damaged in this update (used for the damage outline)

	//key input booleans
	boolean fire;
	boolean reload;
	boolean interact;
	
	boolean hit; //boolean storing if player hit a zombie this update (used for hitmarker)

	//player position/angle variables
	double playerAngle; //direction player is looking in
	double x, y;//player location x and y
	double turnSpeed;//how fast player can turn
	double moveSpeed;//how fast player can move
	double FOV;//field of view of camera
	double vFOV;//vertical field of view of camera (based off of CANVAS_WIDTH and CANVAS_HEIGHT and FOV)
	double playerRadius; //techincally not radius, but width / 2 because player hitbox is a square
	boolean hitXorY; //if player is colliding directly along the x or y axis

	long enemyLastSpawn; //time enemy was last spawned

	double playerMaxHealth;//current max health of player
	double playerHealth;//current health of player
	int healthRegenDelay;//time before health regen starts
	double healthRegenRate;//reate at health regens
	long timeSinceLastHit;//time since player was last damaged
	
	int playerPoints;//how many points the player has

	ArrayList<Spawner> spawners;//list of all Spawner objects
	ArrayList<NPC> npcList;//list of all NPC objects
	ArrayList<Zombie> zombieList;//list of all Zombie objects

	Sprite zombieSprite;
	Sprite[] zombieSpawnAnim;//list of sprites of zombie spawning animation

	// key press variables for looking and moving
	int lookLeft, lookRight;
	int moveAhead, moveBack, moveRight, moveLeft;

	// Handle for the custom drawing panel
	private GameCanvas canvas;

	// Constructor to initialize the UI components and game objects
	public GameMain(int resolutionWidth, int resolutionHeight) {
		// Initialize the game objects
		CANVAS_WIDTH = resolutionWidth;
		CANVAS_HEIGHT = resolutionHeight;
		gameInit();

		// UI components
		canvas = new GameCanvas();
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		add(canvas);

		// Other UI components such as button, score board, if any.
		// ......

		gameStart();
	}

	// All the game related codes here

	// Initialize all the game objects, run only once in the constructor of the main
	// class.
	public void gameInit() {
		// ......
		//initializing all the variables
		//Any time a sprite is intialized, it is also added to imageLoadList so that it will be loaded
		loadingScreen = true;
		loading = true;
		imageLoadList = new ArrayList<Sprite>();

		r = new Random();

		//zBuffer = new ArrayList<Drawable>(CANVAS_WIDTH + MAX_ZOMBIES + 4);
		//npcBuffer = new ArrayList<Drawable>(MAX_ZOMBIES);
		//drawables = new ArrayList<ArrayList<Drawable>>(MAX_ZOMBIES + 1);

		playerAngle = Math.PI / 2;
		x = 3.5;
		y = 6.5;
		turnSpeed = 0.03;
		moveSpeed = DEFAULT_SPEED;
		FOV = Math.PI / 4;
		vFOV = FOV * ((double) CANVAS_HEIGHT / (double) CANVAS_WIDTH);
		//viewDistance = 10;
		playerRadius = 0.3;

		wall = new Sprite("/BrickTexture.png");
		imageLoadList.add(wall);

		message = "";

		// Setup wallbuys
		rifleWallbuyImg = new Sprite("/Rifle_Wallbuy_Updated.png");
		imageLoadList.add(rifleWallbuyImg);

		pistolWallbuyImg = new Sprite("/Pistol_Wallbuy_Updated.png");
		imageLoadList.add(pistolWallbuyImg);

		revolverWallbuyImg = new Sprite("/Revolver_Wallbuy_Updated.png");
		imageLoadList.add(revolverWallbuyImg);

		map = new Map("/map.txt", 20, 26, new Wallbuy("Rifle", rifleWallbuyImg),
				new Wallbuy("Pistol", pistolWallbuyImg), new Wallbuy("Revolver", revolverWallbuyImg));
		miniMap = new MiniMap(map, 8, 0, 0, 0, 40, FOV);
		//pathFinder = new PathFinder(map);

		playerMaxHealth = DEFAULT_HEALTH;
		playerHealth = playerMaxHealth;
		healthRegenDelay = 5000;
		healthRegenRate = 0.25;
		timeSinceLastHit = System.currentTimeMillis();
		
		playerPoints = 100;

		damagedThisTick = false;

		enemyLastSpawn = System.currentTimeMillis();

		// intialize spawners
		//loop through map and if it is a spawner add a new spawner to spawner list with x and y coordinates
		spawners = new ArrayList<Spawner>();
		for (int r = 0; r < map.getHeight(); r++) {

			for (int c = 0; c < map.getWidth(); c++) {
				if (map.isSpawner(c, r)) {
					spawners.add(new Spawner(c, r));
				}
			}

		}

		zombieSprite = new Sprite("/zombie.png");
		imageLoadList.add(zombieSprite);

		//grabs pictures of zombie spawning animations (Pictures are named Zombie_Spawning_1.png, Zombie_Spawning_2.png ...)
		zombieSpawnAnim = new Sprite[17];
		for (int c = 0; c < zombieSpawnAnim.length; c++) {
			zombieSpawnAnim[c] = new Sprite("/Zombie_Spawning_" + (c + 1) + ".png");
			imageLoadList.add(zombieSpawnAnim[c]);
		}

		npcList = new ArrayList<NPC>(MAX_ZOMBIES + 4); //max number of NPC is max Zombies plus the four perk machines
		zombieList = new ArrayList<Zombie>(MAX_ZOMBIES);
		
		//get perks from map and add their NPC to npclist
		for (int i = 0; i < map.getPerks().size(); i ++) {
			npcList.add(map.getPerks().get(i).getNPC());
		}
		
		fire = false;
		reload = false;
		hit = false;
		hmCurrentFrame = 0;

		// initialize guns
		// pistol
		//grabs firing pictures
		pistolFireAnim = new Sprite[18];
		for (int c = 0; c < pistolFireAnim.length; c++) {
			pistolFireAnim[c] = new Sprite("/Pistol_Anim_Fire_" + (c + 1) + ".gif");
			imageLoadList.add(pistolFireAnim[c]);
		}
		//grabs reloading pictures
		pistolReloadAnim = new Sprite[129];
		for (int c = 0; c < (pistolReloadAnim.length + 1) / 2; c++) {
			pistolReloadAnim[c] = new Sprite("/Pistol_Reload_" + (c + 1) + ".png");
			imageLoadList.add(pistolReloadAnim[c]);
		}
		//once halfway pictures go in the opposite order
		//(reuses same pictures for second half of animation but in the reverse order
		for (int c = (pistolReloadAnim.length + 1) / 2; c < pistolReloadAnim.length; c++) {
			pistolReloadAnim[c] = new Sprite("/Pistol_Reload_" + (pistolReloadAnim.length - c) + ".png");
			imageLoadList.add(pistolReloadAnim[c]);
		}
		//grab idle image
		pistolIdleImage = new Sprite("/Pistol_Idle_Fixed.png");
		imageLoadList.add(pistolIdleImage);
		//initialize Gun object
		pistol = new Gun("Pistol", 300, 1500, 10, 20, 10, pistolIdleImage, pistolFireAnim, pistolReloadAnim,
				"/Pistol_Fire_Sound_Effect.wav", "/Pistol_Reload_Sound_Effect.wav");

		// revolver
		revolverFireAnim = new Sprite[42];
		for (int c = 0; c < revolverFireAnim.length; c++) {
			revolverFireAnim[c] = new Sprite("/RevolverFire_" + (c + 1) + ".gif");
			imageLoadList.add(revolverFireAnim[c]);
		}
		revolverReloadAnim = new Sprite[29];
		for (int c = 0; c < revolverReloadAnim.length; c++) {
			revolverReloadAnim[c] = new Sprite("/RevolverReload_" + (c + 1) + ".png");
			imageLoadList.add(revolverReloadAnim[c]);
		}

		revolverIdleImage = new Sprite("/Revolver.gif");
		imageLoadList.add(revolverIdleImage);
		revolver = new Gun("Revolver", 500, 2800, 6, 50, 200, revolverIdleImage, revolverFireAnim,
				revolverReloadAnim, "/RevolverFireSoundEffect.wav", "/RevolverReloadSoundEffect.wav");

		// rifle
		rifleIdleImage = new Sprite("/Rifle_Idle.png");
		imageLoadList.add(rifleIdleImage);

		rifleFireAnim = new Sprite[31];
		for (int c = 0; c < rifleFireAnim.length; c++) {
			rifleFireAnim[c] = new Sprite("/Rifle_Anim_Fire_" + (c + 1) + ".png");
			imageLoadList.add(rifleFireAnim[c]);
		}

		rifleReloadAnim = new Sprite[45];
		for (int c = 0; c < rifleReloadAnim.length; c++) {
			rifleReloadAnim[c] = new Sprite("/Rifle_Anim_Reload_" + (c + 1) + ".png");
			imageLoadList.add(rifleReloadAnim[c]);
		}

		rifle = new Gun("Rifle", 100, 2000, 25, 25, 800, rifleIdleImage, rifleFireAnim, rifleReloadAnim,
				"/RifleFireSoundEffect.wav", "/RifleReloadSoundEffect.wav");

		//add the guns to the gunList
		gunList = new Gun[3];
		gunList[0] = pistol;
		gunList[1] = revolver;
		gunList[2] = rifle;

		//set the current gun
		currentGun = pistol;

		wave = 1;
		maxZombies = 10;
		zombiesThisWave = 5;
		enemySpawnDelay = 5000;
		zombieSpeed = 0.01;
		gracePeriod = false;
		
		newWaveColor = new Color(NEW_WAVE_R, NEW_WAVE_G, NEW_WAVE_B, 0); //color of new wave text
		
		state = GameState.INITIALIZED;
	}

	// Draw all the images on screen so that they don't lag when they are drawn
	// later
	public void loadImages(Graphics2D g2d, ArrayList<Sprite> loadList) {
		//goes through loadList and draws the images
		for (int i = 0; i < loadList.size(); i++) {
			loadList.get(i).draw(g2d, 0, 0);
		}
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
	}

	// Shutdown the game, clean up code that runs only once.
	public void gameShutdown() {
		// ......
		JOptionPane.showMessageDialog(this, "Game Over");
		System.exit(0);
	}

	// To start and re-start the game.
	public void gameStart() {
		// Create a new thread
		Thread gameThread = new Thread() {
			// Override run() to provide the running behavior of this thread.
			@Override
			public void run() {
				gameLoop();
			}
		};
		// Start the thread. start() calls run(), which in turn calls gameLoop().
		gameThread.start();

	}

	// Run the game loop here.
	private void gameLoop() {
		// Regenerate the game objects for a new game
		// ......

		startGracePeriod(); //starts the initial grace period before round 1 starts
		lastUpdate = System.currentTimeMillis();
		
		state = GameState.PLAYING;

		// Game loop
		//not my code, game loop was apart of the framework
		long beginTime, timeTaken, timeLeft; // in msec
		while (state != GameState.GAMEOVER) {
			beginTime = System.nanoTime();
			if (state == GameState.PLAYING) {
				// Update the state and position of all the game objects,
				// detect collisions and provide responses.
				gameUpdate();
			}
			// Refresh the display
			repaint();
			// Delay timer to provide the necessary delay to meet the target rate
			timeTaken = System.nanoTime() - beginTime;
			timeLeft = (UPDATE_PERIOD_NSEC - timeTaken) / 1000000; // in milliseconds
			if (timeLeft < 10)
				timeLeft = 10; // set a minimum
			try {
				// Provides the necessary delay and also yields control so that other thread can
				// do work.
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) {
			}
		}
		gameShutdown();
	}

	// Update the state and position of all the game objects,
	// detect collisions and provide responses.
	/*
	 * Method runs every game update
	 */
	public void gameUpdate() {
		
		long deltaTime = System.currentTimeMillis() - lastUpdate;
		
		lastUpdate = System.currentTimeMillis();
		
		for (int i = 0; i < sounds.size(); i ++) {
			if (sounds.get(i).addToTimer(deltaTime)) {
				sounds.remove(i);
				i --;
			}
		}
		
		message = "";

		//check for gun firing and if it hits a zombie
		if (fire && currentGun.fire()) {//fire method of Gun returns true if the gun can be fired
			Zombie temp = hitZombie(playerAngle, x, y); //returns a zombie if shot hits a zombie, returns null if not
			if (temp != null) {
				damageZombie(temp, currentGun.getDamage()); //deals getDamage() damage to temp
				hit = true;
				music(HIT_MARKER_SOUND_EFFECT); //plays hit marker sound effect
			}
		}

		if (reload) { //only checks for reload key input, Gun.reload() will check if the gun is reloadable
			currentGun.reload();
		}
		
		//check for perk buying
		for (int i = 0; i < map.getPerks().size(); i ++) { //loop through perk list of map
			if (getDistance(x, y, map.getPerks().get(i).getX() + 0.5, map.getPerks().get(i).getY() + 0.5) <= INTERACT_RANGE) { //if player is closer or equal to perk as INTERACT_RANGE
				if (map.getPerks().get(i).isBought() == false) { //check if perk is already bought
					message = "Press E to buy " + map.getPerks().get(i).getName() + " (" + map.getPerks().get(i).getPrice() + "pts)";
					if (playerPoints >= map.getPerks().get(i).getPrice() && interact) { //check if player is buying perk
						playerPoints -= map.getPerks().get(i).getPrice();
						map.getPerks().get(i).effect(this);
					}
				} else {
					message = map.getPerks().get(i).getName() + " already bought!";
				}
				break;
			}
		}

		//check if player is standing on a wallbuy tile
		Wallbuy wallbuy = map.getWallbuy((int) x, (int) y); //wallbuy will be null if player is not standing on a wall buy
		if (wallbuy != null) {
			Gun gun = getGun(wallbuy.getGun());
			if (gun != null) {
				if (gun != currentGun) {
					message = "Press E to buy " + gun.getName() + " (" + gun.getCost() + "pts)";
					if (interact && playerPoints >= gun.getCost()) {
						playerPoints -= gun.getCost();
						currentGun = gun;
					}
				} else {
					message = gun.getName() + " already equipped!";
				}
			} else {
				System.out.println("ERROR GUN " + wallbuy.getGun() + " NOT FOUND");
			}
		}

		int lookInput = lookRight - lookLeft; //if right and left are both pressed lookInput will just be 0
		playerAngle += turnSpeed * lookInput;
		//restricts player angle to 0 - 2Pi
		if (playerAngle > 2 * Math.PI) {
			playerAngle = playerAngle % (2 * Math.PI);
		} else if (playerAngle < 0) {
			playerAngle = 2 * Math.PI + playerAngle;
		}

		double inputZ = moveAhead - moveBack;
		double inputX = moveRight - moveLeft;
		//gets x and y inputs and move player in the appropriate direction
		if (inputZ != 0 || inputX != 0) {
			double inputAngle;
			//gets the input angle from x and y input
			if (inputZ == 1 && inputX == 0) {
				inputAngle = 0;
			} else if (inputZ == 1 && inputX == 1) {
				inputAngle = Math.PI / 4;
			} else if (inputZ == 0 && inputX == 1) {
				inputAngle = Math.PI / 2;
			} else if (inputZ == -1 && inputX == 1) {
				inputAngle = 3 * Math.PI / 4;
			} else if (inputZ == -1 && inputX == 0) {
				inputAngle = Math.PI;
			} else if (inputZ == -1 && inputX == -1) {
				inputAngle = 5 * Math.PI / 4;
			} else if (inputZ == 0 && inputX == -1) {
				inputAngle = 3 * Math.PI / 2;
			} else {
				inputAngle = 7 * Math.PI / 4;
			}
			//determine how much to move the player x and y based on the input angle and the players current angle, and movement speed
			double moveAngle = playerAngle + inputAngle;
			double deltaX = moveSpeed * Math.sin(moveAngle);
			double deltaY = -1 * moveSpeed * Math.cos(moveAngle);
			x += deltaX;
			y += deltaY;
		}

		// Check player collisions with wall
		// Check if colliding with wall in y direction
		if (map.getTile((int) (y - playerRadius), (int) x) == Map.getWall()) {
			hitBoundsAbove();
		} else if (map.getTile((int) (y + playerRadius), (int) x) == Map.getWall()) {
			hitBoundsBelow();
		}

		// check if colliding with wall in x direction
		if (map.getTile((int) y, (int) (x - playerRadius)) == Map.getWall()) {
			hitBoundsLeft();
		} else if (map.getTile((int) y, (int) (x + playerRadius)) == Map.getWall()) {
			hitBoundsRight();
		}

		// check if colliding with wall on diagonals
		if (!hitXorY) {
			double localX = x % 1;
			double localY = y % 1;
			if (map.getTile((int) (y - playerRadius), (int) (x + playerRadius)) == Map.getWall()) {
				if (localY > 1 - localX) {
					hitBoundsAbove();
				} else {
					hitBoundsRight();
				}
			} else if (map.getTile((int) (y + playerRadius), (int) (x + playerRadius)) == Map.getWall()) {
				if (1 - localY > 1 - localX) {
					hitBoundsBelow();
				} else {
					hitBoundsRight();
				}
			} else if (map.getTile((int) (y + playerRadius), (int) (x - playerRadius)) == Map.getWall()) {
				if (1 - localY > localX) {
					hitBoundsBelow();
				} else {
					hitBoundsLeft();
				}
			} else if (map.getTile((int) (y - playerRadius), (int) (x - playerRadius)) == Map.getWall()) {
				if (localY > localX) {
					hitBoundsAbove();
				} else {
					hitBoundsLeft();
				}
			}
		}

		hitXorY = false;

		//make sure player does not walk out of the map
		if (x + playerRadius >= map.getWidth()) {
			x = map.getWidth() - playerRadius;
		} else if (x - playerRadius < 0) {
			x = playerRadius;
		}
		if (y + playerRadius >= map.getHeight()) {
			y = map.getHeight() - playerRadius;
		} else if (x - playerRadius < 0) {
			x = playerRadius;
		}

		damagedThisTick = false;

		// Update zombies
		for (int c = 0; c < zombieList.size(); c++) {
			Zombie temp = zombieList.get(c);
			temp.act(this);

			//checking wall collisions with zombies
			//Exact same code as for player wall collisions
			//I should have made a player NPC to represent the player position, and then I could have made a method for wall collisions which takes a NPC parameter
			if (ZOMBIE_WALL_COLLISIONS) {
				// Check zombie collisions with wall
				// Check if colliding with wall in y direction
				if (map.getTile((int) (temp.getY() - temp.getRadius()), (int) temp.getX()) == Map.getWall()) {
					hitBoundsAbove(temp);
				} else if (map.getTile((int) (temp.getY() + temp.getRadius()), (int) temp.getX()) == Map.getWall()) {
					hitBoundsBelow(temp);
				}

				// check if colliding with wall in x direction
				if (map.getTile((int) temp.getY(), (int) (temp.getX() - temp.getRadius())) == Map.getWall()) {
					hitBoundsLeft(temp);
				} else if (map.getTile((int) temp.getY(), (int) (temp.getX() + temp.getRadius())) == Map.getWall()) {
					hitBoundsRight(temp);
				}

				// check if colliding with wall on diagonals
				if (temp.getHitXorY() == false) {
					double localX = temp.getX() % 1;
					double localY = temp.getY() % 1;
					if (map.getTile((int) (temp.getY() - temp.getRadius()),
							(int) (temp.getX() + temp.getRadius())) == Map.getWall()) {
						if (localY > 1 - localX) {
							hitBoundsAbove(temp);
						} else {
							hitBoundsRight(temp);
						}
					} else if (map.getTile((int) (temp.getY() + temp.getRadius()),
							(int) (temp.getX() + temp.getRadius())) == Map.getWall()) {
						if (1 - localY > 1 - localX) {
							hitBoundsBelow(temp);
						} else {
							hitBoundsRight(temp);
						}
					} else if (map.getTile((int) (temp.getY() + temp.getRadius()),
							(int) (temp.getX() - temp.getRadius())) == Map.getWall()) {
						if (1 - localY > localX) {
							hitBoundsBelow(temp);
						} else {
							hitBoundsLeft(temp);
						}
					} else if (map.getTile((int) (temp.getY() - temp.getRadius()),
							(int) (temp.getX() - temp.getRadius())) == Map.getWall()) {
						if (localY > localX) {
							hitBoundsAbove(temp);
						} else {
							hitBoundsLeft(temp);
						}
					}
				}

				temp.setHitXorY(false);
			}

			//make sure zombies stay inside the map
			if (temp.getX() + temp.getRadius() >= map.getWidth()) {
				temp.setX(map.getWidth() - temp.getRadius());
			} else if (temp.getX() - temp.getRadius() < 0) {
				temp.setX(temp.getRadius());
			}

			if (temp.getY() + temp.getRadius() >= map.getHeight()) {
				temp.setY(map.getHeight() - temp.getRadius());
			} else if (temp.getY() - temp.getRadius() < 0) {
				temp.setY(temp.getRadius());
			}

		}
		
		if (damagedThisTick) {
			timeSinceLastHit = System.currentTimeMillis();
		}

		//health regen
		if (System.currentTimeMillis() - timeSinceLastHit >= healthRegenDelay) {
			playerHealth += healthRegenRate;
			if (playerHealth >= playerMaxHealth) {
				playerHealth = playerMaxHealth;
			}
		}
		
		//if there is currently a grace period, dont spawn anything and displaye new wave text, else spawn zombies
		if (gracePeriod == false) {
			if (zombiesSpawned < zombiesThisWave) { //do not spawn zombies if zombiesSpawned equals or exceeds zombiesThisWave
				if (System.currentTimeMillis() - enemyLastSpawn >= enemySpawnDelay) {

					if (zombieList.size() < maxZombies) {
						int i = r.nextInt(spawners.size());

						addZombie(new Zombie(spawners.get(i).getXLoc() + 0.5, spawners.get(i).getYLoc() + 0.5,
								zombieSpeed, zombieSprite,
								zombieSpawnAnim));
					}

					enemyLastSpawn = System.currentTimeMillis();
				}
			} else if (zombieList.size() == 0) {
				nextWave();
			}
		} else if (System.currentTimeMillis() - gracePeriodStart >= GRACE_PERIOD) { //end grace period if time exceeds GRACE_PERIOD
			gracePeriod = false;
		} else {
			//make the new wave text fade in and out
			int alpha;
			if ((double) (System.currentTimeMillis() - gracePeriodStart) / (double) GRACE_PERIOD < 0.5) {
				alpha = (int) (2 * 255 * (double) (System.currentTimeMillis() - gracePeriodStart)
						/ (double) GRACE_PERIOD);
			} else {
				alpha = (int) (255 * 2
						* (1 - (double) (System.currentTimeMillis() - gracePeriodStart) / (double) GRACE_PERIOD));
			}
			newWaveColor = new Color(NEW_WAVE_R, NEW_WAVE_G, NEW_WAVE_B, alpha);
		}

	}

	// Modified version of Map.lineOfSight() method
	/*
	 * returns the Corner(Corrdinate) of the hit location the ray hits a wall
	 */
	private Corner rayTrace(double x, double y, double deltaX, double deltaY) {
		//old rayTrace method incremented the ray by a tiny amount and checked if that point was colliding with a wall
		//very inefficient for 1080 pixels across the screen
		//new rayTrace method goes directly to the next grid

		if (x < 0 || (int) x >= map.getWidth() || y < 0 || y >= map.getHeight()) { // if tile is out of bounds
			return new Corner(x, y);
		} else if (map.isWall((int) x, (int) y)) { // If current tile is a wall then return current coords
			return new Corner(x, y);
		} else if (deltaX == 0) { // Need to have a case if deltaX == 0 or else an error will occur when trying to
									// calculate slope in the next case
			if (deltaY > 0) {
				return rayTrace(x, (int) y + 1, deltaX, deltaY);
			} else {
				return rayTrace(x, (int) y - 1, deltaX, deltaY);
			}
		} else {
			double slope = deltaY / deltaX;
			if (deltaX > 0) {
				if (slope >= 0) {
					// line is going to the right and down
					// check if line intersects tile below or tile to the right
					// Calculate y when x is at the border of the tile to the right
					double yNext = y + slope * ((int) x + 1 - x);
					if ((int) y == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the right
						return rayTrace((int) x + 1, yNext, deltaX, deltaY);
					} else { // else line must intersect with tile below
						double xNext = x + ((int) y + 1 - y) / slope;
						return rayTrace(xNext, (int) y + 1, deltaX, deltaY);
					}
				} else {
					// line is going to the right and up
					// check if line intersects tile to the right or above
					// calculate y when x is at the border of the tile to the right
					double yNext = y + slope * ((int) x + 1 - x);
					if ((int) y == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the right
						return rayTrace((int) x + 1, yNext, deltaX, deltaY);
					} else { // else line must intersect with tile above
						double xNext = x + ((int) y - SMALL_NUMBER - y) / slope;
						return rayTrace(xNext, (int) y - SMALL_NUMBER, deltaX, deltaY);
					}
				}
			} else {
				if (slope >= 0) {
					// line is going to the left and up
					// check if line intersects tile above or tile to the left
					// Calculate y when x is at the border of the tile to the left
					double yNext = y + slope * ((int) x - SMALL_NUMBER - x);
					if ((int) y == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the left
						return rayTrace((int) x - SMALL_NUMBER, yNext, deltaX, deltaY);
					} else { // else line must intersect with tile above
						double xNext = x + ((int) y - SMALL_NUMBER - y) / slope;
						return rayTrace(xNext, (int) y - SMALL_NUMBER, deltaX, deltaY);
					}
				} else {
					// line is going to the left and down
					// check if line intersects tile to the left or below
					// calculate y when x is at the border of the tile to the left
					double yNext = y + slope * ((int) x - SMALL_NUMBER - x);
					if ((int) y == (int) yNext) { // If yNext is same as y1 then line intersects with tile to the left
						return rayTrace((int) x - SMALL_NUMBER, yNext, deltaX, deltaY);
					} else { // else line must intersect with tile below
						double xNext = x + ((int) y + 1 - y) / slope;
						return rayTrace(xNext, (int) y + 1, deltaX, deltaY);
					}
				}
			}
		}
	}

	// Refresh the display. Called back via repaint(), which invoke the
	// paintComponent().
	//Original idea for ray casting https://www.youtube.com/watch?v=xW8skO7MFYw
	private void gameDraw(Graphics2D g2d) {
		if (loadingScreen) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
			loadingScreen = false;
		} else if (loading) {
			loadImages(g2d, imageLoadList);
			loading = false;
		} else {
			// Draw floor
			g2d.setColor(COLOUR_FLOOR);
			g2d.fillRect(0, CANVAS_HEIGHT / 2, CANVAS_WIDTH, CANVAS_HEIGHT / 2);

			// Draw Ceiling
			g2d.setPaint(COLOUR_CEILING);
			g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT / 2);

			ArrayList<Drawable> zBuffer = new ArrayList<Drawable>(CANVAS_WIDTH + MAX_ZOMBIES + 4);
			
			// get npc drawables and add them to drawables list
			for (int c = 0; c < npcList.size(); c++) {

				NPC temp = npcList.get(c);

				double angleToNPC = getAngle(temp.getX(), temp.getY(), x, y);
				double startAngle = playerAngle - FOV / 2;
				if (startAngle < 0) {
					startAngle = 2 * Math.PI + startAngle;
				}
				if (angleToNPC < Math.PI / 2 && startAngle > Math.PI / 2) {
					angleToNPC = angleToNPC + 2 * Math.PI;
				}
				double deltaAngle = angleToNPC - startAngle;
				if (deltaAngle <= 90) {
					int renderX = radiansToPixelsWidth(deltaAngle);
					double playerToNPC = getDistance(x, y, temp.getX(), temp.getY());
					int width = radiansToPixelsWidth(2 * Math.asin(temp.getRadius() / playerToNPC));
					zBuffer.add(temp.getSprite().getDrawable(renderX, CANVAS_HEIGHT / 2, width, playerToNPC));
				}

			}
			// drawables.add(new ArrayList<Drawable>(CANVAS_WIDTH * 2 + 1)); // add one more
			// drawable list for walls that
			// are not behind any npc
			// npcBuffer.sort(null);

			// get wall drawables
			double rayAngle = playerAngle - FOV / 2;
			double step = FOV / (CANVAS_WIDTH - 1);
			for (int i = 0; i < CANVAS_WIDTH; i++) { //for every pixel across the screen

				//determine deltaX and deltaY i.e. the slope of the ray (deltaX / deltaY)
				double deltaX = Math.sin(rayAngle);
				double deltaY = -1 * Math.cos(rayAngle);

				Corner wallHit = rayTrace(x, y, deltaX, deltaY); //get the hit coordinate of where the ray hits a wall
				double distance = getDistance(x, y, wallHit.getX(), wallHit.getY()); //get distance of hit

				//determine what column of the wall sprite to sample
				double sampleCol;
				//if hit along x axis sample y coordinate
				if (wallHit.getX() % 1 == 0 || (wallHit.getX() % 1 > 1 - SMALL_NUMBER - SMALL_NUMBER / 2
						&& wallHit.getX() % 1 < 1 - SMALL_NUMBER + SMALL_NUMBER / 2)) {
					sampleCol = wallHit.getY() % 1;
					//if hit along y axis sample x coordinate
				} else {
					sampleCol = wallHit.getX() % 1;
				}

				//determine the angle from the top to bottom of the wall and convert that to pixels
				int wallHeight = radiansToPixelsHeight(2 * Math.atan((WALL_HEIGHT / 2) / distance));

				//if the wall is a wallbuy then add the wallbuy drawable, else add the normal wall drawable to zBuffer
				Wallbuy wallbuy = map.hitWallbuy(wallHit);
				
				if (wallbuy != null) {
					zBuffer.add(wallbuy.getImg().getDrawableCol(sampleCol, i, CANVAS_HEIGHT / 2, wallHeight,
							distance));
				} else {
					zBuffer.add(wall.getDrawableCol(sampleCol, i, CANVAS_HEIGHT / 2, wallHeight, distance));
				}

				//increment rayAngle
				rayAngle += step;
			}

			//Draw drawables in zBuffer, drawing the greatest distance drawables first
			zBuffer.sort(null);
			for (int i = 0; i < zBuffer.size(); i++) {
				zBuffer.get(i).draw(g2d);
			}
			//reset the zBuffer and draw the ui
			UIDraw(g2d);
		}
	}

	private void UIDraw(Graphics2D g2d) {

		//Draw the current gun
		//the Gun class will handle what Sprite to draw
		currentGun.draw(g2d, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

		// Draw damage outline
		if (damagedThisTick) {
			g2d.setColor(DAMAGE_OUTLINE_COLOUR);
			g2d.fillRect(0, 0, CANVAS_WIDTH, DAMAGE_OUTLINE_WIDTH);
			g2d.fillRect(CANVAS_WIDTH - DAMAGE_OUTLINE_WIDTH, 0, DAMAGE_OUTLINE_WIDTH, CANVAS_HEIGHT);
			g2d.fillRect(0, CANVAS_HEIGHT - DAMAGE_OUTLINE_WIDTH, CANVAS_WIDTH, DAMAGE_OUTLINE_WIDTH);
			g2d.fillRect(0, 0, DAMAGE_OUTLINE_WIDTH, CANVAS_HEIGHT);
		}

		// Draw Score
		// System.out.println("Score: " + playerPoints);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 40));
		g2d.drawString("Points " + playerPoints, HEALTH_SYMBOL_PADDING * 2 + HEALTH_SYMBOL_LENGTH * 2 + SCORE_PADDING,
				CANVAS_HEIGHT - SCORE_PADDING);

		// Draw wave
		g2d.drawString("Wave " + wave, HEALTH_SYMBOL_PADDING * 2 + HEALTH_SYMBOL_LENGTH * 2 + WAVE_PADDING,
				CANVAS_HEIGHT - WAVE_Y_OFFSET);

		// Draw minimap
		if (MINI_MAP_ENABLED) {
			Corner[] enemyList = new Corner[zombieList.size()];
			for (int i = 0; i < zombieList.size(); i++) {
				enemyList[i] = new Corner(zombieList.get(i).getX(), zombieList.get(i).getY());
			}

			miniMap.setPlayerPos((int) x, (int) y);
			miniMap.setPlayerAngle(playerAngle);
			miniMap.setEnemies(enemyList);
			miniMap.draw(g2d);
		}

		// Draw Health Symbol Outline and background
		drawCross(g2d, HEALTH_SYMBOL_LENGTH, HEALTH_SYMBOL_WIDTH, HEALTH_SYMBOL_PADDING,
				CANVAS_HEIGHT - HEALTH_SYMBOL_PADDING, Color.WHITE);
		drawCross(g2d, HEALTH_SYMBOL_LENGTH - HEALTH_SYMBOL_OUTLINE, HEALTH_SYMBOL_WIDTH - HEALTH_SYMBOL_OUTLINE,
				HEALTH_SYMBOL_PADDING + HEALTH_SYMBOL_OUTLINE,
				CANVAS_HEIGHT - HEALTH_SYMBOL_PADDING - HEALTH_SYMBOL_OUTLINE, Color.BLACK);

		// Draw Health Cross
		drawCross(g2d, HEALTH_SYMBOL_LENGTH - HEALTH_SYMBOL_OUTLINE, HEALTH_SYMBOL_WIDTH - HEALTH_SYMBOL_OUTLINE,
				HEALTH_SYMBOL_PADDING + HEALTH_SYMBOL_OUTLINE,
				CANVAS_HEIGHT - HEALTH_SYMBOL_PADDING - HEALTH_SYMBOL_OUTLINE,
				(double) playerHealth / (double) playerMaxHealth, Color.RED);

		// Draw Ammo Counter
		for (int i = 1; i <= currentGun.getAmmoCapacity(); i++) {
			if (i > currentGun.getAmmoCount()) {
				g2d.setColor(new Color(0, 0, 0, AMMO_COUNT_OPACITY));
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.fillRect(CANVAS_WIDTH - i * (AMMO_COUNT_PADDING + AMMO_COUNT_WIDTH), AMMO_COUNT_PADDING,
					AMMO_COUNT_WIDTH, AMMO_COUNT_HEIGHT);
		}

		// Draw crosshairs
		g2d.setColor(CROSSHAIR_COLOUR);
		g2d.fillRect(CANVAS_WIDTH / 2 - CROSSHAIR_THICKNESS, CANVAS_HEIGHT / 2 - CROSSHAIR_LENGTH,
				CROSSHAIR_THICKNESS * 2, CROSSHAIR_LENGTH * 2);
		g2d.fillRect(CANVAS_WIDTH / 2 - CROSSHAIR_LENGTH, CANVAS_HEIGHT / 2 - CROSSHAIR_THICKNESS, CROSSHAIR_LENGTH * 2,
				CROSSHAIR_THICKNESS * 2);

		// Draw hitmarker
		if (hit) {
			g2d.setColor(HIT_MARKER_COLOUR);
			int[] x1Points = { CANVAS_WIDTH / 2 - HIT_MARKER_SIZE,
					CANVAS_WIDTH / 2 - HIT_MARKER_SIZE + HIT_MARKER_THICKNESS, CANVAS_WIDTH / 2 + HIT_MARKER_SIZE,
					CANVAS_WIDTH / 2 + HIT_MARKER_SIZE - HIT_MARKER_THICKNESS };
			int[] y1Points = { CANVAS_HEIGHT / 2 - HIT_MARKER_SIZE + HIT_MARKER_THICKNESS,
					CANVAS_HEIGHT / 2 - HIT_MARKER_SIZE, CANVAS_HEIGHT / 2 + HIT_MARKER_SIZE - HIT_MARKER_THICKNESS,
					CANVAS_HEIGHT / 2 + HIT_MARKER_SIZE };
			g2d.fillPolygon(x1Points, y1Points, 4);
			int[] x2Points = { CANVAS_WIDTH / 2 - HIT_MARKER_SIZE + HIT_MARKER_THICKNESS,
					CANVAS_WIDTH / 2 - HIT_MARKER_SIZE, CANVAS_WIDTH / 2 + HIT_MARKER_SIZE - HIT_MARKER_THICKNESS,
					CANVAS_WIDTH / 2 + HIT_MARKER_SIZE };
			int[] y2Points = { CANVAS_HEIGHT / 2 + HIT_MARKER_SIZE,
					CANVAS_HEIGHT / 2 + HIT_MARKER_SIZE - HIT_MARKER_THICKNESS, CANVAS_HEIGHT / 2 - HIT_MARKER_SIZE,
					CANVAS_HEIGHT / 2 - HIT_MARKER_SIZE + HIT_MARKER_THICKNESS };
			g2d.fillPolygon(x2Points, y2Points, 4);
			hmCurrentFrame++;
			if (hmCurrentFrame >= HIT_MARKER_FRAMES) {
				hit = false;
				hmCurrentFrame = 0;
			}
		}

		// Draw message
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
		g2d.drawString(message, MESSAGE_X_OFFSET, MESSAGE_Y_OFFSET);

		// Draw new round indicator
		g2d.setColor(newWaveColor);
		g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, NEW_WAVE_FONT_SIZE));
		g2d.drawString(wave + "", CANVAS_WIDTH / 2 - NEW_WAVE_X_OFFSET, CANVAS_HEIGHT / 2 + NEW_WAVE_Y_OFFSET);
	}

	// Process a key-pressed event. Update the current state.
	public void gameKeyPressed(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_E:
			interact = true;
			break;
		case KeyEvent.VK_R:
			reload = true;
			break;
		case KeyEvent.VK_SPACE:
			fire = true;
			break;
		case KeyEvent.VK_W:
			// ......
			moveAhead = 1;
			break;
		case KeyEvent.VK_S:
			// ......
			moveBack = 1;
			break;
		case KeyEvent.VK_A:
			// ......
			moveLeft = 1;
			break;
		case KeyEvent.VK_D:
			// ......
			moveRight = 1;
			break;
		case KeyEvent.VK_LEFT:
			// ......
			lookLeft = 1;
			break;
		case KeyEvent.VK_RIGHT:
			// ......
			lookRight = 1;
			break;
		}
	}

	public void gameKeyReleased(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_E:
			interact = false;
			break;
		case KeyEvent.VK_R:
			reload = false;
			break;
		case KeyEvent.VK_SPACE:
			fire = false;
			break;
		case KeyEvent.VK_W:
			// ......
			moveAhead = 0;
			break;
		case KeyEvent.VK_S:
			// ......
			moveBack = 0;
			break;
		case KeyEvent.VK_A:
			// ......
			moveLeft = 0;
			break;
		case KeyEvent.VK_D:
			// ......
			moveRight = 0;
			break;
		case KeyEvent.VK_LEFT:
			// ......
			lookLeft = 0;
			break;
		case KeyEvent.VK_RIGHT:
			// ......
			lookRight = 0;
			break;
		}
	}

	// Other methods
	// ......
	
	/**
	 * Sets the max health of the player
	 * @param h max health
	 */
	public void setMaxHealth(double h) {
		playerMaxHealth = h;
		playerHealth = playerMaxHealth;
	}
	
	/**
	 * Starts a grace period
	 */
	private void startGracePeriod() {
		music(roundStartSE);
		gracePeriod = true;
		gracePeriodStart = System.currentTimeMillis();
	}

	/**
	 * Starts a new wave, increase zombiesSpawned, maxZombies, zombie speed, decrease zombie spawn delay
	 */
	private void nextWave() {
		wave++;
		zombiesSpawned = 0;
		if (wave < 20) {
			zombiesThisWave += 2;
		} else if (wave < 30) {
			zombiesThisWave += 5;
		} else {
			zombiesThisWave += 10;
		}
		
		if (wave < 10) {
			maxZombies = 10;
		} else if (wave < 20) {
			maxZombies += 1;
		} else {
			maxZombies += 2;
		}
		
		if (maxZombies > MAX_ZOMBIES) {
			maxZombies = MAX_ZOMBIES;
		}
		
		enemySpawnDelay -= 500;
		if (enemySpawnDelay < SPAWN_DELAY_MIN) {
			enemySpawnDelay = SPAWN_DELAY_MIN;
		}

		zombieSpeed += 0.001;
		if (zombieSpeed > ZOMBIE_MAX_SPEED) {
			zombieSpeed = ZOMBIE_MAX_SPEED;
		}

		startGracePeriod();
	}

	/**
	 * Draws a cross
	 * @param g2d Graphics2D component
	 * @param length pixels length from center to arm end
	 * @param width pixels from center to one side width of arm
	 * @param x x coordinate of bottom left
	 * @param y y coordinate of bottom left
	 * @param c color
	 */
	private void drawCross(Graphics2D g2d, int length, int width, int x, int y, Color c) {
		g2d.setColor(c);

		g2d.fillRect(x, y - length - width, 2 * length, 2 * width);
		g2d.fillRect(x + length - width, y - 2 * length, 2 * width, 2 * length);
	}

	/**
	 * Draws a cross but only fillPercent of the height filled in
	 * @param g2d
	 * @param length
	 * @param width
	 * @param x
	 * @param y
	 * @param fillPercent
	 * @param c
	 */
	private void drawCross(Graphics2D g2d, int length, int width, int x, int y, double fillPercent, Color c) {
		g2d.setColor(c);

		g2d.fillRect(x + length - width, (int) ((y - length * 2 * fillPercent) + 0.5), 2 * width,
				(int) ((2 * length * fillPercent) + 0.5)); //draw the vertical rectangle , but only fillpercent of the way

		//determine if horizontal arm needs to be drawn at all, if it does determine how much of it to draw
		if (fillPercent >= 0.5 + ((double) width / (double) (2 * length))) {
			g2d.fillRect(x, y - length - width, 2 * length, 2 * width);
		} else if (fillPercent > 0.5 - ((double) width / (double) (2 * length))) {
			g2d.fillRect(x, y - (int) ((fillPercent * 2 * length) + 0.5), 2 * length,
					(int) ((((fillPercent - ((double) (length - width) / (double) (2 * length))) * length * 2) + 0.5)));
		}
	}

	/**
	 * returns the Gun object that has name
	 * @param name name of the gun to be returned
	 * @return the Gun object with name of name
	 */
	public Gun getGun(String name) {
		//loop through gunlist, if element's name is equal to name return that element
		for (int i = 0; i < gunList.length; i++) {
			if (gunList[i].getName() == name) {
				return gunList[i];
			}
		}
		return null;
	}

	/**
	 * Returns the Map object associated with GameMain
	 * @return game map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * returns player x-coordinate
	 * @return x coordinate
	 */
	public double getPlayerX() {
		return x;
	}

	/**
	 * returns player y-coordinate
	 * @return y-coordinate
	 */
	public double getPlayerY() {
		return y;
	}

	/**
	 * damages player
	 * @param damage how much to damage player by
	 */
	public void damagePlayer(double damage) {
		if (INVINCIBILITY == false) {
			playerHealth -= damage;
			if (playerHealth <= 0) {
				playerHealth = 0;
				state = GameState.GAMEOVER;
			}

			damagedThisTick = true;
		}
	}

	/**
	 * damages a Zombie object
	 * @param zombie the Zombie object to damage
	 * @param damage how much damage to deal
	 */
	private void damageZombie(Zombie zombie, double damage) {
		if (zombie.damage(damage)) { //if Zombie.damage() returns true then the zombie is dead
			npcList.remove(zombie);
			zombieList.remove(zombie);
			playerPoints += zombie.getPointsOnKill();
		}
	}

	/**
	 * Returns the zombie that gets hit by a ray starting at (x, y) and has an angle of rayAngle
	 * @param rayAngle angle of ray
	 * @param x start x
	 * @param y start y
	 * @return null if no zombie hit, Zombie object if a zombie was hit
	 */
	private Zombie hitZombie(double rayAngle, double x, double y) {
		double deltaX = RAY_INCREMENT * Math.sin(rayAngle);
		double deltaY = -1 * RAY_INCREMENT * Math.cos(rayAngle);
		double currX = x;
		double currY = y;
		while (true) {
			currX += deltaX;
			currY += deltaY;
			int cellX = (int) (currX);
			int cellY = (int) (currY);
			if (cellX < 0 || cellX >= map.getWidth() || cellY < 0 || cellY >= map.getHeight()) {
				return null;
			} else if (map.getTile(cellY, cellX) == Map.getWall()) {
				return null;
			} else {
				for (int c = 0; c < zombieList.size(); c++) {
					Zombie temp = zombieList.get(c);
					double distanceToNPC = getDistance(currX, currY, temp.getX(), temp.getY());
					if (distanceToNPC <= temp.getRadius()) {
						return temp;
					}
				}
			}
		}
	}

	/**
	 * adds a zombie to npcList and zombieList
	 * @param zombie Zombie object to add
	 */
	private void addZombie(Zombie zombie) {
		npcList.add(zombie);
		zombieList.add(zombie);
		zombiesSpawned++;
	}

	/**
	 * Returns the angle between two points
	 * @param x1 x coordinate of point one
	 * @param y1 y coordinate of point one
	 * @param x2 x coordinate of point two
	 * @param y2 y coordinate of point two
	 * @return angle (in radians)
	 */
	public static double getAngle(double x1, double y1, double x2, double y2) {
		double deltaX = x1 - x2;
		double deltaY = y2 - y1;
		// System.out.print(deltaX + ", " + deltaY + ", ");
		double rAngle = Math.atan(deltaX / deltaY);
		if (deltaX >= 0 && deltaY >= 0) {
			return rAngle;
		} else if (deltaX >= 0 && deltaY <= 0) {
			return Math.PI + rAngle;
		} else if (deltaX <= 0 && deltaY <= 0) {
			return Math.PI + rAngle;
		} else {
			return 2 * Math.PI + rAngle;
		}
	}

	/**
	 * Converts radians to pixels in width (if object takes up angle out of total FOV, then it should be drawn with width Width)
	 * @param angle from left to right
	 * @return pixel width
	 */
	private int radiansToPixelsWidth(double angle) {
		return (int) (angle * CANVAS_WIDTH / FOV);
	}

	/**
	 * converts radians to pixels in height (if object takes up angle out of total FOV, then it should be drawn with height Height)
	 * @param angle angle from top to bottom
	 * @return pixel height
	 */
	private int radiansToPixelsHeight(double angle) {
		return (int) (angle * CANVAS_HEIGHT / vFOV);
	}

	/**
	 * calculates the distance between two points
	 * @param x1 x coordinate of point one
	 * @param y1 y coordinate of point one
	 * @param x2 x coordinate of point two
	 * @param y2 y coordinate of point two
	 * @return distance between two points
	 */
	public static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	/**
	 * restrict the player's y position when they hit wall above
	 */
	private void hitBoundsAbove() {
		y = (int) y + playerRadius;
		hitXorY = true;
	}

	/**
	 * restrict the player's y position when they hit wall below
	 */
	private void hitBoundsBelow() {
		y = (int) (y + 1) - playerRadius;
		hitXorY = true;
	}

	/**
	 * restrict the player's x position when they hit wall left
	 */
	private void hitBoundsLeft() {
		x = (int) x + playerRadius;
		hitXorY = true;
	}

	/**
	 * restrict the player's x position when they hit wall left
	 */
	private void hitBoundsRight() {
		x = (int) (x + 1) - playerRadius;
		hitXorY = true;
	}

	/**
	 * restricts zombies y position when they hit wall above
	 * @param zombie zombie
	 */
	private void hitBoundsAbove(Zombie zombie) {
		zombie.setY((int) zombie.getY() + zombie.getRadius());
		zombie.setHitXorY(true);
	}

	/**
	 * restricts zombies y position when they hit wall below
	 * @param zombie zombie
	 */
	private void hitBoundsBelow(Zombie zombie) {
		zombie.setY((int) (zombie.getY() + 1) - zombie.getRadius());
		zombie.setHitXorY(true);
	}

	/**
	 * restricts zombies x position when they hit wall left
	 * @param zombie zombie
	 */
	private void hitBoundsLeft(Zombie zombie) {
		zombie.setX((int) zombie.getX() + zombie.getRadius());
		zombie.setHitXorY(true);
	}

	/**
	 * restricts zombies x position when they hit wall right
	 * @param zombie zombie
	 */
	private void hitBoundsRight(Zombie zombie) {
		zombie.setX((int) (zombie.getX() + 1) - zombie.getRadius());
		zombie.setHitXorY(true);
	}

	/**
	 * returns a grey color with all rbg channels set to value
	 * @param value value of all rgb channels
	 * @return color
	 */
	private Color grey(int value) {
		return new Color(value, value, value);
	}

	/**
	 * restricts an integer to a max and min
	 * @param i integer
	 * @param min minimum value
	 * @param max maximum value
	 * @return new integer value
	 */
	private int clamp(int i, int min, int max) {
		if (i < min) {
			i = min;
		} else if (i > max) {
			i = max;
		}
		return i;
	}

	/**
	 * sets the player's movement speed
	 * @param speed speed
	 */
	public void setPlayerSpeed(double speed) {
		moveSpeed = speed;
	}
	
	/**
	 * Multiplies the damage of all guns by multiplier
	 * @param multiplier damage multiplier
	 */
	public void multiplyDamage(int multiplier) {
		for (int i = 0; i < gunList.length; i ++) {
			gunList[i].multiplyDamage(multiplier);
		}
	}
	
	/**
	 * multiplies the reload time of all guns by multiplier
	 * @param multiplier reload multiplier
	 */
	public void multiplyReload(double multiplier) {
		for (int i = 0; i < gunList.length; i ++) {
			gunList[i].multiplyReload(multiplier);
		}
	}
	
	//Not my code, part of the framework
	// Custom drawing panel, written as an inner class.
	class GameCanvas extends JPanel implements KeyListener {
		// Constructor
		public GameCanvas() {
			setFocusable(true); // so that can receive key-events
			requestFocus();
			addKeyListener(this);
		}

		// Override paintComponent to do custom drawing.
		// Called back by repaint().
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d); // paint background
			setBackground(Color.BLACK); // may use an image for background
			// Draw the game objects
			gameDraw(g2d);
		}

		// KeyEvent handlers
		@Override
		public void keyPressed(KeyEvent e) {
			gameKeyPressed(e.getKeyCode());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			gameKeyReleased(e.getKeyCode());
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}

	//not my code
	/**
	 * Plays a sound file
	 * 
	 * @author Julia and Liina
	 * @param fileName
	 */
	public static void music(URL url) {
		
		sounds.add(new SoundPlayer(url));
		
		//try {
		//	playSoundEffects(url);
		//} catch (MalformedURLException ex) {
		//	Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		//} catch (LineUnavailableException ex) {
		//	Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		//} catch (UnsupportedAudioFileException ex) {
		//	Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		//} catch (IOException ex) {
		//	Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
		//}
	}

	//not my code
	/**
	 * Plays the sound effect
	 * 
	 * @author Julia and Liina
	 * @param fileName
	 * @throws MalformedURLException
	 * @throws LineUnavailableException
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	//public static void playSoundEffects(URL url)
			//throws MalformedURLException, LineUnavailableException, UnsupportedAudioFileException, IOException { // Written
																													// by
																													// Julia
																													// and
																													// Liina
																													// from
																													// "Brick
																													// Breaker"
																													// project
		// File url = new File(fileName);
		//Clip clip = AudioSystem.getClip();
//
		//AudioInputStream ais = AudioSystem.getAudioInputStream(url);
		//clip.open(ais);
		//clip.start();
	//}

}