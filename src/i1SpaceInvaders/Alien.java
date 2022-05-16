package i1SpaceInvaders;

import javax.swing.ImageIcon;


public class Alien extends Sprite {
	
	private boolean dead = false;
    private Bomb bomb;

    public Alien(int x, int y) {
        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);
    }

    public void act(int direction) {
        this.x += direction;
    }
    
    public void setDead(boolean value) {
    	this.dead = value;
    }
    
    public boolean getDead() {
    	return this.dead;
    }

    public Bomb getBomb() {
        return bomb;
    }

    public class Bomb extends Sprite {

        private final String bomb = "/res/bomb.png";
        private boolean destroyed;

        public Bomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            ImageIcon ii = new ImageIcon(this.getClass().getResource(bomb));
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }    
    }
}