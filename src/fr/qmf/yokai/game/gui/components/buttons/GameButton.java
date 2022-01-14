package fr.qmf.yokai.game.gui.components.buttons;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;

import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;
import fr.qmf.yokai.ui.components.Button;
import fr.qmf.yokai.ui.components.TextComponent;

public class GameButton extends Button {

	private Window window;
	
	private String text;
	private TextComponent textComponent;
	
	private Font defaultFont, hoveredFont;
	
	
	private Image background;
	
	private static final int HOVER_DELTA = 4;
	
	public GameButton(Window window, UILayer layer, Font font, String text, Color textColor, int x, int y, int width, int height) {
		super(layer, x, y, width, height);
		
		this.window = window;
		
		this.background = Textures.getTexture("gui/buttons/button_background").getScaledInstance(width, height, Image.SCALE_SMOOTH);
		
		this.text = text;
		this.defaultFont = font;
		this.hoveredFont = font.deriveFont((float)(font.getSize2D()+HOVER_DELTA/2f));
		
		this.textComponent = new TextComponent(layer, text, font, textColor, x+width/2, y+height/2);
		this.textComponent.setCenterHorizontally(true);
		this.textComponent.setShiftedVertically(true);
	}
	
	@Override
	public void draw(Graphics2D g) {
		int delta = hovered ? HOVER_DELTA : 0;
		g.drawImage(background, x-delta, y-delta, width+2*delta, height+2*delta, null);
		if(hovered) {
			textComponent.setFont(hoveredFont);
		} else {
			textComponent.setFont(defaultFont);
		}
		this.textComponent.draw(g);
	}
	
	@Override
	public boolean click(int screenX, int screenY, int x, int y, int clickCount) {
		window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		return false;
	}

	@Override
	public void hover(int screenX, int screenY, int x, int y) {
		
	}
	
	@Override
	public void setHovered(boolean hovered) {
		super.setHovered(hovered);
		if(hovered) {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public String getText() {
		return text;
	}
	
	public TextComponent getTextComponent() {
		return textComponent;
	}
	
	public void setText(String text) {
		this.text = text;
		this.textComponent.setText(text);
	}
	
	@Override
	public void setX(int x) {
		super.setX(x);
		this.textComponent.setX(x+width/2);
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		this.textComponent.setY(y+height/2);
	}

}
