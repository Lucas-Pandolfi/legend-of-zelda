package com.blackcatstudios.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 240;
	private final int HEIGHT = 160;
	private final int SCALE = 3;
	
	private BufferedImage image;
	
	public Game() {
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
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
	
	public static  void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();	
		if(bs == null) {
			this.createBufferStrategy(3); //Responsavel por otimização. Um buffer é uma área de memória temporária que armazena dados enquanto eles são transferidos de um lugar para outro
			return;
		}
		
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/*Graphics2D g2 = (Graphics2D) g;*/
 		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		bs.show();
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

}
