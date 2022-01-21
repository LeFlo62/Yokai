/*    MIT License

Copyright (c) 2021 Leclercq Florentin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package fr.qmf.yokai.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import fr.qmf.yokai.ui.UIComponent;
import fr.qmf.yokai.ui.UILayer;

/**
 * A UIComponent that displays Text.
 * @author LeFlo
 *
 */
public class TextComponent extends UIComponent {

	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 11);
	
	private String text;
	private Font font;
	private Color color;
	
	private int maxWidth;
	private boolean centerVertically;
	private boolean centerHorizontally;
	private boolean shiftedVertically;

	private int outlineSize;

	private Color outlineColor;

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
	public void draw(Graphics2D g) {
		g.setColor(color);
		if(font != null) g.setFont(font);
		
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
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
		
		int lineOffset = 0;
		for(String line : text.split(System.lineSeparator())) {
			drawText(g, line, getX() + (centerHorizontally ? (getMaxWidth() - metrics.stringWidth(line))/2 : 0), (int)(getY() + lineOffset + (shiftedVertically ? bounds.getHeight()/2 : bounds.getHeight()) + (centerVertically ? (layer.getHeight() - bounds.getHeight())/2 : 0)));
			lineOffset += metrics.getHeight();
		}
	}
	
	/**
	 * Draws a String {@code text} on the {@code x} and {@code y} coordinates. 
	 * @param g The graphics context.
	 * @param text The text to draw.
	 * @param x The x position in pixels on window.
	 * @param y The y position in pixels on window.
	 */
	private void drawText(Graphics2D g, String text, int x, int y) {
		AffineTransform before = g.getTransform();
        AffineTransform transform = g.getTransform();
        transform.translate(x, y);
        g.setTransform(transform);
        
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout tl = new TextLayout(text, font, frc);
        Shape shape = tl.getOutline(null);
        
        if(outlineSize != 0) {
        	g.setColor(outlineColor);
            g.setStroke(new BasicStroke(outlineSize));
            g.draw(shape);
        }
        
        g.setColor(color);
        g.fill(shape);
        g.setTransform(before);
	}

	/**
	 * The current text being drawn.
	 * @return The current text being drawn.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text to be drawn.
	 * @param text The text to be drawn.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * The font used to draw the text.
	 * @return The font used to draw the text.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the font to be used to draw the text.
	 * @param font The font to be used to draw the text.
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * The color used to draw the text.
	 * @return The color used to draw the text.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color to be used to draw the text.
	 * @param color The color to be used to draw the text.
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Sets an outline 
	 * @param color The color of the outline.
	 * @param size The size of the outline.
	 */
	public void setOutline(Color color, int size) {
		this.outlineColor = color;
		this.outlineSize = size;
	}

	/**
	 * The maximum width this TextComponent may have.
	 * 0 means is has not maxWidth.
	 * @return The maximum width this TextComponent may have.
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Sets the maximum width this TextComponent may have.
	 * 0 means is has not maxWidth.
	 * @return The maximum width this TextComponent may have.
	 */
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * Is this text centered vertically on the screen.
	 * @return true if this text is centered vertically on the screen.
	 */
	public boolean isCenterVetically() {
		return centerVertically;
	}

	/**
	 * Sets this text centered vertically on the screen.
	 * @param centerVertically Should be true if this text should be centered vertically on the screen.
	 */
	public void setCenterVertically(boolean centerVertically) {
		this.centerVertically = centerVertically;
	}
	
	/**
	 * Is this text centered horizontally from its middle.
	 * @return true if this text is centered horizontally from its middle.
	 */
	public boolean isCenterHorizontally() {
		return centerHorizontally;
	}

	/**
	 * Sets this text centered horizontally from its middle.
	 * @param centerHorizontally Should be true if this text should be centered horizontally from its middle.
	 */
	public void setCenterHorizontally(boolean centerHorizontally) {
		this.centerHorizontally = centerHorizontally;
	}
	
	/**
	 * Sets this text centered vertically from its middle.
	 * @param centerVertically Should be true if this text should be centered vertically from its middle.
	 */
	public void setShiftedVertically(boolean shiftedVertically) {
		this.shiftedVertically = shiftedVertically;
	}
	
	/**
	 * Is this text centered vertically from its middle.
	 * @return true if this text is centered vertically from its middle.
	 */
	public boolean isShiftedVertically() {
		return shiftedVertically;
	}

}
