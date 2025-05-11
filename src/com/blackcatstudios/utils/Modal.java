package com.blackcatstudios.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import com.blackcatstudios.main.Game;

public class Modal {
	public boolean visible;
	public boolean automaticHide;
	public long durationTime;
	
	private long showTime;
    private String message;
    private int x, y, width, height;
    private final int cornerRadius = 10; // Reduzido para sua tela pequena
    private final int borderThickness = 2;

    public Modal(String message, boolean automaticHide, long durationTime, int width, int height) {
        this.message = message;
        this.durationTime = durationTime;
        this.automaticHide = automaticHide;
        this.width = width;
        this.height = height;
        this.x = (Game.WIDTH - width) / 2;
        this.y = (Game.HEIGHT - height) / 2;
    }

    public void show() {
        this.visible = true;
        this.showTime = System.currentTimeMillis();  
    }

    public void tick() {
        if (!visible) 
        	return;
        
        if (automaticHide && System.currentTimeMillis() - this.showTime >= this.durationTime)
            hide();
    }

    public void hide() {
        this.visible = false;
    }

    public void render(Graphics g) {
        if (!visible) return;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fundo semi-transparente (cobre a tela inteira já escalada)
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);

            // Ajusta as coordenadas do modal para o espaço já escalado
            int scaledX = x * Game.SCALE;
            int scaledY = y * Game.SCALE;
            int scaledWidth = width * Game.SCALE - 1;
            int scaledHeight = height * Game.SCALE - 1;

            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                scaledX, scaledY, scaledWidth, scaledHeight, 
                cornerRadius * Game.SCALE, cornerRadius * Game.SCALE
            );

            g2.setColor(new Color(30, 30, 30));
            g2.fill(roundedRect);

            g2.setStroke(new BasicStroke(borderThickness * Game.SCALE));
            g2.setColor(Color.WHITE);
            g2.draw(roundedRect);

            // Texto centralizado (também ajustado para SCALE)
            g2.setFont(Game.baseFont.deriveFont(Font.BOLD, 10 * Game.SCALE)); // Ajusta fonte
            FontMetrics fm = g2.getFontMetrics();
            int textX = scaledX + (scaledWidth - fm.stringWidth(message)) / 2;
            int textY = scaledY + (scaledHeight - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(message, textX, textY);
        } finally {
            g2.dispose();
        }
    }
}