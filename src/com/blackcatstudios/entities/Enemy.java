package com.blackcatstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.World;

public class Enemy extends Entity {

	private double speed = 1;
	private int maskX = 1, maskY = 1, maskWidth = 13, maskHeight = 14;
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 2;
	private BufferedImage[] sprites = new BufferedImage[2]; 
	
	private static int ENEMY_SIZE = 16;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		
		getSprites();
	}
	
	public void tick() {
		if(!enemyCollidingWithPlayer()) 
		{
			if(Game.random.nextInt(100) < 60) 
			{
				if((int)x < Game.player.getX() && World.collidedWithWallTile((int)(x + speed), (int)y, width, height)
						&& !enemyCollidingAnotherEnemy((int)(x + speed), (int)y))
			        x += speed;
			    else if((int)x > Game.player.getX() && World.collidedWithWallTile((int)(x - speed), (int)y, width, height)
			    		&& !enemyCollidingAnotherEnemy((int)(x - speed), (int)y))
			        x -= speed;
			    
			    if((int)y < Game.player.getY() && World.collidedWithWallTile((int)x, (int)(y + speed), width, height)
			    		&& !enemyCollidingAnotherEnemy((int)x, (int)(y + speed)))
			        y += speed;
			    else if((int)y > Game.player.getY() && World.collidedWithWallTile((int)x, (int)(y - speed), width, height)
			    		&& !enemyCollidingAnotherEnemy((int)x, (int)(y - speed)))
			        y -= speed;
			    
			    animation();
			}
		}
		else 
		{
			animation();
			
			if(Game.random.nextInt(100) < 10) {
				 Game.player.life -= Game.random.nextInt(6);
				
				if(Game.player.life <= 0)
					System.out.println("Mio pá nois parceiro!");
			}
		}
	}
	
	public void render(Graphics graphics) {	
		graphics.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//Usado para visualizar a colisão do inimigos
		/*super.render(graphics);
		graphics.setColor(Color.blue);
		graphics.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskWidth, maskHeight);*/
	}
	
	private boolean enemyCollidingWithPlayer() {
		Rectangle currentEnemy = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskWidth, maskHeight);
		
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16,  16);
		
		return currentEnemy.intersects(player);
	}
	
	private boolean enemyCollidingAnotherEnemy(int xNext, int yNext) {
		Rectangle currentEnemy = new Rectangle(xNext + maskX, yNext + maskY, maskWidth, maskHeight);
		
		for(int i = 0; i < Game.enemiesOnMap.size(); i++) 
		{
			Enemy enemy = Game.enemiesOnMap.get(i);
			if(enemy == this)// se o enemy que eu estiver percorrendo for a minha própria classe eu apenas continuo o loopiong
				continue;
			
			Rectangle targetEnemy = new Rectangle(enemy.getX() + maskX, enemy.getY() + maskY, maskWidth, maskHeight);
			
			if(currentEnemy.intersects(targetEnemy))
				return true;
		}
		
		return false;
	}
	
	private void getSprites() {
		sprites[0] = Game.spritesheet.getSprite(80, 16, ENEMY_SIZE, ENEMY_SIZE);
		sprites[1] = Game.spritesheet.getSprite(96, 16, ENEMY_SIZE, ENEMY_SIZE);
	}
	
	private void animation() {
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			
			if(index >= maxIndex)
				index = 0;
		}
	}
}
