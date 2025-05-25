package com.blackcatstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.blackcatstudios.graphics.Spritesheet;
import com.blackcatstudios.graphics.UI;
import com.blackcatstudios.main.Game;
import com.blackcatstudios.main.GameState;
import com.blackcatstudios.main.Sound;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.World;

public class Player extends Entity {

	public double speed = 1.2;
	public boolean right, left, up, down;
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = right_dir;
	public int mx = 0;
	public int my = 0;	
	public int ammo = 0;
	public double life = 100, maxLife = 100;
	public boolean hasGun = false;
	public boolean keyboardShoot = false;
	public boolean mouseShoot = false;
	public boolean  isDamaged = false;
	
	private boolean moved = false;
	private int damageFrames = 0;
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
		
		getWeapon();
		
		getAmmo();
		
		damageAnimation();
		
		Shoot();
		
		MouseShoot();
		
		if(life <= 0)
			Game.gameState = GameState.GAME_OVER;
		
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
		if(life == 100)
			return;
			
	    for(int i = 0; i < Game.lifepacksOnMap.size(); i++) {
	        Lifepack currentLifepack = Game.lifepacksOnMap.get(i);
	        
	        if(Entity.isColidding(this, currentLifepack)) {
	            life += 25;
	            
	            if(life >= 100)
	                life = 100;
	            
	            Game.entities.remove(currentLifepack);
	            Game.lifepacksOnMap.remove(i);
	            return;
	        }
	    }   
	}
	
	private void getWeapon() {
	    for(int i = 0; i < Game.weaponsOnMap.size(); i++) {
	        Weapon currentweapon = Game.weaponsOnMap.get(i);
	        
	        if(Entity.isColidding(this, currentweapon)) {
	        	hasGun = true;
	        	
	            Game.entities.remove(currentweapon);
	            Game.weaponsOnMap.remove(i);
	            return;
	        }
	    }   
	}
	
	private void getAmmo() {
	    for(int i = 0; i < Game.ammosOnMap.size(); i++) {
	        Ammo currentAmmo = Game.ammosOnMap.get(i);
	        
	        if(Entity.isColidding(this, currentAmmo)) {
	            ammo += 15;
	            
	            Game.entities.remove(currentAmmo);
	            Game.ammosOnMap.remove(i);
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
	
	private void Shoot() {
		if(keyboardShoot)
		{
			keyboardShoot = false;
			if(hasGun && ammo > 0) 
			{
				//Sound.pistolShootEffect.play();
				ammo--;
				int directionX = 0;
				int px = 0;
				int py = 8;
				
				if(dir == right_dir) 
				{
					px = 18;
					py = 5;
					directionX = 1;
				}
				else 
				{
					px = -3;
					py = 3;
					directionX = -1;
				}
				
				BulletShoot bulletShoot = new BulletShoot(this.getX() + px, this.getY() + py, 2, 2, null, directionX, 0);
				Game.bulletShoots.add(bulletShoot);
			}
		}
	}
	
	private void MouseShoot() {
		if(mouseShoot)
		{
			mouseShoot = false;
			if(hasGun && ammo > 0) 
			{
				//Sound.pistolShootEffect.play();
				ammo--;				
				double angle = Math.atan2(my - (this.getY() + 8 - Camera.y), mx - (this.getX() + 8 - Camera.x));
				double directionX = Math.cos(angle);
				double directionY = Math.sin(angle);
				int px = 0;
				int py = 8;
				
				if(dir == right_dir) 
				{
					px = 18;
					py = 5;
				}
				else 
				{
					px = -3;
					py = 3;
				}
				
				BulletShoot bulletShoot = new BulletShoot(this.getX() + px, this.getY() + py, 2, 2, null, directionX, directionY);
				Game.bulletShoots.add(bulletShoot);
			}
		}
	}
	
	public void render(Graphics graphics) {
		if(!isDamaged) 
		{
			if(dir == right_dir) {
				graphics.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				
				//Desenhar a arma do player para a direita
				if(hasGun)
					graphics.drawImage(Entity.WEAPON_RIGHT, this.getX() + 11 - Camera.x, this.getY() - 2 - Camera.y, null);
			}
			else if(dir == left_dir) {
				graphics.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - 2 - Camera.y, null);
				
				//Desenhar a arma do player para a esquerda
				if(hasGun)
					graphics.drawImage(Entity.WEAPON_LEFT, this.getX() - 10 - Camera.x, this.getY() - 4 - Camera.y, null);
			}
		}
		else
			graphics.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
