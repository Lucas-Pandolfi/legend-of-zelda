package com.blackcatstudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.blackcatstudios.entities.Bullet;
import com.blackcatstudios.entities.BulletShoot;
import com.blackcatstudios.entities.Enemy;
import com.blackcatstudios.entities.Entity;
import com.blackcatstudios.entities.Lifepack;
import com.blackcatstudios.entities.Player;
import com.blackcatstudios.entities.Weapon;
import com.blackcatstudios.graphics.Spritesheet;
import com.blackcatstudios.graphics.UI;
import com.blackcatstudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener {
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	private final int SCALE = 3;
	private BufferedImage image;
	
	public static UI ui;
	public static World world;
	public static Player player;
	public static Random random;
	public static List<Entity> entities;
	public static List<Enemy> enemiesOnMap;
	public static List<Lifepack> lifepacksOnMap;
	public static List<Bullet> bulletsOnMap;
	public static List<Weapon> weaponsOnMap;
	public static List<BulletShoot> bulletShoots;
	public static Spritesheet spritesheet;
	
	public Game() {
		addKeyListener(this);	
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		
		//Initialize objects
		ui = new UI();
		random = new Random();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemiesOnMap = new ArrayList<Enemy>();
		lifepacksOnMap = new ArrayList<Lifepack>();
		bulletShoots = new ArrayList<BulletShoot>();
		bulletsOnMap = new ArrayList<Bullet>();
		weaponsOnMap = new ArrayList<Weapon>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/map.png");
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
		for(int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			entity.tick();
		}
		
		for(int i = 0; i < bulletShoots.size(); i++) {
			bulletShoots.get(i).tick();
		}
	}
	
	public void render() {
		BufferStrategy bufferStrategy = this.getBufferStrategy();	
		if(bufferStrategy == null) {
			this.createBufferStrategy(3); //Responsavel por otimização. Um buffer é uma área de memória temporária que armazena dados enquanto eles são transferidos de um lugar para outro
			return;
		}
		
		Graphics graphics = image.getGraphics();
		graphics.setColor(new Color(0, 0, 0));
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		
		world.render(graphics);
		
		for(int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			entity.render(graphics);
		}
		
		for(int i = 0; i < bulletShoots.size(); i++) {
			bulletShoots.get(i).render(graphics);
		}
		
		ui.render(graphics);
		
		graphics.dispose();
		graphics = bufferStrategy.getDrawGraphics();
		graphics.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		
		//Renderizando textos abaixo do "graphics.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);" faz com a fonte não fique pixelizada
		/*graphics.setFont(new Font("arial", Font.BOLD, 17));
		graphics.setColor(Color.white);
		graphics.drawString("Munição: " + Player.ammo, 620, 18);*/
		
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
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS:" + frames);
				frames = 0;
				timer += 1000;
			}
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
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
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

}
