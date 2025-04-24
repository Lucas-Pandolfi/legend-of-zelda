package com.blackcatstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Menu {

	public int currentOption = 0;
	public boolean up, down, enter;
	
	private long lastMenuMove = 0;
	private final long menuMoveDelay = 140;

	public void tick() {
	    if(Game.gameState != GameState.PAUSE && Game.gameState != GameState.MENU) {
	        return;
	    }

	    long now = System.currentTimeMillis();
	    String[] currentMenuOptions = getMenuOptions();
	    
	    if(up) {
	    	up = false;
	    	
	        if(now - lastMenuMove > menuMoveDelay) {
	            lastMenuMove = now;
	            currentOption--;
	            if(currentOption < 0) currentOption = currentMenuOptions.length - 1;
	        }
	    }
	    
	    if(down) {
	    	down = false;
	        if(now - lastMenuMove > menuMoveDelay) {
	            lastMenuMove = now;
	            currentOption++;
	            if(currentOption >= currentMenuOptions.length) currentOption = 0;
	        }
	    }
	    
	    if(enter) {
	    	System.out.println("entrouuuuu");
	        enter = false;
	        String selectedOption = currentMenuOptions[currentOption];
	        
	        if(selectedOption.equals("Continuar")) {
	            Game.gameState = GameState.NORMAL;
	        } 
	        else if(selectedOption.equals("Novo Jogo")) {
	            Game.gameState = GameState.NORMAL;
	            // Adicionar aqui a inicialização do novo jogo
	        }
	        else if(selectedOption.equals("Carregar Jogo")) {
	            // Adicioanr a Lógica para carregar jogo
	        } 
	        else if(selectedOption.equals("Sair")) {
	            System.exit(0);
	        }
	    }
	}
	
	public void render(Graphics graphics) {
		RenderMenu(graphics);
	}
	
	private void RenderMenu(Graphics graphics) {
	    Graphics2D graphics2D = (Graphics2D) graphics;
	    graphics2D.setColor(new Color(0, 0, 0, 100));
	    graphics2D.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
	    
	    Font menuFont = new Font("arial", Font.BOLD, 35);
	    graphics.setFont(menuFont);
	    graphics.setColor(Color.white);
	    
	    String gameName = "Diff Killer";
	    FontMetrics fmGameName = graphics.getFontMetrics();
	    int gameNameWidth = fmGameName.stringWidth(gameName);
	    int gameNameY = (Game.HEIGHT * Game.SCALE) / 4;
	    graphics.drawString(gameName, (Game.WIDTH * Game.SCALE - gameNameWidth) / 2, gameNameY);
	    
	    String[] currentMenuOptions = getMenuOptions();
	    Font menuOptionsFont = new Font("arial", Font.BOLD, 28);
	    graphics.setFont(menuOptionsFont);
	    FontMetrics fmGameOptions = graphics.getFontMetrics();
	    
	    int optionSpacing = 50;
	    int firstOptionY = gameNameY + 100;
	    int optionHeight = fmGameOptions.getHeight();
	    
	    for (int i = 0; i < currentMenuOptions.length; i++) {
	        String option = currentMenuOptions[i];
	        int optionWidth = fmGameOptions.stringWidth(option);
	        int optionY = firstOptionY + (i * optionSpacing);
	        
	        if (i == currentOption) 
	        {
	            graphics2D.setColor(Color.WHITE);
	            int padding = 10;
	            int backgroundWidth = optionWidth + padding * 2;
	            int backgroundHeight = optionHeight + padding;
	            int backgroundX = (Game.WIDTH * Game.SCALE - backgroundWidth) / 2;
	            int backgroundY = optionY - optionHeight + padding / 2;
	            
	            graphics2D.fillRoundRect(backgroundX, backgroundY, backgroundWidth, backgroundHeight, 10, 10);
	            
	            graphics.setColor(Color.BLACK);
	            graphics.drawString(option, (Game.WIDTH * Game.SCALE - optionWidth) / 2, optionY);
	            
	            graphics.setColor(Color.WHITE);
	        } 
	        else 
	            graphics.drawString(option, (Game.WIDTH * Game.SCALE - optionWidth) / 2, optionY);
	    }
	}
	
	public String[] getMenuOptions() {
	    if(Game.gameState == GameState.PAUSE) 
	        return new String[]{"Continuar", "Carregar Jogo", "Sair"};
	    else 
	        return new String[]{"Novo Jogo", "Carregar Jogo", "Sair"};
	}
}
