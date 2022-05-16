package i1SpaceInvaders;

import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;


public class Player extends Sprite implements Commons{

    private final int START_Y = 560; 
    private final int START_X = 540;
    private boolean canMove = false;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean isInvincible;

    private final String player = "/res/player.png";
    private final String player_left = "/res/player_left.png";
    private final String player_right = "/res/player_right.png";
    private final String expl = "/res/explosion.png";
    private int width;

    ImageIcon ii = new ImageIcon(this.getClass().getResource(player));
    ImageIcon ii_right = new ImageIcon(this.getClass().getResource(player_right));
    ImageIcon ii_left = new ImageIcon(this.getClass().getResource(player_left));
    ImageIcon ii_exploded = new ImageIcon(this.getClass().getResource(expl));
    
    private Image image_left;
    private Image image_right;
    private Image image_exploded;
    private Image image;

    public Player() {

        width = ii.getImage().getWidth(null); 

        image_left = ii_left.getImage();
        image_right = ii_right.getImage();
        image_exploded = ii_exploded.getImage();
        image = ii.getImage();
        
        this.isInvincible = false;
        
        setX(START_X);
        setY(START_Y);
    }

    public void act() {
        x += dx;
        if (x <= -6) 
            x = -6;
        if (x >= (BOARD_WIDTH + 10) - 1.5*width) 
            x = (int) ((BOARD_WIDTH + 10) - 1.5*width);
    }
    
    public void setMove(boolean state) {
    	this.canMove = state;
    }
    
    public void setInvincible(boolean state) {
    	this.isInvincible = state;
    }
    
    public void resetPosition() {
    	setX(START_X);
    }
    
    public void resetImage() {
    	setImage(ii.getImage());
    }
    
    public Image getImage() {
    	if(!isInvincible)
	    	if(isMovingLeft)
	    		return image_left;
	    	else if(isMovingRight)
	    		return image_right;
	    	else
	    		return image;
    	else {
    		return image_exploded;
    	}
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && canMove)
        {
            dx = -4;
            isMovingLeft = true;
        }
        

        if (key == KeyEvent.VK_RIGHT && canMove)
        {
            dx = 4;
            isMovingRight = true;
        }

    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT)
        {
            dx = 0;
            isMovingLeft = false;
        }

        if (key == KeyEvent.VK_RIGHT)
        {
            dx = 0;
            isMovingRight = false;
        }
    }
}