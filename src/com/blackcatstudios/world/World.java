package com.blackcatstudios.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.blackcatstudios.entities.Bullet;
import com.blackcatstudios.entities.Enemy;
import com.blackcatstudios.entities.Entity;
import com.blackcatstudios.entities.Lifepack;
import com.blackcatstudios.entities.Weapon;
import com.blackcatstudios.main.Game;

public class World {
	private static int wall = 0xFFFFFFFF;
	private static int player = 0xFF0026FF;//Este FF que vem depois do '0x' é necessário pois sem isso o Java considera a opacidade da cor.
	private static int weapon = 0xFFFF6A00;
	private static int bullet = 0xFFFFD800;
	private static int lifepack = 0xFF4CFF00;
	private static int enemy = 0xFFFF0000;
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	public static int margin = 2;

	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			tiles = new Tile[map.getWidth() * map.getHeight()];
			
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			
			map.getRGB(0,  0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			
			for(int xx = 0; xx < map.getWidth(); xx++) 
			{
				for(int yy = 0; yy < map.getHeight(); yy++) 
				{
					
					int currentPixel = pixels[xx + (yy * map.getWidth())];
					
					tiles[xx + (yy * WIDTH)] = new FloorTile(xx * TILE_SIZE, yy * TILE_SIZE, Tile.TILE_FLOOR);

					if (currentPixel == wall)			
						tiles[xx + (yy * WIDTH)] = new WallTile(xx * TILE_SIZE, yy * TILE_SIZE, Tile.TILE_WALL);
					else if(currentPixel == player) 
					{
						Game.player.setX(xx * TILE_SIZE);
						Game.player.setY(yy * TILE_SIZE);
					}
					else if(currentPixel == weapon) 
					{
						Weapon weapon = new Weapon(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.WEAPON_ENTITY);
						Game.entities.add(weapon);
						Game.weaponsOnMap.add(weapon);
					}
					else if(currentPixel == bullet) 
					{
						Bullet bullet = new Bullet(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.BULLET_ENTITY);
						Game.entities.add(bullet);
						Game.bulletsOnMap.add(bullet);
					}
					else if(currentPixel == lifepack) 
					{
						Lifepack lifepack = new Lifepack(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.LIFEPACK_ENTITY);
						Game.entities.add(lifepack);
						Game.lifepacksOnMap.add(lifepack);
					}
					else if(currentPixel == enemy) 
					{
						Enemy enemy = new Enemy(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.ENEMY_ENTITY);
						Game.entities.add(enemy);
						Game.enemiesOnMap.add(enemy);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean collidedWithWallTile(int x, int y, int width, int height) {
	    // Verifica todos os tiles que intersectam com a entidade
	    int x1 = x / TILE_SIZE;
	    int y1 = y / TILE_SIZE;
	    int x2 = (x + width - 1) / TILE_SIZE;
	    int y2 = (y + height - 1) / TILE_SIZE;
	    
	    for(int xx = x1; xx <= x2; xx++) {
	        for(int yy = y1; yy <= y2; yy++) {
	            if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
	                return false;
	                
	            if(tiles[xx + (yy * WIDTH)] instanceof WallTile)
	                return false;
	        }
	    }
	    return true;
	}
	
	public void render(Graphics graphics) {
		int camera_xstart = Camera.x >> 4; //Usamos int neste momento pois não queremos números quebrados e apenas inteiros paea inciar o eixo x de nossa camera
		int camera_ystart = Camera.y >> 4;
		
		int camera_xfinal = camera_xstart + (Game.WIDTH >> 4);//esse sinal '>>' se chama bitwise shift right, ele desloca os bits 4 casas para a direita
		int camera_yfinal = camera_ystart + (Game.HEIGHT >> 4);
		
		for(int xx = camera_xstart; xx <= camera_xfinal; xx++) {
			for(int yy = camera_ystart; yy <= camera_yfinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(graphics);
			}
		}
	}
}
