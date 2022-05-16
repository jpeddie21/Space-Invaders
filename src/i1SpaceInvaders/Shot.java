package i1SpaceInvaders;

import javax.swing.ImageIcon;


public class Shot extends Sprite {

    private String shot = "/res/shot.png";
    private final int H_SPACE = 22;
    private final int V_SPACE = 2;
    private boolean dead = false;

    public Shot() {
    }

    public Shot(int x, int y) {

        ImageIcon ii = new ImageIcon(this.getClass().getResource(shot));
        setImage(ii.getImage());
        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }        
    
    public boolean getDead() {
    	return this.dead;
    }
    
    public void setDead(boolean value) {
    	this.dead = value;
    	setX(getX() - 6);
    }
}
