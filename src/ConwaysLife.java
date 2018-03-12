

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.*;
import java.io.*;

public class ConwaysLife extends JFrame implements Runnable, MouseListener, MouseMotionListener {

	// member data
	private BufferStrategy strategy;
	private boolean gameState[][][] = new boolean[40][40][2];
	private boolean playing = false;
	//private int aliveCounter = 0;
	private int gameStateFrontBuffer = 0;
	
	// constructor
	public ConwaysLife () {
		
        //Display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width/2 - 400;
        int y = screensize.height/2 - 400;
        setBounds(x, y, 800, 800);
        setVisible(true);
    		this.setTitle("Conway's game of life");
    	
        // create and start our animation thread
        Thread t = new Thread(this);
        t.start();
		
        // initialise double-buffering
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        
        // register the Jframe itself to receive mouse events
        addMouseListener(this);
        addMouseMotionListener(this);
        
        // initialize the game state
        for(int m=0;m<40;m++){
        		for(int n=0;n<40;n++){
        			for(int z = 0; z<2; z++){
        			gameState[m][n][z] = false;          // initially sets all gamestates to false.
        			}
        		}
        }
	}
	
	// thread's entry point
	public void run() {
		while ( 1==1 )   {                  
			// 1: sleep for 1/5 sec
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) { }
			
			
		if(playing){
			doOneEpochOfGame();
			
			this.repaint();	
			}
		}
	}

	// mouse events which must be implemented for MouseListener
    public void mousePressed(MouseEvent e) {
	   	 // determine which cell of the gameState array was clicked on
	   	 int x = e.getX()/20;
	   	 int y = e.getY()/20;
	   	 
	   	 // code for START button
	   	 if((e.getX()>= 30 && e.getX()<= 90) && (e.getY()>=40 && e.getY()<=70)){   //coordinate bounds for start button
	   		 playing = true;                       
	   	 }
	   	 
	   	 // code for RANDOM button
	   	if((e.getX()>= 100 && e.getX()<= 160) && (e.getY()>=40 && e.getY()<=70)){
	   		random();
	   	}
	   	
	   	if((e.getX()>= 250 && e.getX()<=320) && (e.getY()>=40 && e.getY()<=70)){
	   		playing = false;
	   	}
   	// toggle the state of the cell provided the game is playing
	    gameState[x][y][gameStateFrontBuffer] = !gameState[x][y][gameStateFrontBuffer];
	    // throw an extra repaint, to get immediate visual feedback
	   	this.repaint();
     }

    public void mouseMoved(MouseEvent e){
    	
    }
    
    public void mouseDragged(MouseEvent e){
    	// determine which cell of the gameState array was clicked on
	   	int x = e.getX()/20;
	   	int y = e.getY()/20;
	   	gameState[x][y][gameStateFrontBuffer] = !gameState[x][y][gameStateFrontBuffer];
	    // throw an extra repaint, to get immediate visual feedback
	   	this.repaint();

    }
    
    
     public void mouseReleased(MouseEvent e) { }

     public void mouseEntered(MouseEvent e) { }

     public void mouseExited(MouseEvent e) { }

     public void mouseClicked(MouseEvent e) { }
// application's paint method
	public void paint(Graphics g) {
		g = strategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 800);                          // paints background black
	
		// redraw all  LIVE game objects
		g.setColor(Color.WHITE);
		for (int x=0;x<40;x++) {
			for (int y=0;y<40;y++) {
				if (gameState[x][y][gameStateFrontBuffer] == true) {
					g.fillRect(x*20, y*20, 20, 20);
				}
			}
		}
		
		
		 // game is not running..
       	 // draw a 'start button' as a rectangle with text on top
       	 // also draw a 'randomise' button
        g.setColor(Color.GREEN);
        g.fillRect(15, 40, 70, 30);
        g.fillRect(115, 40, 100, 30);
        g.fillRect(250, 40, 70, 30);
       	g.setFont(new Font("Times", Font.PLAIN, 24));
        g.setColor(Color.BLACK);
        g.drawString("Start", 22, 62);
        g.drawString("Random", 122, 62);
       	g.drawString("Stop",255,62);
		
		// flip the buffers
		g.dispose();
		strategy.show();
	}
	
	
	private void random() {
		for(int i = 0; i< 40; i++) {
			for(int j = 0; j<40; j++) {
				int random = (Math.random()<=0.5) ? 0 : 1;
				if (random ==0) gameState[i][j][gameStateFrontBuffer]= true;
				else gameState[i][j][gameStateFrontBuffer] = false;
			}
		}
	}
	
	private void doOneEpochOfGame() {
    	// apply game rules to game state ‘front buffer’, copying results into ‘back buffer’
    	int front = gameStateFrontBuffer;
    	int back = (front+1)%2;
        for (int x=0;x<40;x++) {
        	for (int y=0;y<40;y++) {
        		// count the neighbours of cell x,y
        		int liveneighbours=0;
        		for (int xx=-1;xx<=1;xx++) {
        			for (int yy=-1;yy<=1;yy++) {
        				if (xx!=0 || yy!=0) {
        					int xxx=x+xx;
        					if (xxx<0)
        						xxx=39;
        					else if (xxx>39)
        						xxx=0;
        					int yyy=y+yy;
        					if (yyy<0)
        						yyy=39;
        					else if (yyy>39)
        						yyy=0;           					
        					if (gameState[xxx][yyy][front])
        						liveneighbours++;
        				}
        			}
        		}

        		// apply rules
        		if (gameState[x][y][front]) {
        			// cell x,y was alive
           // #1. Any live cell with fewer than two live neighbours dies
        			if (liveneighbours<2)
        				gameState[x][y][back] = false;
           // #2. Any live cell with two or three live neighbours lives
        			else if (liveneighbours<4)
        				gameState[x][y][back] = true;
           // #3. Any live cell with more than three live neighbours dies   
        			else
        				gameState[x][y][back] = false;
        		}
        		else {
        			// cell x,y was dead
        			// #4. Dead cells with three live neighbours become live
        			if (liveneighbours==3)
        				gameState[x][y][back] = true;
        			else
        				gameState[x][y][back] = false;
        		}
        	}
        }
    	
    	// now flip the game state buffers
    	gameStateFrontBuffer = back;		
	}
	
	// application entry point
	public static void main(String[] args) {
		ConwaysLife w = new ConwaysLife();
	}

}
