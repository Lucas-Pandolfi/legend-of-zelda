package com.blackcatstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.blackcatstudios.graphics.Spritesheet;
import com.blackcatstudios.graphics.UI;
import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.World;

public class Player extends Entity {

	public boolean right, left, up, down;
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = right_dir;
	public double speed = 1.2;
	public double life = 100, maxLife = 100;
	public int ammo = 0;
	public boolean  isDamaged = false;
	
	private int damageFrames = 0;
	private boolean moved = false;
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private BufferedImage playerDamage;
	private BufferedImage[] leftPlayer= new BufferedImage[3];
	private BufferedImage[] rightPlayer = new BufferedImage[3]; 
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		getSprites();
	}

	public void tick() {
		walk();
		
		getLife();
		
		getBullet();
		
		damageAnimation();
		
		if(life <= 0)
			gameOver();
		
		cameraClamp();
	}
	
	private void walk() {
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
	}
	
	private void getLife() {
	    for(int i = 0; i < Game.lifepacksOnMap.size(); i++) {
	        Lifepack currentLifepack = Game.lifepacksOnMap.get(i);
	        
	        if(Entity.isColidding(this, currentLifepack)) {
	            life += 8;
	            
	            if(life >= 100)
	                life = 100;
	            
	            Game.entities.remove(currentLifepack);
	            Game.lifepacksOnMap.remove(i);
	            return;
	        }
	    }   
	}
	
	private void getBullet() {
	    for(int i = 0; i < Game.bulletsOnMap.size(); i++) {
	        Bullet currentBullet = Game.bulletsOnMap.get(i);
	        
	        if(Entity.isColidding(this, currentBullet)) {
	            ammo += 4;
	            
	            Game.entities.remove(currentBullet);
	            Game.bulletsOnMap.remove(i);
	            return;
	        }
	    }   
	}
	
	private void getSprites() {
		for(int i = 0; i < rightPlayer.length; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, width, height);			
		}
		
		for(int i = 0; i < leftPlayer.length; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, width, height);			
		}
		
		playerDamage = Game.spritesheet.getSprite(0, 16, width, height);
	}
	
	private void damageAnimation() {
		if(isDamaged) 
		{
			damageFrames++;
			if(damageFrames == 8)
			{
				damageFrames = 0;
				isDamaged = false;
			}
		}
	}
	
	private void cameraClamp() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	private void gameOver() {
		Game.entities = new ArrayList<Entity>();
		Game.enemiesOnMap = new ArrayList<Enemy>();
		Game.lifepacksOnMap = new ArrayList<Lifepack>();
		Game.bulletsOnMap = new ArrayList<Bullet>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/map.png");
		
		return;
	}
	
	public void render(Graphics graphics) {
		if(!isDamaged) 
		{
			if(dir == right_dir) {
				graphics.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);			
			}
			else if(dir == left_dir) {
				graphics.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		else
			graphics.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
