
/**
 * holds a gun name String thats associated with it and an image
 * @author matth
 *
 */
public class Wallbuy {
	String gun;
	Sprite img;
	
	public Wallbuy (String gunName, Sprite image) {
		gun = gunName;
		img = image;
	}
	
	public Sprite getImg() {
		return img;
	}
	
	public String getGun() {
		return gun;
	}
	
}
