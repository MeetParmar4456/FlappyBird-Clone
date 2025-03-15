import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;
    
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = 64;
        int height = 512;
        Image img;
        boolean passed = false;

        Pipe(Image img) { // did this to differenciate topPipe and bottomPipe
            this.img = img;
        }
    }

    //pure logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed 
    int velocityY = 7; //move bird up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes; 
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        
        setFocusable(true);
        addKeyListener(this);

      
        backgroundImg = new ImageIcon(getClass().getResource("imgs/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("imgs/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("imgs/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("imgs/bottompipe.png")).getImage();

        
        bird = new Bird(birdImg); // This is just object of the class Bird
        pipes = new ArrayList<Pipe>(); // initialized arraylist in the constructor

        //timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // Code to be executed
              placePipes();
            }
        } ) ;
        placePipeTimer.start();
        
		//game timer
		gameLoop = new Timer(1000/60, this); //how long it takes to start timer, milliseconds gone between frames 
        gameLoop.start();
	}
    
    void placePipes() {
       
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = 160;
    
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe); // added a pipe to array list which's named "pipes"
    
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    
    
    public void paintComponent(Graphics g) { // repaint function will come here
		super.paintComponent(g); // This acts like clearing the canvas
		draw(g); // Call the draw function
	}

	public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width,bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) { 
            Pipe pipe = pipes.get(i); // to store pipes
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35); //now This won't throw an error
        }
        else {
            g.drawString( "" + (int) score, 10, 35); // Wrote this double quotes to make this an String , then drew a String
        }
        
	}
// Everything to print the score !

    public void move() {
        //bird
        velocityY += 1; // 1 is Gravity         Hum ise harbaar 1 se ghirate rahege
        bird.y += velocityY; // applies gravity to current bird.y
        bird.y = Math.max(bird.y, 0); // limit the bird.y to top of the canvas

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) { // If the bird passes the right side of the pipe, pipe.x+pipe.width gives us right side of pipe (as x position increases from left to right) 
                score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) { 
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9; 

            if (gameOver) {
                //restart game by resetting conditions 
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    // We don't use this but can't remove this ow errors
    @Override
    public void keyTyped(KeyEvent e) {
      
    }

    @Override
    public void keyReleased(KeyEvent e) {
       
    }


}
