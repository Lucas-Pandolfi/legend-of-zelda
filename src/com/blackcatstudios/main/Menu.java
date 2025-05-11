package com.blackcatstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.blackcatstudios.world.World;

public class Menu {

	public int currentOption = 0;
	public boolean up, down, enter;
	
	private long lastMenuMove = 0;
	private static int encodeLevel = 5;
	private final long menuMoveDelay = 140;
	private static String saveFileName = "save.txt";

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
	    
	    if(enter) 
	    {
	        enter = false;
	        String selectedOption = currentMenuOptions[currentOption];
	        
	        if(selectedOption.equals("Continuar"))
	            Game.gameState = GameState.NORMAL;
	        else if(selectedOption.equals("Novo Jogo"))
	        {
	        	deleteSave();
	        	Game.gameState = GameState.NORMAL;
	        }
	        else if(selectedOption.equals("Salvar Jogo"))
	        	save();
	        else if(selectedOption.equals("Carregar Jogo")) 
	        {
	            File file = new File(saveFileName);
	            
	            if(file.exists()) 
	            {
	            	String save = loadGame(encodeLevel);
	            	
	            	applySave(save);
	            }
	            else
	            	Game.noSaveGameFoundModal.show();
	        } 
	        else if(selectedOption.equals("Sair")) {
	            System.exit(0);
	        }
	    }
	}
	
	public static void applySave(String str) {
	    String[] values = str.split("/");

	    for (int i = 0; i < values.length; i++) 
	    {
	        if (values[i].isEmpty()) continue;

	        String[] values2 = values[i].split(":");

	        if (values2.length < 2) continue;

	        switch (values2[0]) 
	        {
	            case "level":
	                World.restartGame("level" + values2[1] + ".png");
	                Game.gameState = GameState.NORMAL;
	                break;
	            case "life":
	            	Game.player.life = (int)Integer.parseInt(values2[1]);
	            	break;
	        }
	    }
	}
	
	public static void saveGame(String[] keys, int[] values, int encode) {
	    try {
	        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(saveFileName));

	        for (int i = 0; i < keys.length; i++) 
	        {
	            String currentEntity = keys[i] + ":";

	            char[] currentValue = Integer.toString(values[i]).toCharArray();
	            
	            for (int n = 0; n < currentValue.length; n++) {
	                currentValue[n] += encode;
	                currentEntity += currentValue[n];
	            }

	            bufferedWriter.write(currentEntity);

	            if (i < keys.length - 1)
	                bufferedWriter.newLine();
	        }

	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static String loadGame(int encode) {
		String line = "";
		File file = new File(saveFileName);
		
		if(file.exists()) 
		{
			try {
				String singleLine = null;
				
				BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFileName));
				
				try {
					while ((singleLine = bufferedReader.readLine()) != null) 
					{
					    String[] transitions = singleLine.split(":");

					    if (transitions.length < 2) continue; // skip invalid lines

					    char[] values = transitions[1].toCharArray();
					    transitions[1] = "";

					    for (int i = 0; i < values.length; i++) 
					    {
					        values[i] -= encode;
					        transitions[1] += values[i];
					    }

					    line += transitions[0] + ":" + transitions[1] + "/";
					}
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return line;
	}
	
	public String[] getMenuOptions() {	
	    if(Game.gameState == GameState.PAUSE) 
	        return new String[]{"Continuar", "Salvar Jogo", "Carregar Jogo", "Sair"};
	    else 
	        return new String[]{"Novo Jogo", "Carregar Jogo", "Sair"};
	}
	
	public void render(Graphics graphics) {
		RenderMenu(graphics);
	}
	
	private void RenderMenu(Graphics graphics) {
	    Graphics2D graphics2D = (Graphics2D) graphics;
	    graphics2D.setColor(new Color(0, 0, 0, 100));
	    graphics2D.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
	    
	    Font menuFont = Game.baseFont.deriveFont(Font.BOLD, 35f);

	    graphics.setFont(menuFont);
	    graphics.setColor(Color.white);
	    
	    String gameName = "Diff Killer";
	    FontMetrics fmGameName = graphics.getFontMetrics();
	    int gameNameWidth = fmGameName.stringWidth(gameName);
	    int gameNameY = (Game.HEIGHT * Game.SCALE) / 4;
	    graphics.drawString(gameName, (Game.WIDTH * Game.SCALE - gameNameWidth) / 2, gameNameY);
	    
	    String[] currentMenuOptions = getMenuOptions();
	    Font menuOptionsFont = Game.baseFont.deriveFont(Font.BOLD, 28f);
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
	
	private void save() {
		int currentPlayerLife = (int)Game.player.life;
    	
    	String[] keys = {"level", "life"};
    	int[] values = {Game.currentLevel, currentPlayerLife};
    	
    	saveGame(keys, values, encodeLevel);
    	
    	Game.saveModal.show();
	}
	
	private void deleteSave() {
		File file = new File(saveFileName);
		
		file.delete();
	}
}
