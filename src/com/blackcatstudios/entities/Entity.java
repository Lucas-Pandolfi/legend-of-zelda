package com.blackcatstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.Camera;

public class Entity {
	private int maskX, maskY, mWidth, mHeight;
	
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected BufferedImage sprite;
	
	public static BufferedImage LIFEPACK_ENTITY = Game.spritesheet.getSprite(80, 0, 16, 16);
	public static BufferedImage WEAPON_ENTITY = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage AMMO_ENTITY = Game.spritesheet.getSprite(128, 0, 16, 16);
	public static BufferedImage ENEMY_ENTITY = Game.spritesheet.getSprite(80, 16, 16, 16);
	public static BufferedImage ENEMY_ENTITY_FEEDBACK = Game.spritesheet.getSprite(112, 16, 16, 16);
	public static BufferedImage WEAPON_RIGHT = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage WEAPON_LEFT = Game.spritesheet.getSprite(112, 0, 16, 16);
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskX = 0;
		this.maskY = 0;
		this.mWidth = width;
		this.mHeight = height;
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
	
	public void setMask(int maskX, int maskY, int mWidth, int mHeight) {
		this.maskX = maskX;
		this.maskY = maskY;
		this.mWidth = mWidth;
		this.mHeight = mHeight;
	}
	
	public void tick() {
		
	}
	
	public static boolean isColidding(Entity entity1, Entity entity2) {
		Rectangle entityMask1 = new Rectangle(entity1.getX() + entity1.maskX, entity1.getY() + entity1.maskY, entity1.mWidth, entity1.mHeight);
		Rectangle entityMask2 = new Rectangle(entity2.getX() + entity2.maskX, entity2.getY() + entity2.maskY, entity2.mWidth, entity2.mHeight);
		
		if(entityMask1.intersects(entityMask2))
			System.out.println("colidiu");
			
		return entityMask1.intersects(entityMask2);
	}
	
	public void render(Graphics graphics) {
		graphics.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//Usado para visualizar a colisão das entidade
		//graphics.setColor(Color.red);
		//graphics.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, mWidth, mHeight);
	}
}
