
public class SpeedCola extends Perk {
	public SpeedCola(int x, int y) {
		super(x, y, 700, "Speed Cola", new Sprite("/SpeedCola.png"), 0.21829268292);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void effect(GameMain game) {
		// TODO Auto-generated method stub
		game.multiplyReload(0.5);
		setBought();
	}
}
