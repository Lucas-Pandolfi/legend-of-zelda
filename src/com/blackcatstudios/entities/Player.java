package com.blackcatstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.blackcatstudios.main.Game;

public class Player extends Entity {

	public boolean right, left, up, down;
	
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = right_dir;
	
	public double speed = 1.2;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer = new BufferedImage[3]; 
	private BufferedImage[] leftPlayer= new BufferedImage[3];
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		getSprites();
	}

	public void tick() {
		moved = false;
		if(right) {
			moved = true;
			dir = right_dir;
			x += speed;
		}
		if(left) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		if(up) {
			moved = true;
			y -= speed;
		}
		if(down) {
			moved = true;
			y += speed;
		}
		
		if(moved) {
			frames++;
			
			if(frames == maxFrames) {
				frames = 0;
				index++;
				
				if(index >= maxIndex)
					index = 0;
			}
		}
	}
	
	public void render(Graphics graphics) {
		if(dir == right_dir) {
			graphics.drawImage(rightPlayer[index], this.getX(), this.getY(), null);			
		}
		else if(dir == left_dir) {
			graphics.drawImage(leftPlayer[index], this.getX(), this.getY(), null);	
		}
	}
	
	private void getSprites() {
		for(int i = 0; i < rightPlayer.length; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, width, height);			
		}
		
		for(int i = 0; i < leftPlayer.length; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, width, height);			
		}
	}
}
