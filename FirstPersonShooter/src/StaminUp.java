
public class StaminUp extends Perk{

	public StaminUp(int x, int y) {
		super(x, y, 600, "Stamin-Up", new Sprite("/StaminUp.png"), 0.27621283255);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void effect(GameMain game) {
		// TODO Auto-generated method stub
		game.setPlayerSpeed(0.07);
		setBought();
	}

}
