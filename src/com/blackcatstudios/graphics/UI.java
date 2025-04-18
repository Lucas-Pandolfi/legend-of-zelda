package com.blackcatstudios.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.blackcatstudios.entities.Player;
import com.blackcatstudios.main.Game;

public class UI {

	public void render(Graphics graphics) {
		
		addUiLifeBar(graphics);
		addUiAmmo(graphics);
	}
	
	private void addUiLifeBar(Graphics graphics) {
		graphics.setColor(Color.red);
		graphics.fillRect(6, 5, 50, 7);
		
		graphics.setColor(Color.green);
		graphics.fillRect(6, 5, (int)((Game.player.life / Game.player.maxLife) * 50), 7);
		
		graphics.setColor(Color.white);
		graphics.setFont(new Font("arial", Font.BOLD, 7));
		graphics.drawString(Integer.toString((int)Game.player.life), 9, 11);
	}
	
	private void addUiAmmo(Graphics graphics) {
		graphics.setColor(Color.yellow);
		graphics.fillRect(60, 4, 4, 8);
		graphics.setColor(Color.black);
		graphics.drawRect(60, 4, 4, 8);
		
		graphics.setColor(Color.white);
		graphics.setFont(new Font("arial", Font.BOLD, 10));
		graphics.drawString(Integer.toString((int)Game.player.ammo), 67, 12);
	}
}
