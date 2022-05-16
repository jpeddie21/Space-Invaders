package i1SpaceInvaders;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Base extends Sprite implements Commons {
	
	private int lives;
	
	ImageIcon ii = new ImageIcon(this.getClass().getResource(BASE_10));
	ImageIcon ii9 = new ImageIcon(this.getClass().getResource(BASE_9));
	ImageIcon ii8 = new ImageIcon(this.getClass().getResource(BASE_8));
	ImageIcon ii7 = new ImageIcon(this.getClass().getResource(BASE_7));
	ImageIcon ii6 = new ImageIcon(this.getClass().getResource(BASE_6));
	ImageIcon ii5 = new ImageIcon(this.getClass().getResource(BASE_5));
	ImageIcon ii4 = new ImageIcon(this.getClass().getResource(BASE_4));
	ImageIcon ii3 = new ImageIcon(this.getClass().getResource(BASE_3));
	ImageIcon ii2 = new ImageIcon(this.getClass().getResource(BASE_2));
	ImageIcon ii1 = new ImageIcon(this.getClass().getResource(BASE_1));
	
	private Image base10;
	private Image base9;
	private Image base8;
	private Image base7;
	private Image base6;
	private Image base5;
	private Image base4;
	private Image base3;
	private Image base2;
	private Image base1;
	
	public Base(int x, int y) {
		
		base10 = ii.getImage();
		base9 = ii9.getImage();
		base8 = ii8.getImage();
		base7 = ii7.getImage();
		base6 = ii6.getImage();
		base5 = ii5.getImage();
		base4 = ii4.getImage();
		base3 = ii3.getImage();
		base2 = ii2.getImage();
		base1 = ii1.getImage();
        
        this.x = x;
        this.y = y;
        
        this.lives = 20;
        
	}
	
	public void setBaseLives(int lives) {
		
		this.lives = lives;
		
	}
	
	public int getBaseLives() {
		
		return lives;
		
	}
	
	public Image getImage() {
		
		if(lives != 20) {
			if(lives >= 18)
				return base1;
			else if(lives >= 16 && lives <= 17)
				return base2;
			else if(lives >= 14 && lives <= 15)
				return base3;
			else if(lives >= 12 && lives <= 13)
				return base4;
			else if(lives >= 10 && lives <= 11)
				return base5;
			else if(lives >= 8 && lives <= 9)
				return base6;
			else if(lives >= 6 && lives <= 7)
				return base7;
			else if(lives >= 4 && lives <= 5)
				return base8;
			else 
				return base9;
		}
		else
			return base10;
		
	}

}
