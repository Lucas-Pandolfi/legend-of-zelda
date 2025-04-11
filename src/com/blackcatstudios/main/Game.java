package com.blackcatstudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.blackcatstudios.entities.Entity;
import com.blackcatstudios.entities.Player;
import com.blackcatstudios.graphics.Spritesheet;
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
	
	public static World world;
	public static Player player;
	public static List<Entity> entities;
	public static Spritesheet spritesheet;
	
	public Game() {
		addKeyListener(this);	
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		
		//Initialize objects
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
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
		
		/*Graphics2D g2 = (Graphics2D) g;*/
		world.render(graphics);
		
		for(int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			entity.render(graphics);
		}
		
		graphics.dispose();
		graphics = bufferStrategy.getDrawGraphics();
		graphics.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		bufferStrategy.show();
	}
	
	public void run() {
		long lastTime = System.nanoTime(); // Responsavel por pegar o tempo atual do nosso computador em nano segundos, é utilizado assim pela alta precisão
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks; // Dividindo 1 segundo (formato de nano) com o amountOfTicks. o ns recebera o tempo para atualizar o game
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		
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
