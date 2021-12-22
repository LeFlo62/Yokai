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

public class TextComponent extends UIComponent {

	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 11);
	
	private String text;
	private Font font;
	private Color color;
	
	private int maxWidth;
	private boolean centerVetically;
	private boolean centerHorizontally;

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
		
		drawText(g, text, getX() + (centerHorizontally ? (getMaxWidth() - metrics.stringWidth(text))/2 : 0), (int)(getY() + bounds.getHeight() + (centerVetically ? (layer.getHeight() - bounds.getHeight())/2 : 0)));
	}
	
	private void drawText(Graphics2D g, String text, int x, int y) {
        AffineTransform transform = g.getTransform();
        transform.translate(x, y);
        g.transform(transform);
        
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
	
	public void setOutline(Color color, int size) {
		this.outlineColor = color;
		this.outlineSize = size;
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
