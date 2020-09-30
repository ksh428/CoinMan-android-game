package com.ksh428.coinman1.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

import sun.rmi.runtime.Log;

public class CoinMan1 extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background; //texture is used to insert image
	Texture[] man;//contains the diff images of the man running
	int manState = 0;
	int pause = 0;//to regulte the speed of the manstate
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0; //y position of man
	Rectangle manRectangle;// rectangle is used to draw a shape(here to detect collission)// here for the man
	BitmapFont font;//to display score
	Texture dizzy;//collision man pic
	int score = 0;
	int gameState = 0;

	Random random;

	//store the x and y position of the coin
	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles =  new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;//to regulate the number of coins

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles =  new ArrayList<Rectangle>();//we could have done circle also
	Texture bomb;
	int bombCount;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");//sets the background
		man = new Texture[4];
		//set all the man images
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		//initial position of the man
		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		dizzy = new Texture("dizzy-1.png");

		font = new BitmapFont(); //for displaying score
		font.setColor(Color.WHITE);
		font.getData().setScale(10);//set size of the font
	}

	public void makeCoin() { //display the coin
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		// add the height and widths to the arrays
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());// starts from the top left (0,0) to fill the entire screen

		if (gameState == 1) {
			// GAME IS LIVE
			// BOMB
			if (bombCount < 250) { // display the bomb only after 250 loops
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangles.clear();//doubt
			for (int i=0;i < bombXs.size();i++) {
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));//draw the bombs
				bombXs.set(i, bombXs.get(i) - 8);// update the bomb x positions to make the bomb appear to move in the left
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			// COINS
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();//hide the coins whenever the man collides with it.
			for (int i=0;i < coinXs.size();i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 4);
				//draw the coon rectangle around the coin
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));// (xcoordinate,ycoordinate,width,height of coin)
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;//when the screen is touched make the man move up
			}

			if (pause < 8) { //repaeat the loop after 8 loops
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;//to make the downfall non uniform we use gravity.so that down speed increase by 0.2
			manY -= velocity; // decrease the mans y acc to the velocity

			if (manY <= 0) {
				manY = 0;// make it stick to the ground
			}
		} else if (gameState == 0) {
			// Waitng to start
			if (Gdx.input.justTouched()) { //start the game when touched
				gameState = 1;
			}
		} else if (gameState == 2) {
			// GAME OVER
			if (Gdx.input.justTouched()) {//start the game when touched after the game is over
				gameState = 1;
				//set initial prameters after restart
				//reset evrything
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				// and clear every paramater stored uptil now
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		if (gameState == 2) {// draw the dizzy image when the game is over
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		} else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
		// check for collisions
		//mans x pos never changes
		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());
		//loop through all the coin rectangles to check whether the man is colliding or not

		for (int i=0; i < coinRectangles.size();i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {//learn ...
				score++;// increase the score if the man collides with coin
				// use ...Gdx.app.log(","); //for logging
				//remove the coins at this position to stop overlapping
				coinRectangles.remove(i);//remove the overlapping coinrectangles and coinxs and coinys
				coinXs.remove(i);
				coinYs.remove(i);
				break;//leave the for loop once collided
			}
		}

		for (int i=0; i < bombRectangles.size();i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
				//Gdx.app.log("Bomb!", "Collision!");
				gameState = 2;
				break;
			}
		}

		//display the score
		font.draw(batch, String.valueOf(score),100,200);//draw the scores(batch,string,xpos,ypos)

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
