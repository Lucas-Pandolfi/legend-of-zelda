package com.blackcatstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.Camera;

public class Entity {
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected BufferedImage sprite;
	
	public static BufferedImage LIFEPACK_ENTITY = Game.spritesheet.getSprite(80, 0, 16, 16);
	public static BufferedImage WEAPON_ENTITY = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage BULLET_ENTITY = Game.spritesheet.getSprite(96, 16, 16, 16);
	public static BufferedImage ENEMY_ENTITY = Game.spritesheet.getSprite(80, 16, 16, 16);
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public int getX() {
		return (int)this.x;
	}
	
	public int getY() {
		return (int)this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {
		
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
