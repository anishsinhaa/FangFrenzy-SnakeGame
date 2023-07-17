package com.Projects.SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int screenWidth=600;
    static final int screenHeight=600;
    static final int unitSize=25;
    static final int gameUnits=(screenWidth*screenHeight)/unitSize;
    static final int delay=75;
    final int []x=new int[gameUnits];
    final int []y=new int[gameUnits];
    int snakeBody=6;
    int foodEaten=0;
    int foodX,foodY;
    char direction='R';
    boolean running=false;
    boolean onMenu=true;
    boolean isGameOver=false;
    int highScore=0;
    Timer timer;
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyHandler());
        menu();
    }

    public void menu(){
        onMenu=true;
        isGameOver=false;
        running=false;
        //reset game values
        snakeBody = 6;
        foodEaten = 0;
        direction = 'R';
        x[0]=unitSize;
        y[0]=unitSize;
        //System.out.println("menu");
    }

    public void startGame(){
        onMenu=false;
        isGameOver=false;
        running=true;
        newFood();
        timer = new Timer(delay,this);
        timer.start();
        //System.out.println("start Game");
        repaint();
    }
    public void gameOver(){
        running=false;
        onMenu=false;
        isGameOver=true;
        if(foodEaten>highScore) highScore=foodEaten;
        //System.out.println("gameOver");
        repaint();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        //grid lines
//        for(int i=0;i<screenHeight/unitSize;i++){
//            g.drawLine(i*unitSize,0,i*unitSize,screenHeight);
//            g.drawLine(0,i*unitSize,screenWidth,i*unitSize);
//        }
        if(onMenu & !running & !isGameOver){
            //menu text
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free",Font.BOLD,40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Fang Frenzy - A Snake Game",(screenWidth - metrics.stringWidth("Fang Frenzy - A Snake Game"))/2, (int) (screenHeight/2.6));
            g.setFont(new Font("Courier",Font.CENTER_BASELINE,20));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("PRESS ANY KEY TO START",(screenWidth - metrics2.stringWidth("PRESS ANY KEY TO START"))/2, (int) (screenHeight/1.5));
        }
        if(running && !onMenu & !isGameOver) {
            //draw food
            g.setColor(Color.red);
            g.fillOval(foodX, foodY, unitSize, unitSize);

            //draw snake
            for (int i = 0; i < snakeBody; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], unitSize, unitSize);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], unitSize, unitSize);
                }
            }
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free",Font.BOLD,40));
            g.drawString(Integer.toString(foodEaten),unitSize,screenHeight-unitSize);
        }
        else if (isGameOver && !running && !onMenu) {
            //game over text
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free",Font.BOLD,75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("GAME OVER",(screenWidth - metrics.stringWidth("GAME OVER"))/2,screenHeight/2);
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free",Font.BOLD,40));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("Score: "+foodEaten,(screenWidth - metrics2.stringWidth("Score: "+foodEaten))/2,screenHeight/2/2);
            g.drawString("High Score: "+highScore,(screenWidth - metrics2.stringWidth("High Score: "+foodEaten))/2,screenHeight/2/3);
            g.setFont(new Font("Courier",Font.CENTER_BASELINE,20));
            FontMetrics metrics3 = getFontMetrics(g.getFont());
            g.drawString("PRESS ANY KEY TO TRY AGAIN",(screenWidth - metrics3.stringWidth("PRESS ANY KEY TO TRY AGAIN"))/2, (int) (screenHeight/1.5));
        }
    }
    public void newFood(){
        foodX=random.nextInt((screenWidth/unitSize))*unitSize;
        foodY=random.nextInt((screenHeight/unitSize))*unitSize;
    }
    public void move(){
        for(int i=snakeBody;i>0;i--){
            x[i]= x[i-1];
            y[i]=y[i-1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - unitSize;
            case 'D' -> y[0] = y[0] + unitSize;
            case 'L' -> x[0] = x[0] - unitSize;
            case 'R' -> x[0] = x[0] + unitSize;
        }
    }
    public void checkFood(){
        if((x[0])==foodX && y[0]==foodY){
            snakeBody++;
            foodEaten++;
            newFood();
        }
    }
    public void checkCollisions(){
        //checks if head collides with body;
        for(int i=snakeBody;i>0;i--){
            if((x[0]== x[i]) && y[0]==y[i]){
                gameOver();
            }

        }
        //check if head touches left or right border
        if(x[0]<0 || x[0]>screenWidth) {
            gameOver();
        }

        //check if head touches top or bottom border
        if(y[0]<0 || y[0]>screenHeight) {
            gameOver();
        }

        //stop timer
        if(!running) timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }
    public class KeyHandler extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            if(onMenu){
                startGame();
                repaint();
            }
            else if(running) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_A -> {
                        if (direction != 'R') direction ='L';
                    }
                    case KeyEvent.VK_D -> {
                        if (direction != 'L') direction ='R';
                    }
                    case KeyEvent.VK_W -> {
                        if (direction != 'D') direction ='U';
                    }
                    case KeyEvent.VK_S -> {
                        if (direction != 'U') direction ='D';
                    }
                }
            }
            else if(isGameOver){
                menu();
                repaint();
            }
            repaint();
        }
    }
}
