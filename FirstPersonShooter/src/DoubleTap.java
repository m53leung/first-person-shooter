
public class DoubleTap extends Perk {

	public DoubleTap(int x, int y) {
		super(x, y, 900, "Double Tap", new Sprite("/DoubleTap.png"), 0.295);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void effect(GameMain game) {
		// TODO Auto-generated method stub
		game.multiplyDamage(2);
		setBought();
	}
	
}
