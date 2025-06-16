package com.blackcatstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.Node;
import com.blackcatstudios.world.Vector2i;

public class Entity {	
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	protected BufferedImage sprite;
	protected List<Node> paths;
	
	public int depth;
	public int maskX, maskY, mWidth, mHeight;
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
	
	public static Comparator<Entity> entitySorter = new Comparator<Entity>() {
		@Override
		public int compare(Entity e0, Entity e1) {
			if(e1.depth < e0.depth)
				return +1;
			
			if(e1.depth > e0.depth)
				return -1;
			
			return 0;
		}
	};
	
	public double calculateDistace(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)* (y1 - y2));
	}
	
	public void followPath(List<Node> paths) {
		if(paths != null) 
		{
			if(paths.size() > 0) 
			{
				Vector2i target = paths.get(paths.size() - 1).tile;
				
				if(x < target.x * 16 && !enemyCollidingAnotherEnemy(this.getX() + 1, this.getY()))
					x++;
				else if(x > target.x * 16 && !enemyCollidingAnotherEnemy(this.getX() - 1, this.getY()))
					x--;
				
				if(y < target.y * 16 && !enemyCollidingAnotherEnemy(this.getX(), this.getY() + 1))
					y++;
				else if(y > target.y * 16 && !enemyCollidingAnotherEnemy(this.getX(), this.getY() - 1))
					y--;
				
				if(x == target.x * 16 && y == target.y * 16)
					paths.remove(paths.size() - 1);
			}
		}
	}
	
	private boolean enemyCollidingAnotherEnemy(int xNext, int yNext) {
		Rectangle currentEnemy = new Rectangle(xNext + maskX, yNext + maskY, mWidth, mHeight);
		
		for(int i = 0; i < Game.enemiesOnMap.size(); i++) 
		{
			Enemy enemy = Game.enemiesOnMap.get(i);
			if(enemy == this)// se o enemy estiver percorrendo a própria classe apenas continua o loopiong
				continue;
			
			Rectangle targetEnemy = new Rectangle(enemy.getX() + maskX, enemy.getY() + maskY, mWidth, mHeight);
			
			if(currentEnemy.intersects(targetEnemy))
				return true;
		}
		
		return false;
	}
	
	public static boolean isColidding(Entity entity1, Entity entity2) {
		Rectangle entityMask1 = new Rectangle(entity1.getX() + entity1.maskX, entity1.getY() + entity1.maskY, entity1.mWidth, entity1.mHeight);
		Rectangle entityMask2 = new Rectangle(entity2.getX() + entity2.maskX, entity2.getY() + entity2.maskY, entity2.mWidth, entity2.mHeight);
			
		return entityMask1.intersects(entityMask2);
	}
	
	public void render(Graphics graphics) {
		graphics.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//Usado para visualizar a colisão das entidade
		//graphics.setColor(Color.red);
		//graphics.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, mWidth, mHeight);
	}
}
