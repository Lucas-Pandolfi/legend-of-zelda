package com.blackcatstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.World;

public class Player extends Entity {

	public boolean right, left, up, down;
	
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = right_dir;
	public double speed = 1.2;
	public static double life = 100, maxLife = 100;
	
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
		if(right && World.collidedWithWallTile((int)(x + speed), (int)y, width, height)) {
	        moved = true;
	        dir = right_dir;
	        x += speed;
	    }
	    else if(left && World.collidedWithWallTile((int)(x - speed), (int)y, width, height)) {
	        moved = true;
	        dir = left_dir;
	        x -= speed;
	    }
	    
	    if(up && World.collidedWithWallTile((int)x, (int)(y - speed), width, height)) {
	        moved = true;
	        y -= speed;
	    }
	    else if(down && World.collidedWithWallTile((int)x, (int)(y + speed), width, height)) {
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
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	public void render(Graphics graphics) {
		if(dir == right_dir) {
			graphics.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);			
		}
		else if(dir == left_dir) {
			graphics.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
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
