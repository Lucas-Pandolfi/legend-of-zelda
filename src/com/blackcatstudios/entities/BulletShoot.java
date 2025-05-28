package com.blackcatstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.Camera;

public class BulletShoot extends Entity {

	private double directionX;
	private double directionY;
	private double speed = 4;
	private int bulletLife = 30;
	private int currentBulletLife = 0;


	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		
		this.directionX = dx;
		this.directionY = dy;
	}
	
	public void tick() {
		x += directionX * speed;
		y += directionY * speed;
		
		if (Game.world.isSolidTile(this.getX(), this.getY())) {
	        Game.bulletShoots.remove(this);
	        return;
	    }

	    for(int i = 0; i < Game.enemiesOnMap.size(); i++) {
	        Enemy enemy = Game.enemiesOnMap.get(i);
	        if(Entity.isColidding(this, enemy)) {
	            enemy.takeDamage();
	            Game.bulletShoots.remove(this);
	            return;
	        }
	    }

	    currentBulletLife++;
	    if(currentBulletLife >= bulletLife) {
	        Game.bulletShoots.remove(this);
	        return;
	    }
	}
	
	public void render(Graphics graphics) {
		graphics.setColor(Color.YELLOW);
		graphics.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
}
