
/**
 * Perk has an x and y location, an associated image, and an appliable effect
 * @author matth
 *
 */
public abstract class Perk {
	
	int x;
	int y;
	int price;
	String name;
	boolean bought;
	NPC npc;
	
	public Perk (int x, int y, int price, String name, Sprite image, double radius) {
		this.x = x;
		this.y = y;
		this.price = price;
		this.name = name;
		bought = false;
		npc = new NPC(image, radius, x + 0.5, y + 0.5);
	}
	
	/**
	 * applies the effect
	 * @param game MainGame class
	 */
	public abstract void effect(GameMain game);

	public void setBought() {
		bought = true;
	}
	
	public boolean isBought() {
		return bought;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * returns the associated NPC
	 * @return
	 */
	public NPC getNPC() {
		return npc;
	}
	
}
