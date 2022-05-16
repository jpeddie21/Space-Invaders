package i1SpaceInvaders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sf.Sound;
import sf.SoundFactory;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class Board extends JPanel implements Runnable, Commons { 

    private Dimension d;
    private ArrayList aliens;
    private ArrayList bases;
    private Player player;
    private Shot shot;
    private Mothership mothership;
    private HighScores highScores;
    private ArrayList<Record> records;
    public ArrayList<String> names;
    public ArrayList<Integer> scores;

    private int alienX = 150;
    private int alienY = 5;
    private int direction = -1;
    private int deaths;
    private int lives;
    private int shotCount;
    private int hits;
    private int hitRatio;
    private int highScore = 99;
    private int mothershipMove = 1;
    private int arbitraryHit = 5;
    private int soundCount;
    private int bombCount;
    private int hitGroundCount;
    private int alienCount;

    private boolean ingame = false;
    private boolean invincible = false;
    private boolean mothershipSpawned;
    private boolean start = false;
    private boolean exploded = false;
    private boolean bombed = false;
    private boolean paused = false;

    private String message = "Game Over!";
    private String highScorePlayer = "AAA";
    
    private long invincibleStart;
    private long deadAlienStart;
    private long deadBombStart;

    private Thread animator;
    
    private Font small = new Font("Helvetica", Font.BOLD, 14);
    private FontMetrics metr = this.getFontMetrics(small);
    

    public Board() 
    {

        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
        setBackground(Color.black);        
        highScores = new HighScores(RECORD_LOCATION);
        names = new ArrayList<String>();
        scores = new ArrayList<Integer>();

        gameInit();
        setDoubleBuffered(true);
    }

    public void addNotify() {
        super.addNotify();
        gameInit();
    }

    public void gameInit() {
    	lives = 3;
    	deaths = 0;
    	shotCount = 0;
    	hits = 0;
    	hitRatio = 100;
    	soundCount = 0;
    	hitGroundCount = 0;
    	bombCount = 0;
    	alienCount = 0;
    	mothershipSpawned = false;
    	
        aliens = new ArrayList();
        bases = new ArrayList();
                
        message = "Game Over!";

        ImageIcon ii = new ImageIcon(this.getClass().getResource(ALIEN1));
        ImageIcon ii2 = new ImageIcon(this.getClass().getResource(ALIEN2));
        ImageIcon ii3 = new ImageIcon(this.getClass().getResource(ALIEN3));
        ImageIcon ii4 = new ImageIcon(this.getClass().getResource(ALIEN4));
        
        for (int i=0; i < 4; i++) {
            for (int j=0; j < 6; j++) {
                Alien alien = new Alien(alienX + 36*j, alienY + 36*i);
                alienCount++;
                if (alienCount <= 6)
                	alien.setImage(ii.getImage());
                else if (alienCount > 6 && alienCount <= 12)
                	alien.setImage(ii2.getImage());
                else if (alienCount > 12 && alienCount <= 18)
                	alien.setImage(ii3.getImage());
                else
                	alien.setImage(ii4.getImage());
                aliens.add(alien);
            }
        }
        
        int baseX = 129;
        int baseY = 480;
        for (int j = 0; j < 3; j++) {
        	Base base = new Base(baseX, baseY);
        	baseX += 179;
        	bases.add(base);
        }
        
        records = highScores.load();
        records.clear();
        
        player = new Player();
        player.setMove(true);
        shot = new Shot();   
        mothership = new Mothership(0 - MOTHERSHIP_WIDTH, (10 + MOTHERSHIP_HEIGHT));

	    if (animator == null || !ingame) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void drawAliens(Graphics g) 
    {
        Iterator it = aliens.iterator();

        while (it.hasNext()) {
            Alien alien = (Alien) it.next();

            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }

            if (alien.isDying()) {
                alien.die();
            }
        }
    }
    
    public void drawMothership(Graphics g) {
        Random rand = new Random();
        int randSpawn = rand.nextInt(1000);
        int randSide = rand.nextInt(2);
        
        if(randSide == 0 && !mothershipSpawned) {
        	mothership.setX(BOARD_WIDTH + MOTHERSHIP_WIDTH);
        	mothershipMove = -2;
        }
        else if (randSide == 1 && !mothershipSpawned) {
        	mothership.setX(MOTHERSHIP_WIDTH);
        	mothershipMove = 2;
        }
        if(randSpawn <= 5 && !mothershipSpawned) {
        	mothershipSpawned = true;
        }
        if (mothershipSpawned) {
            if (mothership.isVisible()) 
            	g.drawImage(mothership.getImage(), mothership.getX(), mothership.getY(), this);
            if (mothership.isDying()) {
            	mothership.die();
            }
        }
    }

    public void drawPlayer(Graphics g) {

        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            ingame = false;
        }
    }

    public void drawShot(Graphics g) {
    	
        if (shot.isVisible())
            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        
    }

    public void drawBombing(Graphics g) {

        Iterator i3 = aliens.iterator();
        
        while (i3.hasNext()) {
            Alien a = (Alien) i3.next();

            Alien.Bomb b = a.getBomb();

            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this); 
            }
        }
    }
    
    public void drawBase(Graphics g) {
    	
    	Iterator it = bases.iterator();
    	
    	while (it.hasNext()) {
    		Base base = (Base) it.next();
    		
    		if (base.isVisible()) {
    			g.drawImage(base.getImage(), base.getX(), base.getY(), this);
    		}
    		
    		if (base.isDying()) {
    			base.die();
    		}
    	}
    	
    }
    
    public void playSound(String sound) {
    	
		Sound soundClip = SoundFactory.getInstance(sound);
		SoundFactory.play(soundClip);
		
    }

    public void paint(Graphics g) {
      super.paint(g);

      g.setColor(Color.black);
      g.fillRect(0, 0, d.width, d.height);
      g.setColor(Color.green);   

      if (ingame && start) {
    	  String pause;
  	    g.setColor(Color.white);
  	    g.setFont(small);
        if (invincible) { 
      	    pause = "Respawning..."; 
        	g.drawString(pause, (BOARD_WIDTH - metr.stringWidth(pause))/2, 
	              BOARD_WIDTH/2);
        }
    	else if (paused) { 
    		pause = "Respawned! Press SPACE to continue!";
    		g.drawString(pause, (BOARD_WIDTH - metr.stringWidth(pause))/2, 
	              BOARD_WIDTH/2);
        }
        g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
        drawAliens(g);
        drawBase(g);
        drawPlayer(g);
        drawShot(g);
        drawBombing(g);
        updateFeedback(g);
        drawMothership(g);
      }
      else if (!ingame && !start)
    	  splashScreen(g);

      Toolkit.getDefaultToolkit().sync();
   	  g.dispose();

    }

    public void gameOver() {

        Graphics g = this.getGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH/2 - 30, BOARD_WIDTH-100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH/2 - 30, BOARD_WIDTH-100, 50);

        g.setColor(Color.white);
        g.setFont(small);
        
        message += " Press SPACE to restart or ESC to quit!";
        
        g.drawString(message, (BOARD_WIDTH - metr.stringWidth(message))/2, 
            BOARD_WIDTH/2);
    }    
    
    public void recordHighscore() {
    	/* 
    	 * Pull highScorePlayer and highScore from HighScores.java
    	 * - make getter and setter in Board.java that is called in HighScores
    	 * - getter and setter in record.java, just use those methods
    	 * Put in gameInit()
    	 * - only needs to be done on game load. 
    	 * Have saving new record and player name be in gameOver()
    	 */
    	highScorePlayer = JOptionPane.showInputDialog("Please enter your intials.");
    	names.add(highScorePlayer);
    	scores.add(deaths);
    	for (int i = 0; i < names.size(); i++) {
    		records.add(new Record(names.get(i), scores.get(i)));
    	}
    	highScores.save();
    	
    	if (records.size() > 1) {
	    	for (int i = 0; i < scores.size(); i++) 
	    	    for (int j = 0; j < scores.size(); j++)
	    	         if (scores.get(i) > scores.get(j)) {
	    	                highScore = scores.get(i);
	    	                highScorePlayer = names.get(i);
	    	         }
    	}
    	else {
    		highScorePlayer = names.get(0);
    		highScore = deaths;
    	}
    }
    
    public void updateFeedback(Graphics g) {
    	
    	if (shotCount != 0) {
    		hitRatio = hits * 100 /shotCount;
    	}
    	
    	String feedback = 
    			"Score: " + deaths + " Lives: " + lives + " Hit Ratio: " + hitRatio + "%"; 
    	
    	String highestScore = "Name: " + highScorePlayer + " Score: " + highScore;
        
    	g.setColor(Color.white);
    	g.setFont(small);
    	
    	g.drawString(feedback, 10, (GROUND + metr.getHeight() * 2));
    	g.drawString(highestScore, 10, (GROUND + metr.getHeight() * 3));
    	
    }
    
    public void splashScreen(Graphics g) {
    	
    	g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH/2-90, BOARD_WIDTH-100, 100);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH/2-90, BOARD_WIDTH-100, 100);

        Font large = new Font("Helvetica", Font.BOLD, 28);
        FontMetrics metr = this.getFontMetrics(large);

        g.setColor(Color.white);
        g.setFont(large);
    	  	
        message = "Space Invaders! Press SPACE to Start!";
        
        g.drawString(message, (BOARD_WIDTH - metr.stringWidth(message))/2, 
            BOARD_WIDTH/2 - 30);
    	
    }
    
    public void checkInvincible() {
    	
    	long now = System.currentTimeMillis();
    	long timeElapsed = now - invincibleStart;
    	if(invincible && timeElapsed > 5000) {
    		invincible = false;
    		player.setInvincible(false);
    		player.setMove(true);
    		player.resetPosition();
    		player.resetImage();
    	}
    	
    }

    public void animationCycle()  {

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            ingame = false;
            message = "Game won!";
        }

        // player

        player.act();

        // shot
        if (shot.isVisible()) {
            Iterator it = aliens.iterator();
            Iterator it2 = bases.iterator();
            int shotX = shot.getX();
            int shotY = shot.getY();

            while (it.hasNext()) {
                Alien alien = (Alien) it.next();
                int alienX = alien.getX();
                int alienY = alien.getY();
                int mothershipX = mothership.getX();
                int mothershipY = mothership.getY();

                if (alien.isVisible() && shot.isVisible()) {
                    if (shotX >= (alienX - arbitraryHit) && 
                        shotX <= (alienX + ALIEN_WIDTH + arbitraryHit) &&
                        shotY >= (alienY) &&
                        shotY <= (alienY + ALIEN_HEIGHT)) {
                            playSound(SOUND_INVADER_DEATH);
                            ImageIcon ii_death = 
                            		new ImageIcon(this.getClass().getResource(ALIEN_EXPLOSION));
                            alien.setImage(ii_death.getImage());
                            deaths++;
                            hits++;
                            shot.die();
                            alien.setDead(true);
                            deadAlienStart = System.currentTimeMillis();
                        }
                }
                
                if (mothership.isVisible() && shot.isVisible()) {
                    if (shotX >= (mothershipX - arbitraryHit) && 
                        shotX <= (mothershipX + MOTHERSHIP_WIDTH + arbitraryHit) &&
                        shotY >= (mothershipY) &&
                        shotY <= (mothershipY + MOTHERSHIP_HEIGHT)) {
                            mothership.setDying(true);
                            playSound(SOUND_INVADER_DEATH);
                            hits++;
                            shot.die();
                        }
                }
            }
            while (it2.hasNext()) {
            	Base base = (Base) it2.next();
            	int baseX = base.getX();
            	int baseY = base.getY();
            	if (base.isVisible() && shot.isVisible()) {
                    if (shotX >= (baseX - arbitraryHit) && 
                        shotX <= (baseX + BASE_WIDTH + arbitraryHit) &&
                        shotY >= (baseY) &&
                        shotY <= (baseY + BASE_HEIGHT)) {
                            shot.die();
                            base.setBaseLives(base.getBaseLives() - 1);
                            if (base.getBaseLives() == 0) {
                            	base.setDying(true);
                            }
                        }
                }
            }
            
            int y = shot.getY();
            y -= 6;
            if (y < 0)
                shot.die();
            else shot.setY(y);
        }

        // aliens

         Iterator it1 = aliens.iterator();

         while (it1.hasNext()) {
             Alien a1 = (Alien) it1.next();
             int x = a1.getX();

             if (x  >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                 direction = -1;
                 Iterator i1 = aliens.iterator();
                 while (i1.hasNext()) {
                     Alien a2 = (Alien) i1.next();
                     a2.setY(a2.getY() + GO_DOWN);
                 }
             }

            if (x <= BORDER_LEFT && direction != 1) {
                direction = 1;

                Iterator i2 = aliens.iterator();
                while (i2.hasNext()) {
                    Alien a = (Alien)i2.next();
                    a.setY(a.getY() + GO_DOWN);
                }
            }            	
            long now = System.currentTimeMillis();
        	long timeElapsed = now - deadAlienStart;
            if (a1.getDead() && timeElapsed > 100) {
                a1.setDying(true);
            }
        }


        Iterator it = aliens.iterator();
        
        while (it.hasNext()) {
            Alien alien = (Alien) it.next();
            if (alien.isVisible()) {

                int y = alien.getY();

                if (y > GROUND - ALIEN_HEIGHT) {
                    ingame = false;
                    message = "Invasion!";
                }

                alien.act(direction);
            }
        }
        soundCount++;
        if (soundCount == 75) {
        	soundCount = 0;
            playSound(SOUND_INVADER_MOVE);
        }
        
        // bombs

        Iterator i3 = aliens.iterator();
        Random generator = new Random();
        
        while (i3.hasNext()) {
            int shots = generator.nextInt(15);
            Alien a = (Alien) i3.next();
            Alien.Bomb b = a.getBomb();
            if (shots == CHANCE && a.isVisible() && b.isDestroyed()) {
                b.setDestroyed(false);
                b.setX(a.getX());
                b.setY(a.getY()); 
            }                

            int bombX = b.getX();
            int bombY = b.getY();
            int playerX = player.getX();
            int playerY = player.getY();
            int shotX = shot.getX();
            int shotY = shot.getY();

            if (player.isVisible() && !b.isDestroyed()) {
                if (bombX >= (playerX - arbitraryHit) && 
                    bombX <= (playerX + PLAYER_WIDTH + arbitraryHit) &&
                    bombY >= (playerY) && 
                    bombY <= (playerY + PLAYER_HEIGHT) ) {
                	
                    if(!invincible) {
                    	lives--;
                    	invincible = true;  
                    	paused = true;
                		player.setInvincible(true);
                		player.setMove(false); 
                        hitGroundCount++;
                        bombCount++;
                    	invincibleStart = System.currentTimeMillis();
                        playSound(SOUND_PLAYER_DEATH);
                    }
                    if (lives == 0) {
                    	player.setDying(true);
                    }
                    b.setDestroyed(true);
                }
            }
            
            if (shot.isVisible() && !b.isDestroyed()) {
            	if (bombX >= (shotX - arbitraryHit) && 
            			bombX <= (shotX + BOMB_WIDTH + arbitraryHit) && 
            			bombY >= (shotY) && 
            			bombY <= (shotY + BOMB_HEIGHT)) {
		            		ImageIcon ii_death = 
		                    	new ImageIcon(this.getClass().getResource(BOMB_EXPLOSION));
		                    shot.setImage(ii_death.getImage());
		                    shot.setDead(true);
		                    deadBombStart = System.currentTimeMillis();
		            		hitGroundCount++;
		                    bombCount++;
		            		playSound(SOUND_MISSILE_MISSILE);
		            		b.setDestroyed(true);
            		}
            	}
            
            Iterator base2 = bases.iterator();

        	while (base2.hasNext()) {
                Base base = (Base) base2.next();
            	int baseX = base.getX();
            	int baseY = base.getY();
	        	if (base.isVisible() && !b.isDestroyed()) {
	                if (bombX >= (baseX - arbitraryHit) && 
	                    bombX <= (baseX + BASE_WIDTH + arbitraryHit) &&
	                    bombY >= (baseY) &&
	                    bombY <= (baseY + BASE_HEIGHT)) {		            		
	                		hitGroundCount++;
	                		bombCount++;
	                        b.setDestroyed(true);
	                        base.setBaseLives(base.getBaseLives() - 1);
	                        if (base.getBaseLives() == 0) {
	                        	base.setDying(true);
	                        }
	                    }
	                }
        	}
            
            long now = System.currentTimeMillis();
        	long timeElapsed = now - deadBombStart;     
            if (shot.getDead() && timeElapsed > 50) {
                shot.die();
            }
            
            if (!b.isDestroyed()) {
                b.setY(b.getY() + 1);    
                if (b.getY() >= GROUND - BOMB_HEIGHT) {
                    b.setDestroyed(true);
                    hitGroundCount++;
                    bombCount++;
                    if (hitGroundCount ==  1) {
                		playSound(SOUND_PLAYER_DEATH);
                    	exploded = false;
                    }


                }
            }
            if(hitGroundCount == (24 - deaths) && !exploded) {
            		hitGroundCount = 0;
            		exploded = true;
            }
            
            // Plays sound on last bomb in wave. Not first.
            if(bombCount == 0 && !bombed) {
            	bombed = true;
            	playSound(SOUND_BOMBING);
            }
            if(bombed && bombCount == 24 - deaths) {
            	bombCount = 0;
            	bombed = false;
            }
            	
        } 	
        
        // mothership
        if (mothershipSpawned) {
        	int x = mothership.getX();
        	mothership.act(mothershipMove);
        	if (x >= BOARD_WIDTH - BORDER_RIGHT && mothershipMove != -2) {
               mothershipMove = -2;
            }
        	if (x <= BORDER_LEFT && mothershipMove != 2) {
               mothershipMove = 2;
        	}
        }
    }

    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();
                while (ingame && start) {
            repaint();
            if(!invincible && !paused)
            	animationCycle();
	        checkInvincible();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) 
                sleep = 2;
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            beforeTime = System.currentTimeMillis();   	
        }
        
        if (start && !ingame) {
        	gameOver();
        	recordHighscore();
        }
    }

    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        public void keyPressed(KeyEvent e) {
          int key = e.getKeyCode();	
          player.keyPressed(e);

          int x = player.getX();
          int y = player.getY();

          if (ingame)
          {
            if (e.isControlDown() && !invincible) {
            	shotCount++;
                if (!shot.isVisible()) {
                    shot = new Shot(x, y);
                    playSound(SOUND_SHOOT);
                }
            }
            
            if (key == KeyEvent.VK_ESCAPE) {
            	ingame = false;
            }
            if (paused)
            	if (key == KeyEvent.VK_SPACE)
            		paused = false;
          }          
          else if (!ingame) {
        	  if (key == KeyEvent.VK_SPACE) {
        		  ingame = true;
        		  animator = null;
        		  start = true;
        		  gameInit();
        	  }
        	  if (key == KeyEvent.VK_ESCAPE)
        		  System.exit(0);
          }
        }
      }
    }