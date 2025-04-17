package com.blackcatstudios.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.blackcatstudios.entities.Player;

public class UI {

	public void render(Graphics graphics) {
		graphics.setColor(Color.red);
		graphics.fillRect(6, 5, 50, 7);
		
		graphics.setColor(Color.green);
		graphics.fillRect(6, 5, (int)((Player.life / Player.maxLife) * 50), 7);
		
		graphics.setColor(Color.white);
		graphics.setFont(new Font("arial", Font.BOLD, 7));
		graphics.drawString(Integer.toString((int)Player.life), 9, 11);
	}
}
