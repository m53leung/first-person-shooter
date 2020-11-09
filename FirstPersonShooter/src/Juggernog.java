
public class Juggernog extends Perk{

	public Juggernog(int x, int y) {
		super(x, y, 500, "Juggernog", new Sprite("/Juggernog.png"), 0.16666666666);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void effect(GameMain game) {
		// TODO Auto-generated method stub
		game.setMaxHealth(100);
		setBought();
	}
	
}
