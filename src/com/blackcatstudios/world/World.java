package com.blackcatstudios.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.blackcatstudios.entities.Ammo;
import com.blackcatstudios.entities.Enemy;
import com.blackcatstudios.entities.Entity;
import com.blackcatstudios.entities.Lifepack;
import com.blackcatstudios.entities.Player;
import com.blackcatstudios.entities.Weapon;
import com.blackcatstudios.graphics.Spritesheet;
import com.blackcatstudios.main.Game;

public class World {
	private static int wall = 0xFFFFFFFF;
	private static int player = 0xFF0026FF;//Este FF que vem depois do '0x' é necessário pois sem isso o Java considera a opacidade da cor.
	private static int weapon = 0xFFFF6A00;
	private static int ammo = 0xFFFFD800;
	private static int lifepack = 0xFF4CFF00;
	private static int enemy = 0xFFFF0000;
	private static final int MINIMAP_WIDTH =  70;
	private static final int MINIMAP_HEIGHT = 70;
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	public static int margin = 2;
	public static boolean showMiniMap = false;
	
	public BufferedImage lightMap;
	public int[] lightMapPixels;

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
					else if(currentPixel == ammo) 
					{
						Ammo ammo = new Ammo(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.AMMO_ENTITY);
						Game.entities.add(ammo);
						Game.ammosOnMap.add(ammo);
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
	
	public static void restartGame(String level) {
		Game.entities = new ArrayList<Entity>();
		Game.enemiesOnMap = new ArrayList<Enemy>();
		Game.lifepacksOnMap = new ArrayList<Lifepack>();
		Game.ammosOnMap = new ArrayList<Ammo>();
		Game.weaponsOnMap = new ArrayList<Weapon>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
		Game.miniMap = new BufferedImage(Game.world.WIDTH, Game.world.HEIGHT, BufferedImage.TYPE_INT_RGB);
		Game.miniMapPixels = ((DataBufferInt)Game.miniMap.getRaster().getDataBuffer()).getData();
		
		//Game.world.getLightMap();
		
		return;
	}
	
	public void getLightMap() {
		try {
			lightMap = ImageIO.read(getClass().getResource("/lightmap.png"));
			
			lightMapPixels = new int [lightMap.getWidth() * lightMap.getHeight()];
			
			lightMap.getRGB(0, 0, lightMap.getWidth(), lightMap.getHeight(), lightMapPixels, 0, lightMap.getWidth());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void applyLightMap() {
		for(int xx = 0; xx < Game.WIDTH; xx++) 
		{
			for(int yy = 0; yy < Game.HEIGHT; yy++) 
			{
				if(lightMapPixels != null && lightMapPixels[xx + (yy * Game.WIDTH)] == 0xffffffff)
					Game.pixels[xx + (yy * Game.WIDTH)] = 0;
			}
		}
	}
	
	public static void renderMiniMap(Graphics graphics) {
		Arrays.fill(Game.miniMapPixels, 0xff000000);

	    for(int xx = 0; xx < WIDTH; xx++) {
	        for(int yy = 0; yy < HEIGHT; yy++) {
	            if(tiles[xx + (yy * WIDTH)] instanceof WallTile) {
	                Game.miniMapPixels[xx + (yy * WIDTH)] = wall;
	            }
	        }
	    }
		
	    setPixel(Game.player.getX(), Game.player.getY(), player);

	    renderEntities(Game.enemiesOnMap, enemy);

	    renderEntities(Game.lifepacksOnMap, lifepack);

	    renderEntities(Game.weaponsOnMap, weapon);

	    renderEntities(Game.ammosOnMap, ammo);
		
	    int margin = 5;
	    int screenWidth = Game.WIDTH;
	    int posX = screenWidth - MINIMAP_WIDTH - margin;
	    int posY = margin;

	    if(showMiniMap)
	    	graphics.drawImage(Game.miniMap, posX, posY, MINIMAP_WIDTH, MINIMAP_HEIGHT, null);
	}
	
	public boolean isSolidTile(int x, int y) {
	    int tileX = x / TILE_SIZE;
	    int tileY = y / TILE_SIZE;

	    if (tileX < 0 || tileY < 0 || tileX >= WIDTH || tileY >= HEIGHT)
	        return true;

	    return tiles[tileX + (tileY * WIDTH)] instanceof WallTile;
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
	
	private static void setPixel(int x, int y, int color) {
	    int miniMapX = x / 16;
	    int miniMapY = y / 16;
	    Game.miniMapPixels[miniMapX + (miniMapY * WIDTH)] = color;
	}

	private static void renderEntities(List<? extends Entity> entities, int color) {
	    for (Entity e : entities)
	        setPixel(e.getX(), e.getY(), color);
	}
}
