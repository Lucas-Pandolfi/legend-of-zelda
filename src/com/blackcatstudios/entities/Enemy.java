package com.blackcatstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.main.Sound;
import com.blackcatstudios.world.AStar;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.Node;
import com.blackcatstudios.world.World;

public class Enemy extends Entity {

	private double speed = 1;
	private int life = 3;
	private int maskX = 1, maskY = 1, maskWidth = 13, maskHeight = 14;
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 2;
	private BufferedImage[] sprites = new BufferedImage[2];
	private static int ENEMY_SIZE = 16;
	
	private boolean isDamaged = false;
	private int damageFrames = 0, currentDamage = 0;
	
	private List<Node> path;
	private int pathIndex = 0;
	private int pathCooldown = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		
		getSprites();
	}
	
	public void tick() {
		if(!enemyCollidingWithPlayer()) 
		{
			if (pathCooldown == 0 || path == null || path.isEmpty()) 
			{
			    path = AStar.findPath((int)x, (int)y, Game.player.getX(), Game.player.getY());
			    pathIndex = 0;
			    pathCooldown = 30; // só atualiza a cada 30 ticks (~0.5 segundo)
			} 
			else
			    pathCooldown--;

			if (path != null && pathIndex < path.size()) 
			{
			    Node target = path.get(pathIndex);
			    int tx = target.x * 16;
			    int ty = target.y * 16;

			    if (x < tx) 
			    	x += speed;
			    else if (x > tx) 
			    	x -= speed;

			    if (y < ty) 
			    	y += speed;
			    else if (y > ty)
			    	y -= speed;

			    if (Math.abs(x - tx) < 2 && Math.abs(y - ty) < 2)
			        pathIndex++;

			    animation();
			}
		}
		else 
		{
			Sound.playerReceivingDamageEffect.play();
			animation();
			
			decrementPlayerLife();
		}
				
		enemyCollidingWithBullet();
		
		damageAnimation();
		
		if(life <= 0) 
		{
			destroySelf();
			return;
		}
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
			if(enemy == this)// se o enemy que eu estiver percorrendo a própria classe eu apenas continuo o loopiong
				continue;
			
			Rectangle targetEnemy = new Rectangle(enemy.getX() + maskX, enemy.getY() + maskY, maskWidth, maskHeight);
			
			if(currentEnemy.intersects(targetEnemy))
				return true;
		}
		
		return false;
	}
	
	private void enemyCollidingWithBullet() {
		for(int i = 0; i < Game.bulletShoots.size(); i++) {
			Entity currentBullet = Game.bulletShoots.get(i);
			
			if(currentBullet instanceof BulletShoot) 
			{
				if(Entity.isColidding(this, currentBullet))
				{
					isDamaged = true;
					life--;
					Game.bulletShoots.remove(i);
					
					return;
				}
			}
		}
	}
	
	private void decrementPlayerLife() {
		if(Game.random.nextInt(100) < 10) 
		{
			 Game.player.life -= Game.random.nextInt(4);
			 Game.player.isDamaged = true;
		}
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
	
	private void destroySelf() {
		Game.entities.remove(this);
		Game.enemiesOnMap.remove(this);
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
	
	public void render(Graphics graphics) {	
		if(!isDamaged)
			graphics.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			graphics.drawImage(Entity.ENEMY_ENTITY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//Usado para visualizar a colisão do inimigos
		/*super.render(graphics);
		graphics.setColor(Color.blue);
		graphics.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskWidth, maskHeight);*/
	}
}
