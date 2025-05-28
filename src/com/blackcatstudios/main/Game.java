package com.blackcatstudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.image.DataBufferInt;


import javax.swing.JFrame;

import com.blackcatstudios.entities.Ammo;
import com.blackcatstudios.entities.BulletShoot;
import com.blackcatstudios.entities.Enemy;
import com.blackcatstudios.entities.Entity;
import com.blackcatstudios.entities.Lifepack;
import com.blackcatstudios.entities.Player;
import com.blackcatstudios.entities.Weapon;
import com.blackcatstudios.graphics.Spritesheet;
import com.blackcatstudios.graphics.UI;
import com.blackcatstudios.utils.Modal;
import com.blackcatstudios.world.Camera;
import com.blackcatstudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	private Thread thread;
	public static JFrame frame;
	private boolean isRunning = true;
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	private BufferedImage image;
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	private int maxLevel = 2;
	
	public static UI ui;
	public static Menu menu;
	public static World world;
	public static Player player;
	public static Random random;
	public static int currentLevel = 1;
	public static List<Entity> entities;
	public static List<Enemy> enemiesOnMap;
	public static List<Lifepack> lifepacksOnMap;
	public static List<Ammo> ammosOnMap;
	public static List<Weapon> weaponsOnMap;
	public static List<BulletShoot> bulletShoots;
	public static Spritesheet spritesheet;
	public static GameState gameState = GameState.MENU;
	public static Font baseFont;
	public InputStream streamFont = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");	
	public static Modal saveModal = new Modal("Jogo salvo!", true, 750, 120, 40);
	public static Modal noSaveGameFoundModal = new Modal("Nenhum save encontrado!", true, 850, 150, 40);
	public int mouseX, mouseY;
	public static int[] pixels;
	public static BufferedImage miniMap;
	public static int[] miniMapPixels;
	
	public Game() {
		//Sound.musicBackground.loop();
		
		addKeyListener(this);	
		addMouseListener(this);	
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		
		//Initialize objects
		ui = new UI();
		random = new Random();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		//pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		bulletShoots = new ArrayList<BulletShoot>();
		enemiesOnMap = new ArrayList<Enemy>();
		lifepacksOnMap = new ArrayList<Lifepack>();
		ammosOnMap = new ArrayList<Ammo>();
		weaponsOnMap = new ArrayList<Weapon>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		miniMap = new BufferedImage(world.WIDTH, world.HEIGHT, BufferedImage.TYPE_INT_RGB);
		miniMapPixels = ((DataBufferInt)miniMap.getRaster().getDataBuffer()).getData();
		menu = new Menu();
		
		//world.getLightMap();
		
		try {
			baseFont = Font.createFont(Font.TRUETYPE_FONT, streamFont);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void initFrame() {
		frame = new JFrame("Game teste - #1");
		frame.add(this);
		frame.setResizable(false); // Não permite o usuário a redimensionar a janela do jogo
		frame.pack(); // Responsavel por calcular as dimensões e apresentar a janela.
		frame.setLocationRelativeTo(null); // Seta a janela do jogo no centro da tela 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Após clicar no botão para fechar a janela este comando encerra o jogo
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
			
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if(gameState == GameState.NORMAL) 
		{
			restartGame = false;
			
			for(int i = 0; i < entities.size(); i++) {
				Entity entity = entities.get(i);
				entity.tick();
			}
			
			for(int i = 0; i < bulletShoots.size(); i++) {
				bulletShoots.get(i).tick();
			}
			
			renderLevel();
		}
		else if(gameState == GameState.GAME_OVER) 
		{
			gameOverAnimation();
			
			if(restartGame) 
			{
				restartGame = false;
				
				gameState = GameState.NORMAL;
				
				String newWorld = "level" + currentLevel + ".png";		

				World.restartGame(newWorld);
			}
		}
		else if(gameState == GameState.PAUSE || gameState == GameState.MENU)
		{
			if(saveModal.visible)
				saveModal.tick();
			else if(noSaveGameFoundModal.visible)
				noSaveGameFoundModal.tick();
			
			menu.tick();
		}
	}
	
	public void renderLevel() {
		if(enemiesOnMap.size() == 0) 
		{
			currentLevel++;
			
			if(currentLevel > maxLevel)
				currentLevel = 1;
			
			String newWorld = "level" + currentLevel + ".png";		

			World.restartGame(newWorld);
			Sound.stopAllSounds();
			//Sound.musicBackground.loop();
		}
	}
	
	//Método resposnavel por manipular pixel por pixeldo meu mapa
	/*public void drawRectangleExample(int xOff, int yOff) {
		for(int xx = 0; xx < 32; xx++) 
		{
			for(int yy = 0; yy < 32; yy++) 
			{
				int xOffValue = xx + xOff;
				int yOffValue = yy + yOff;
				
				if(xOffValue < 0 || yOffValue < 0 || xOffValue >= WIDTH || yOffValue >= HEIGHT)
					continue;
				
				pixels[xOffValue + (yOffValue * WIDTH)] = 0xff0000;
			}
		}
	}*/
	
	public void render() {
	    BufferStrategy bufferStrategy = this.getBufferStrategy();    
	    if(bufferStrategy == null) {
	        this.createBufferStrategy(3);
	        return;
	    }
	    
	    Graphics graphics = image.getGraphics();
	    graphics.setColor(new Color(0, 0, 0));
	    graphics.fillRect(0, 0, WIDTH, HEIGHT);
	    
	    // ==== RENDERIZAÇÃO DO JOGO (ANTES DO SCALE) ====
	    world.render(graphics);
	    
	    Collections.sort(entities, Entity.entitySorter);
	    
	    for(int i = 0; i < entities.size(); i++) {
	        Entity entity = entities.get(i);
	        entity.render(graphics);
	    }
	    
	    for(int i = 0; i < bulletShoots.size(); i++) {
	        bulletShoots.get(i).render(graphics);
	    }
	    
	    //world.applyLightMap();
	    
	    ui.render(graphics);
	    
	    world.renderMiniMap(graphics);
	    
	    graphics.dispose();
	    
	    // ==== APÓS ISSO, APLICA O SCALE ====
	    graphics = bufferStrategy.getDrawGraphics();
	    
	    graphics.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
	    
	    // ==== RENDERIZAÇÃO DO MENU (DEPOIS DO SCALE, PARA FICAR POR CIMA) ====
	    if(gameState == GameState.GAME_OVER)
	        gameOverMessage(graphics);
	    else if(gameState == GameState.PAUSE || gameState == GameState.MENU)
	        menu.render(graphics);  // Menu continua por cima de tudo   
	    
	    Game.saveModal.render(graphics);
	    
	    Game.noSaveGameFoundModal.render(graphics);
	    //rotateObjectFollowingMousePosition(graphics);
	    
	    bufferStrategy.show();
	}
	
	public void run() {
		long lastTime = System.nanoTime(); // Responsavel por pegar o tempo atual do nosso computador em nano segundos, é utilizado assim pela alta precisão
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks; // Dividindo 1 segundo (formato de nano) com o amountOfTicks. o ns recebera o tempo para atualizar o game
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();//Comando usuado para focar na janela do jogo quando ele iniciar
		
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				
				frames++;
				delta--;
			}
			
			// Lógica para saber se o game esta rodando a 60 FPS
			/*if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS:" + frames);
				frames = 0;
				timer += 1000;
			}*/
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			
			if(gameState == GameState.MENU  || gameState == GameState.PAUSE)
				menu.up = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
			if(gameState == GameState.MENU || gameState == GameState.PAUSE)
				menu.down = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.keyboardShoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = true;
			
			if(gameState == GameState.MENU || gameState == GameState.PAUSE)
				menu.enter = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) 
		{
			if(gameState != GameState.MENU)
				gameState = GameState.PAUSE;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_M)
			world.showMiniMap = world.showMiniMap == true ? false : true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = e.getX() / 3;//Dividimos por pois é a escala do nosso jogo. Dessa forma eu  vou pegar a posição do mouse no meu mundo e não na minha janela
		player.my = e.getY() / 3;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}
	
	private void gameOverMessage(Graphics graphics) {
	    Graphics2D graphics2D = (Graphics2D) graphics;
	    
	    graphics2D.setColor(new Color(0, 0, 0, 100));
	    graphics2D.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
	    
	    Font gameOverFont = new Font("arial", Font.BOLD, 35);
	    graphics.setFont(gameOverFont);
	    graphics.setColor(Color.white);
	    
	    String gameOverText = "Game Over";
	    
	    FontMetrics fm = graphics.getFontMetrics(gameOverFont);
	    int gameOverWidth = fm.stringWidth(gameOverText);
	    graphics.drawString(gameOverText, (WIDTH * SCALE - gameOverWidth) / 2, (HEIGHT * SCALE) / 2);
	    
	    if(showMessageGameOver) 
	    {
	        Font restartFont = new Font("arial", Font.BOLD, 28);
	        graphics.setFont(restartFont);
	        
	        String restartText = ">Pressione 'Enter' para reiniciar<";
	        
	        fm = graphics.getFontMetrics(restartFont);
	        int restartWidth = fm.stringWidth(restartText);
	        graphics.drawString(restartText, (WIDTH * SCALE - restartWidth) / 2, (HEIGHT * SCALE) / 2 + 40);
	    }
	}
	
	private void gameOverAnimation() {
		framesGameOver++;
		
		if(framesGameOver == 60) {
			framesGameOver = 0;
			
			if(showMessageGameOver)
				showMessageGameOver = false;
			else
				showMessageGameOver = true;
		}
	}
	
	//TODO: Criar uma classe no package utils e passar mais parametros como a sprite que vc quer renderizar, width e height da sprite
	private void rotateObjectFollowingMousePosition(Graphics graphics) {
		Graphics2D graphics2D = (Graphics2D) graphics;
		
		double angleMouse = Math.atan2(mouseY - 200 + 25, mouseX - 200 + 25);
		
		graphics2D.rotate(angleMouse, 200 + 25, 200 + 25);//o 25 é usado para fazer com que o objeto rotacione no mesmo local de origem, este 25 é a metade do width e height do objeto que você quer rotacionar
		graphics.setColor(Color.BLUE);
	    graphics.fillRect(200, 200, 50, 50);
	}
}
