import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image redghost, pinkghost, powderghost, orangeghost;
	private Image scaredghost, scaredghost2, scaredIcon;
    private Image pacman, pacup1, pacup2, pacup3;
    private Image pacdown1, pacdown2, pacdown3;
    private Image pacleft1, pacleft2, pacleft3;
    private Image pacright1, pacright2, pacright3;
    private final Color mazeCol = new Color (5, 5, 200); // blue
    private final Color dotCol = new Color(192, 192, 0); // yellow color
    private Dimension canvas;
    private Image im;
    
    private boolean ghostScatter = false;
    private boolean ghostScared = false;
    private boolean ghostRecovering = false;
    private boolean gameRunning = false;
    private boolean lostLife = false;
    
    private final int SPACE_PIXELS = 24;
    private final int ANIM_DELAY = 2;
    private final int ANIM_COUNT = 4;
    private final int SCREEN_PIXELS = 360;
    private final int MAX_SPEED = 6;
    
    
    private int animationCount = 2;
    private int pacAnimDir = 1;
    private int pacAnimPos = 0;
    
    
    private int[] gameField, enemyX, enemyY, enemyMoveX, enemyMoveY;
    private int[] validMoveX, validMoveY, enemySpeed;
    
    private int currSpeed = 2;
    private int score, level, livesLeft;
    private int pacX, pacY, pacMoveX, pacMoveY, viewX, viewY;
    private int moveX, moveY;
    private final int ghostSpeeds[] = {1, 2, 3, 4, 6};
    
    private Timer timer, scatter, scare, chase, recover, flashing;
    
    private final short levelDesign[] = {
            19, 26, 42, 26, 18, 18, 18, 34, 34, 18, 18, 18, 18, 18, 22,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
            25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
            1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
            1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
            1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
            9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
        };
	
	public Board() { // constructor
		loadSprites(); // import sprites
		initVars(); // initialize variables
		initBoard(); // initialize board
	}
	
	private void loadSprites () { // load pac and ghost sprites
		redghost = new ImageIcon("images/redghost.png").getImage();  // enemy images
        pinkghost = new ImageIcon("images/pinkghost.png").getImage();
        powderghost = new ImageIcon("images/powderghost.png").getImage();
        orangeghost = new ImageIcon("images/orangeghost.png").getImage();
        scaredghost = new ImageIcon("images/scaredghost.png").getImage();
        scaredghost2 = new ImageIcon("images/scaredghost2.png").getImage();
        pacman = new ImageIcon("images/pacman.png").getImage(); // full pacman
        pacup1 = new ImageIcon("images/pacmanup1.png").getImage(); // begin opening
        pacup2 = new ImageIcon("images/pacmanup2.png").getImage();
        pacup3 = new ImageIcon("images/pacmanup3.png").getImage(); // completely open
        pacdown1 = new ImageIcon("images/pacmandown1.png").getImage();
        pacdown2 = new ImageIcon("images/pacmandown2.png").getImage();
        pacdown3 = new ImageIcon("images/pacmandown3.png").getImage();
        pacleft1 = new ImageIcon("images/pacmanleft1.png").getImage();
        pacleft2 = new ImageIcon("images/pacmanleft2.png").getImage();
        pacleft3 = new ImageIcon("images/pacmanleft3.png").getImage();
        pacright1 = new ImageIcon("images/pacmanright1.png").getImage();
        pacright2 = new ImageIcon("images/pacmanright2.png").getImage();
        pacright3 = new ImageIcon("images/pacmanright3.png").getImage();
	}
	
	private void initVars() { // load values
		gameField = new int[225]; // 15 x 15 board
		canvas = new Dimension(400,400);
		enemyX = new int[4]; // x coord of ghosts
		enemyY = new int[4]; // y coord of ghosts
		enemyMoveX = new int[4]; // move x of ghosts
		enemyMoveY = new int[4]; // move y of ghosts
		enemySpeed = new int[4]; // speed of ghosts
		validMoveX = new int[4]; // check for valid move in x
		validMoveY = new int[4];
		timer = new Timer(40, this);
		scatter = new Timer(7000, new ActionListener() { // ghost scatter timer
            public void actionPerformed(ActionEvent e) {
                ghostScatter = false; // stop scattering
                scatter.stop();
                chase.start();
            }
        });
        chase = new Timer(21000, new ActionListener() { // ghost chase timer
            public void actionPerformed(ActionEvent e) {
                ghostScatter = true; // stop chasing and start scatttering
                chase.stop();
                scatter.start();
            }
        });
        recover = new Timer(5000, new ActionListener() { // ghost recovering timer
            public void actionPerformed(ActionEvent e) {
                ghostRecovering = true; // flag for recovering
                scaredIcon = scaredghost; // change sprite
                flashing.start();
                recover.stop();
            }
        });
        flashing = new Timer (250, new ActionListener() { // flashing timer
            public void actionPerformed(ActionEvent e) {
                if (scaredIcon == scaredghost) // flip between sprites
                    scaredIcon = scaredghost2;
                else
                    scaredIcon = scaredghost;
            }
        });
        scare = new Timer(10000, new ActionListener() { // ghost scared timer
            public void actionPerformed(ActionEvent e) {
                ghostScared = false; // stop being scared or scattering
                ghostScatter = false;
                ghostRecovering = false; // ghost fully recovered
                scare.stop();
                flashing.stop();
                chase.start();
                int rand = (int) (Math.random() * (currSpeed + 1));
                if (rand > currSpeed) {
                    rand = currSpeed; 
                }
                for (int i = 0; i < 4; i++) {
                    if (enemySpeed[i] == 0) {
                        enemyX[i] = 4 * SPACE_PIXELS; // respawn ghosts
                        enemyY[i] = 4 * SPACE_PIXELS;
                        enemySpeed[i] = ghostSpeeds[rand]; // set speed
                    }
                }
            }
        });
        timer.start();
	}
	
	private void initBoard() { // create board
		addKeyListener(new Controls()); // add key listener
		setFocusable(true);
		setBackground(Color.black); // black background
	}
	
	private void initGame() { // load values for new game
        livesLeft = 4;
        score = 0;
        level = 1;
        initLevel();
        currSpeed = 2;
    }
	
	private void initLevel() { // begin level
        int i;
        for (i = 0; i < 225; i++) {
            gameField[i] = levelDesign[i];
        }
        ghostRecovering = false;
        ghostScared = false;
        scatter.start(); // start level by scattering
        ghostScatter = true;
        contLevel();
    }
	
	private void contLevel() { // gameplay
		for (int i = 0; i < 4; i++) {
            enemyX[i] = 4 * SPACE_PIXELS; // spawn ghosts
            enemyY[i] = 4 * SPACE_PIXELS;
            int random = (int) (Math.random() * (currSpeed + 1));
            if (random > currSpeed) {
                random = currSpeed;
            }
            enemySpeed[i] = ghostSpeeds[random];
        }
		pacX = 7 * SPACE_PIXELS; // spawn pacman
        pacY = 11 * SPACE_PIXELS;
        pacMoveX = 0;
        pacMoveY = 0;
        moveX = 0;
        moveY = 0;
        viewX = -1;
        viewY = 0;
        lostLife = false;
	}
	
	private void gameplay(Graphics2D g) {
		if (lostLife) {
			loseLife(); // lost pacman
		}
		else {
			movePac();
			drawPac(g);
			moveGhosts(g);
			checkLevel();
		}
	}
	
	private void loseLife() {
        livesLeft--;
        if (livesLeft == 0) {
            gameRunning = false; // game over
        }
        contLevel(); // continue if able
    }

	private void movePac() {
        int position;
        short ch;
        if (moveX == -pacMoveX && moveY == -pacMoveY) {
            pacMoveX = moveX;
            pacMoveY = moveY;
            viewX = pacMoveX;
            viewY = pacMoveY;
        }
        if (pacX % 15 == 0 && pacY % 15 == 0) {
            position = pacX / SPACE_PIXELS + 15 * (int) (pacY / SPACE_PIXELS);
            ch = levelDesign[position];
            if ((ch & 16) != 0) {
                levelDesign[position] = (short) (ch & 15);
                score++;
            }
            if ((ch & 32) != 0) {
            	levelDesign[position] =  (short) (ch & 31);
            	score++;
            	scare.restart();
            	recover.restart();
            	scatter.stop();
            	chase.stop();
            	ghostScared = true;
            	ghostRecovering = false;
            }
            if (moveX != 0 || moveY != 0) {
                if (!((moveX == -1 && moveY == 0 && (ch & 1) != 0)
                        || (moveX == 1 && moveY == 0 && (ch & 4) != 0)
                        || (moveX == 0 && moveY == -1 && (ch & 2) != 0)
                        || (moveX == 0 && moveY == 1 && (ch & 8) != 0))) {
                    pacMoveX = moveX;
                    pacMoveY = moveY;
                    viewX = pacMoveX;
                    viewY = pacMoveY;
                    System.out.println(pacMoveX);
                }
            }
            
            if ((pacMoveX == -1 && pacMoveY == 0 && (ch & 1) != 0)
                    || (pacMoveX == 1 && pacMoveY == 0 && (ch & 4) != 0)
                    || (pacMoveX == 0 && pacMoveY == -1 && (ch & 2) != 0)
                    || (pacMoveX == 0 && pacMoveY == 1 && (ch & 8) != 0)) {
                pacMoveX = 0;
                pacMoveY = 0;
            }
        }
        pacX = pacX + 6 * pacMoveX;
        pacY = pacY + 6 * pacMoveY;
    }

	private void drawPac(Graphics2D g) { // check for movement direction
        if (viewX == -1) {
            drawPacLeft(g);
        } else if (viewX == 1) {
            drawPacRight(g);
        } else if (viewY == -1) {
            drawPacUp(g);
        } else {
            drawPacDown(g);
        }
    }
	
	private void drawPacUp(Graphics2D g) { // cycle through animations
        switch (pacAnimPos) {
            case 1:
                g.drawImage(pacup1, pacX + 1, pacY + 1, this);
                break;
            case 2:
            	g.drawImage(pacup2, pacX + 1, pacY + 1, this);
                break;
            case 3:
            	g.drawImage(pacup3, pacX + 1, pacY + 1, this);
                break;
            default:
            	g.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }
    private void drawPacDown(Graphics2D g) {
        switch (pacAnimPos) {
            case 1:
            	g.drawImage(pacdown1, pacX + 1, pacY + 1, this);
                break;
            case 2:
            	g.drawImage(pacdown2, pacX + 1, pacY + 1, this);
                break;
            case 3:
            	g.drawImage(pacdown3, pacX + 1, pacY + 1, this);
                break;
            default:
            	g.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }
    private void drawPacLeft(Graphics2D g) {
        switch (pacAnimPos) {
            case 1:
            	g.drawImage(pacleft1, pacX + 1, pacY + 1, this);
                break;
            case 2:
            	g.drawImage(pacleft2, pacX + 1, pacY + 1, this);
                break;
            case 3:
            	g.drawImage(pacleft3, pacX + 1, pacY + 1, this);
                break;
            default:
            	g.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }

    private void drawPacRight(Graphics2D g) {
        switch (pacAnimPos) {
            case 1:
            	g.drawImage(pacright1, pacX + 1, pacY + 1, this);
                break;
            case 2:
            	g.drawImage(pacright2, pacX + 1, pacY + 1, this);
                break;
            case 3:
            	g.drawImage(pacright3, pacX + 1, pacY + 1, this);
                break;
            default:
            	g.drawImage(pacman, pacX + 1, pacY + 1, this);
                break;
        }
    }
    
    private void moveGhosts(Graphics2D g2d) {
    	int i;
        int position;
        int count;
        for ( i = 0; i < 4; i++) {
            if (enemyX[i] % SPACE_PIXELS == 0 && enemyY[i] % SPACE_PIXELS == 0) {
                position = enemyX[i] / SPACE_PIXELS + 15 * (int) (enemyY[i] / SPACE_PIXELS);
                count = 0;
                if ((gameField[position] & 1) == 0 && enemyMoveX[i] != 1) { // check boundaries
                    validMoveX[count] = -1;
                    validMoveY[count] = 0;
                    count++;
                }
                if ((gameField[position] & 2) == 0 && enemyMoveY[i] != 1) {
                    validMoveX[count] = 0;
                    validMoveY[count] = -1;
                    count++;
                }
                if ((gameField[position] & 4) == 0 && enemyMoveX[i] != -1) {
                    validMoveX[count] = 1;
                    validMoveY[count] = 0;
                    count++;
                }
                if ((gameField[position] & 8) == 0 && enemyMoveY[i] != -1) {
                    validMoveX[count] = 0;
                    validMoveY[count] = 1;
                    count++;
                }
                if (count == 0) { // make sure ghosts don't get stuck
                    if ((gameField[position] & 15) == 15) {
                        enemyMoveX[i] = 0;
                        enemyMoveY[i] = 0;
                    } else {
                        enemyMoveX[i] = -enemyMoveX[i]; // reverse if stuck
                        enemyMoveY[i] = -enemyMoveY[i];
                    }

                } else {
                	switch(i) {
                	case 0: // red ghost - chase pacman
                		if (ghostScatter || ghostScared) {
                			if ((gameField[position] & 1) == 0 && enemyMoveX[i] != 1) {
                				enemyMoveX[i] = -1;
                				enemyMoveY[i] = 0;
                            }
                			else if ((gameField[position] & 2) == 0 && enemyMoveY[i] != 1) {
                				enemyMoveX[i] = 0;
                				enemyMoveY[i] = -1;
                    		}
                			else {
                				count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = validMoveX[count];
                                enemyMoveY[i] = validMoveY[count];
                			}
                		}
                		else if ((enemyX[i] - pacX) > 0 && ((gameField[position] & 1) == 0 && enemyMoveX[i] != 1)) {
                			enemyMoveX[i] = -1;
                			enemyMoveY[i] = 0;
                		}
                		else if ((enemyY[i] - pacY) > 0 && ((gameField[position] & 2) == 0 && enemyMoveY[i] != 1)) {
                			enemyMoveX[i] = 0;
                			enemyMoveY[i] = -1;
                		}
                		else if ((pacX - enemyX[i]) > 0 && ((gameField[position] & 4) == 0 && enemyMoveX[i] != -1)) {
                			enemyMoveX[i] = 1;
                			enemyMoveY[i] = 0;
                		}
                		else if ((pacY - enemyY[i]) > 0 && ((gameField[position] & 8) == 0 && enemyMoveY[i] != -1)) {
                			enemyMoveX[i] = 0;
                			enemyMoveY[i] = 1;
                		}
                		else {
                			count = (int) (Math.random() * count);
                            if (count > 3) {
                                count = 3;
                            }
                            enemyMoveX[i] = validMoveX[count];
                            enemyMoveY[i] = validMoveY[count];
                		}
                        break;
                	case 1: // pink ghost - move ahead of pacman
                		if (ghostScatter) {
                			if ((gameField[position] & 4) == 0 && enemyMoveX[i] != -1) {
                				enemyMoveX[i] = 1;
                				enemyMoveY[i] = 0;
                            }
                			else if ((gameField[position] & 2) == 0 && enemyMoveY[i] != 1) {
                				enemyMoveX[i] = 0;
                				enemyMoveY[i] = -1;
                    		}
                			else {
                				count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = validMoveX[count];
                                enemyMoveY[i] = validMoveY[count];
                			}
                		}
                		else if ((enemyX[i] - (pacX + 24 * pacMoveX)) > 0 && ((gameField[position] & 1) == 0 && enemyMoveX[i] != 1)) {
                			enemyMoveX[i] = -1;
                			enemyMoveY[i] = 0;
                		}
                		else if ((enemyY[i] - (pacY + 24 * pacMoveY)) > 0 && ((gameField[position] & 2) == 0 && enemyMoveY[i] != 1)) {
                			enemyMoveX[i] = 0;
                			enemyMoveY[i] = -1;
                		}
                		else if (((pacX + 24 * pacMoveX) - enemyX[i]) > 0 && ((gameField[position] & 4) == 0 && enemyMoveX[i] != -1)) {
                			enemyMoveX[i] = 1;
                			enemyMoveY[i] = 0;
                		}
                		else if (((pacY + 24 * pacMoveY) - enemyY[i]) > 0 && ((gameField[position] & 8) == 0 && enemyMoveY[i] != -1)) {
                			enemyMoveX[i] = 0;
                			enemyMoveY[i] = 1;
                		}
                		else {
                			count = (int) (Math.random() * count);
                            if (count > 3) {
                                count = 3;
                            }
                            enemyMoveX[i] = validMoveX[count];
                            enemyMoveY[i] = validMoveY[count];
                		}
                        break;
                	case 2: // powder ghost - pincer with red ghost
                		if (ghostScatter) {
                			if ((gameField[position] & 4) == 0 && enemyMoveX[i] != -1) {
                				enemyMoveX[i] = 1;
                				enemyMoveY[i] = 0;
                            }
                			else if ((gameField[position] & 8) == 0 && enemyMoveY[i] != -1) {
                				enemyMoveX[i] = 0;
                				enemyMoveY[i] = 1;
                    		}
                			else {
                				count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = validMoveX[count];
                                enemyMoveY[i] = validMoveY[count];
                			}
                		}
                		else if ((enemyY[i] - (pacX + 2*((pacX + 12 * pacMoveX) - enemyX[0]))) > 0 && ((gameField[position] & 1) == 0 && enemyMoveX[i] != 1)) {
                			enemyMoveX[i] = -1;
                			enemyMoveY[i] = 0;
                		}
                		else if ((enemyY[i] - (pacY + 2*((pacY + 12 * pacMoveY) - enemyY[0]))) > 0 && ((gameField[position] & 2) == 0 && enemyMoveY[i] != 1)) {
                			enemyMoveX[i] = 0;
                			enemyMoveY[i] = -1;
                		}
                		else if (((pacX + 2*((pacX + 12 * pacMoveX)  - enemyX[0])) - enemyX[i]) > 0 && ((gameField[position] & 4) == 0 && enemyMoveX[i] != -1)) {
                			enemyMoveX[i] = 1;
                			enemyMoveY[i] = 0;
                		}
                		else if (((pacY + 2*((pacY + 12 * pacMoveY)  - enemyY[0])) - enemyY[i]) > 0 && ((gameField[position] & 8) == 0 && enemyMoveY[i] != -1)) {
                			enemyMoveX[i] = 0;
                			enemyMoveY[i] = 1;
                		}
                		else {
                			count = (int) (Math.random() * count);
                            if (count > 3) {
                                count = 3;
                            }
                            enemyMoveX[i] = validMoveX[count];
                            enemyMoveY[i] = validMoveY[count];
                		}
                        break;
                	case 3: // orange ghost - chill in lower left corner
                		if ((Math.pow((enemyX[i] - pacX), 2) + Math.pow((enemyY[i] - pacY), 2)) > (64 * 225)) {
                			if ((enemyX[i] - pacX) > 0 && ((gameField[position] & 1) == 0 && enemyMoveX[i] != 1)) {
                				enemyMoveX[i] = -1;
                				enemyMoveY[i] = 0;
                    		}
                    		else if ((enemyY[i] - pacY) > 0 && ((gameField[position] & 2) == 0 && enemyMoveY[i] != 1)) {
                    			enemyMoveX[i] = 0;
                    			enemyMoveY[i] = -1;
                    		}
                    		else if ((pacX - enemyX[i]) > 0 && ((gameField[position] & 4) == 0 && enemyMoveX[i] != -1)) {
                    			enemyMoveX[i] = 1;
                    			enemyMoveY[i] = 0;
                    		}
                    		else if ((pacY - enemyY[i]) > 0 && ((gameField[position] & 8) == 0 && enemyMoveY[i] != -1)) {
                    			enemyMoveX[i] = 0;
                    			enemyMoveY[i] = 1;
                    		}
                    		else {
                    			count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = validMoveX[count];
                                enemyMoveY[i] = validMoveY[count];
                    		}
                		}
                		else {
                			if ((gameField[position] & 1) == 0 && enemyMoveX[i] != 1) {
                				enemyMoveX[i] = -1;
                				enemyMoveY[i] = 0;
                            }
                			else if ((gameField[position] & 8) == 0 && enemyMoveY[i] != -1) {
                				enemyMoveX[i] = 0;
                				enemyMoveY[i] = 1;
                    		}
                			else {
                				count = (int) (Math.random() * count);
                                if (count > 3) {
                                    count = 3;
                                }
                                enemyMoveX[i] = validMoveX[count];
                                enemyMoveY[i] = validMoveY[count];
                			}
                		}
                		break;
                	}
                    
                }

            }
            enemyX[i] = enemyX[i] + (enemyMoveX[i] * enemySpeed[i]);
            enemyY[i] = enemyY[i] + (enemyMoveY[i] * enemySpeed[i]);
            if (ghostRecovering)
            	g2d.drawImage(scaredIcon, enemyX[i] + 1, enemyY[i] + 1, this);
            else if (ghostScared) {
            		g2d.drawImage(scaredghost, enemyX[i] + 1, enemyY[i] + 1, this);
            }
            else {
            	switch(i) {
            	case 0:
            		g2d.drawImage(redghost, enemyX[i] + 1, enemyY[i] + 1, this);
            		break;
            	case 1:
            		g2d.drawImage(pinkghost, enemyX[i] + 1, enemyY[i] + 1, this);
            		break;
            	case 2:
            		g2d.drawImage(powderghost, enemyX[i] + 1, enemyY[i] + 1, this);
            		break;
            	case 3:
            		g2d.drawImage(orangeghost, enemyX[i] + 1, enemyY[i] + 1, this);
            		break;
            	}
            }
            if (ghostScared) { // check for collision - eat ghosts
            	if (pacX > (enemyX[i] - 10) && pacX < (enemyX[i] + 10)
                        && pacY > (enemyY[i] - 10) && pacY < (enemyY[i] + 10)
                        && gameRunning) {
                    score += 100;
                    enemyY[i] = 2 * SPACE_PIXELS; // move to ghost house and stop
                    enemyX[i] = 2 * SPACE_PIXELS;
                    enemySpeed[i] = 0;
                }
            } 
            else { // lose pacman
            	if (pacX > (enemyX[i] - 10) && pacX < (enemyX[i] + 10)
            			&& pacY > (enemyY[i] - 10) && pacY < (enemyY[i] + 10)
            			&& gameRunning) {
            		lostLife = true;
            	}
            }
        }
    }
    
    private void checkLevel() {
        int i = 0;
        boolean complete = true;
        while (i < 225 && complete) {
            if ((gameField[i] & 48) != 0) {
                complete = false; // set flag that still pellets to eat
            }
            i++;
        }
        if (complete) {
            score += 250;
            level += 1;
            if (currSpeed < MAX_SPEED) {
                currSpeed++;
            }
            initLevel();
        }
    }
    
    private void animatePac() {
        animationCount--;
        if (animationCount <= 0) {
            animationCount = ANIM_DELAY;
            pacAnimPos = pacAnimPos + pacAnimDir;

            if (pacAnimPos == (ANIM_COUNT - 1) || pacAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }
    
    private void drawIntro(Graphics2D g) {
        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, SCREEN_PIXELS / 2 - 30, SCREEN_PIXELS - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, SCREEN_PIXELS / 2 - 30, SCREEN_PIXELS - 100, 50);
        String message = "Press Enter to start.";
        Font small = new Font("Times New Roman", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (SCREEN_PIXELS - metr.stringWidth(message)) / 2, SCREEN_PIXELS / 2);
    }
    
    private void drawScore(Graphics2D g) {
        int i;
        String message;
        Font small = new Font("Times New Roman", Font.BOLD, 14);
        g.setFont(small);
        g.setColor(new Color(96, 128, 255));
        message = "Level: " + level + "        Score: " + score;
        g.drawString(message, SCREEN_PIXELS / 2, SCREEN_PIXELS + 16);
        for (i = 0; i < livesLeft; i++) {
            g.drawImage(pacleft3, i * 28 + 8, SCREEN_PIXELS + 1, this);
        }
    }
    
    private void drawLevel(Graphics2D g) {
        int i = 0;
        int x, y;
        for (y = 0; y < SCREEN_PIXELS; y += SPACE_PIXELS) {
            for (x = 0; x < SCREEN_PIXELS; x += SPACE_PIXELS) {
                g.setColor(mazeCol);
                g.setStroke(new BasicStroke(2));
                if ((gameField[i] & 1) != 0) { 
                    g.drawLine(x, y, x, y + SPACE_PIXELS - 1);
                }
                if ((gameField[i] & 2) != 0) { 
                    g.drawLine(x, y, x + SPACE_PIXELS - 1, y);
                }
                if ((gameField[i] & 4) != 0) { 
                    g.drawLine(x + SPACE_PIXELS - 1, y, x + SPACE_PIXELS - 1,
                            y + SPACE_PIXELS - 1);
                }
                if ((gameField[i] & 8) != 0) { 
                    g.drawLine(x, y + SPACE_PIXELS - 1, x + SPACE_PIXELS - 1,
                            y + SPACE_PIXELS - 1);
                }
                if ((gameField[i] & 16) != 0) { 
                    g.setColor(dotCol);
                    g.fillRect(x + 11, y + 11, 2, 2);
                }
                if ((gameField[i] & 32) != 0) { 
                    g.setColor(dotCol);
                    g.fillRect(x + 11, y + 11, 8, 8);
                }
                i++;
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; // initialize graphcis
        g2d.setColor(Color.black); // create background
        g2d.fillRect(0, 0, canvas.width, canvas.height);
        drawLevel(g2d); // draw level
        drawScore(g2d);
        animatePac();// draw pac
        if (gameRunning) {
            gameplay(g2d);
        } else {
            drawIntro(g2d);
        }
        g2d.drawImage(im, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }
    
	class Controls extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (gameRunning) {
                if (key == KeyEvent.VK_LEFT) {
                    moveX = -1;
                    moveY = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    moveX = 1;
                    moveY = 0;
                } else if (key == KeyEvent.VK_UP) {
                    moveX = 0;
                    moveY = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    moveX = 0;
                    moveY = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    gameRunning = false;
                } else if (key == KeyEvent.VK_P) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == KeyEvent.VK_ENTER) {
                    gameRunning = true;
                    initGame();
                }
            }
        }
        

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                moveX = 0;
                moveY = 0;
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }
}