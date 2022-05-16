package i1SpaceInvaders;

import javax.swing.ImageIcon;

public class Mothership extends Sprite {

    private final String ship = "/res/mothership.png";
    
	public Mothership(int x, int y) {
		
		this.x = x;
		this.y = y;
		
		ImageIcon ii2 = new ImageIcon(this.getClass().getResource(ship));
		setImage(ii2.getImage());
		
	}    
	
	public void act(int direction) {
        this.x += direction;
    }
}
