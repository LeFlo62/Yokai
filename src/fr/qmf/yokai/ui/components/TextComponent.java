package fr.qmf.yokai.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import fr.qmf.yokai.ui.UIComponent;
import fr.qmf.yokai.ui.UILayer;

public class TextComponent extends UIComponent {

	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 11);
	
	private String text;
	private Font font;
	private Color color;
	
	private int maxWidth;
	private boolean centerVetically;
	private boolean centerHorizontally;

	public TextComponent(UILayer layer, String text, int x, int y) {
		this(layer, text, DEFAULT_FONT, Color.BLACK, x, y);
	}
	
	public TextComponent(UILayer layer, String text, Font font, Color color, int x, int y) {
		super(layer, x, y, 0, 0);
		this.text = text;
		this.font = font;
		this.color = color;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		if(font != null) g.setFont(font);
		
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		Rectangle2D bounds = metrics.getStringBounds(text, g);
		
		if(maxWidth != 0) {
			String shortenedText = text;
			while(metrics.getStringBounds(shortenedText, g).getWidth() > maxWidth) {
				shortenedText = shortenedText.substring(0, shortenedText.length()-4) + "...";
			}
			this.text = shortenedText;
		}
		
		if(getWidth() <= bounds.getWidth() && (maxWidth == 0 || bounds.getWidth() <= maxWidth)) setWidth((int)bounds.getWidth());
		if(getHeight() <= bounds.getHeight()) setHeight((int) bounds.getHeight());
		
		g.drawString(text, getX() + (centerHorizontally ? (getMaxWidth() - metrics.stringWidth(text))/2 : 0), (int)(getY() + bounds.getHeight() + (centerVetically ? (layer.getHeight() - bounds.getHeight())/2 : 0)));
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public boolean isCenterVetically() {
		return centerVetically;
	}

	public void setCenterVetically(boolean centerVetically) {
		this.centerVetically = centerVetically;
	}

	public boolean isCenterHorizontally() {
		return centerHorizontally;
	}

	public void setCenterHorizontally(boolean centerHorizontally) {
		this.centerHorizontally = centerHorizontally;
	}
	
	

}
