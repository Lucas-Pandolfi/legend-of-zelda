package com.blackcatstudios.entities;

import java.awt.image.BufferedImage;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.World;

public class Enemy extends Entity {

	private double speed = 0.6;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}
	
	public void tick() {
		if((int)x < Game.player.getX() && World.collidedWithWallTile((int)(x + speed), (int)y, width, height))
	        x += speed;
	    else if((int)x > Game.player.getX() && World.collidedWithWallTile((int)(x - speed), (int)y, width, height))
	        x -= speed;
	    
	    if((int)y < Game.player.getY() && World.collidedWithWallTile((int)x, (int)(y + speed), width, height))
	        y += speed;
	    else if((int)y > Game.player.getY() && World.collidedWithWallTile((int)x, (int)(y - speed), width, height))
	        y -= speed;
	}
}
