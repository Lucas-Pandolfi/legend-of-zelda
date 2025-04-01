package com.blackcatstudios.entities;

import java.awt.image.BufferedImage;

public class Player extends Entity {

	public boolean right, left, up, down;
	public double speed = 1.2;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}

	public void tick() {
		if(right)
			x += speed;
		else if(left)
			x -= speed;
		else if(up)
			y -= speed;
		else if(down)
			y += speed;
	}
}
