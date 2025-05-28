package com.blackcatstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.blackcatstudios.main.Game;
import com.blackcatstudios.main.Sound;
import com.blackcatstudios.world.AStar;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.Vector2i;
import com.blackcatstudios.world.World;

public class Enemy extends Entity {

	private double speed = 0.4;
	private int life = 3;
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 2;
	private BufferedImage[] sprites = new BufferedImage[2];
	private static int ENEMY_SIZE = 16;
	
	private Vector2i target;
	private boolean isDamaged = false;
	private int damageFrames = 0, currentDamage = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		
		getSprites();
	}
	
	public void tick() {
		depth = 0;
		
		if(this.calculateDistace(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 100) 
		{
			Vector2i currentPlayerPos = new Vector2i((int)(Game.player.x / 16), (int)(Game.player.y / 16));
	        Vector2i currentEnemyPos = new Vector2i((int)(x / 16), (int)(y / 16));
	        
	        if (paths == null || paths.size() == 0 || !currentPlayerPos.equals(target)) 
	        {
	            // Player mudou de posição ou caminho terminou: recalcula
	            paths = AStar.findPath(Game.world, currentEnemyPos, currentPlayerPos);
	            target = currentPlayerPos;  // Atualiza o target
	        }
	        
	        if(new Random().nextInt(100) < 70)
	        	followPath(paths);
			
			if(enemyCollidingWithPlayer()) 
			{
				//Sound.playerReceivingDamageEffect.play();
				animation();
				
				Game.player.takeDamage();
			}
			
			animation();
		}
		
		animation();
		
		damageAnimation();
		
		if(life <= 0) 
		{
			destroySelf();
			return;
		}
	}
	
	public void takeDamage() {
		isDamaged = true;
		life--;
	}
	
	private boolean enemyCollidingWithPlayer() {
		Rectangle currentEnemy = new Rectangle(this.getX() + maskX, this.getY() + maskY, mWidth, mHeight);
		
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16,  16);
		
		return currentEnemy.intersects(player);
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
