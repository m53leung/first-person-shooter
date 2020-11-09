import java.awt.Graphics2D;
import java.net.URL;

/**
 * A Gun represents a gun that the player can shoot and reload.
 * @author matth
 *
 */
public class Gun {
	
	String name;
	long fireDelay; //how long Gun must wait before firing again
	long lastFire; //time of last fire
	long reloadTime;
	int magCapacity; //total capacity of mag
	int magCount; //number of bullets in the magazine
	double damage;
	
	int cost; //how much it costs to buy from a wall buy
	
	boolean firing; //if gun is firing
	boolean reloading; //if gun is reloading
	boolean firstFire; //if current frame is first frame of firing animation
	boolean firstReload; //if current frame of reload is the first frame
	long animStart; //start time of the current animation
	
	URL fireSoundEffect;
	URL reloadSoundEffect;
	
	Sprite texture; //idle texture of gun
	Sprite currentSprite; //current sprite to be drawn of the gun
	Sprite[] fireAnim; //sprites of the firing animation
	Sprite[] reloadAnim; //sprites of the reloading animation
	
	//GameMain game; //reference to the gamemain class
	
	public Gun(String name, int fireDelay, int reloadTime, int magCapacity, double damage, int cost, Sprite texture, Sprite[] fireAnim,
			Sprite[] reloadAnim, String firePathname, String reloadPathname) {
		//this.game = game;
		
		this.name = name;
		this.fireDelay = fireDelay;
		this.reloadTime = reloadTime;
		this.magCapacity = magCapacity;
		magCount = magCapacity;
		this.damage = damage;
		this.cost = cost;
		
		firing = false;
		reloading = false;
		firstFire = false;
		firstReload = false;
		
		this.texture = texture;
		currentSprite = texture;
		this.fireAnim = fireAnim;
		this.reloadAnim = reloadAnim;
		
		fireSoundEffect = Gun.class.getResource(firePathname);
		reloadSoundEffect = Gun.class.getResource(reloadPathname);
	}

	/*
	public void act() {
		if (firstFire) {
			animStart = System.currentTimeMillis();
			firing = true;
			firstFire = false;
		}
		if (firing) {
			long deltaStart = System.currentTimeMillis() - animStart;
			if (deltaStart >= reloadTime) {
				firing = false;
				currentSprite = texture;
			} else {
				currentSprite = fireAnim[(int)((double)deltaStart / ((double)fireDelay / (double)fireAnim.length))];
			}
		}
	}
	*/
	
	public URL getFireSoundUrl() {
		return fireSoundEffect;
	}
	
	public URL getReloadSoundUrl() {
		return reloadSoundEffect;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getAmmoCount() {
		return magCount;
	}
	
	public int getAmmoCapacity() {
		return magCapacity;
	}
	
	public double getDamage() {
		return damage;
	}
	
	/**
	 * Multiplies this guns damage by multiplier
	 * @param multiplier damage multiplier
	 */
	public void multiplyDamage(int multiplier) {
		damage = damage * multiplier;
	}
	
	/**
	 * multiplies the reload of this gun by multiplier
	 * @param multiplier reload multiplier
	 */
	public void multiplyReload(double multiplier) {
		reloadTime = (long) (reloadTime * multiplier);
	}
	
	/**
	 * Draws the current sprite
	 * @param g2d Graphics2D component
	 * @param dx1 destination 1 x
	 * @param dy1 destination 1 y
	 * @param dx2 destination 2 x
	 * @param dy2 destination 2 y
	 */
	public void draw(Graphics2D g2d, int dx1, int dy1, int dx2, int dy2) {
		if (firstFire) {
			animStart = System.currentTimeMillis();
			firing = true;
			firstFire = false;
		}
		if (firing) {
			long deltaStart = System.currentTimeMillis() - animStart;
			if (deltaStart >= fireDelay) {
				firing = false;
				currentSprite = texture;
			} else {
				currentSprite = fireAnim[(int)((double)deltaStart / ((double)fireDelay / (double)fireAnim.length))];
			}
		}
		
		if (firstReload) {
			animStart = System.currentTimeMillis();
			reloading = true;
			firstReload = false;
		}
		if (reloading) {
			long deltaStart = System.currentTimeMillis() - animStart;
			if (deltaStart >= reloadTime) {
				reloading = false;
				currentSprite = texture;
				magCount = magCapacity;
			} else {
				currentSprite = reloadAnim[(int)((double)deltaStart / ((double)reloadTime / (double)reloadAnim.length))];
			}
		}
		g2d.drawImage(currentSprite.getImage(), dx1, dy1, dx2, dy2, 0, 0, currentSprite.getImage().getWidth(null), currentSprite.getImage().getHeight(null), null);
	}
	
	/**
	 * Attempts to fire the gun
	 * @return whether the gun was fired succesfully
	 */
	public boolean fire() {
		if (System.currentTimeMillis() - lastFire >= fireDelay && reloading == false) {
			if (magCount > 0) {
			GameMain.music(fireSoundEffect);
				firstFire = true;
				magCount --;
				lastFire = System.currentTimeMillis();
				return true;
			} else {
				reload();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Attempts to reload the gun
	 */
	public void reload() {
		if (reloading == false && magCount < magCapacity) {
			GameMain.music(reloadSoundEffect);
			firstReload = true;
			reloading = true;
		}
	}
}
