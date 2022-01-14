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
package fr.qmf.yokai.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import javax.swing.JFrame;

/**
 *	Represents a window shown and opened on the user screen.<br/>
 *	The dimensions are fixed and cannot be changed.<br/>
 *	A future improvements would be to be able to change them. 
 */
public class Window implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final int DEFAULT_WIDTH = 1280, DEFAULT_HEIGHT = 720;
	private static final int MIN_WIDTH = 970, MIN_HEIGHT = 690;
	private static final int RESIZE_TIME = 50;
	
	private String title;
	
	private JFrame frame;
	private Image iconImage;
	private BufferedImage screen;
	
	private Canvas canvas;
	private BufferStrategy bufferStrategy;
	private Graphics graphics;

	private UILayer currentLayer;
	
	private int mouseX, mouseY;
	
	private int dragStartX, dragStartY;
	private int previousDragX, previousDragY;
	private boolean dragging;
	private Queue<Dragable> draggingContainers = new ArrayDeque<>();
	
	private boolean layerConstrained = false;

	private long lastResized;
	private int width, height;

	public Window(String title, Image iconImage) {
		this.title = title;
		this.iconImage = iconImage;
		
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		
		initHandle();
		
		this.currentLayer = new EmptyLayer(this);
	}
    
	/**
	 * Creates everything needed to have a window.
	 */
	private void initHandle() {
		screen = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		frame = new JFrame(title) {
			private static final long serialVersionUID = 1L;
			@Override
			public void invalidate() {
				super.invalidate();
				lastResized = System.currentTimeMillis();
			}
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		frame.setLocationRelativeTo(null);
		frame.setIconImage(iconImage);
		
		canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		canvas.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
		
		frame.setVisible(true);
		
		frame.add(canvas);
		
		canvas.requestFocus();
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		graphics = bufferStrategy.getDrawGraphics();
		
		lastResized = 0;
	}
	
	/**
	 * Clears the screen.
	 */
	public void clear() {
		if(lastResized == 0) {
			Graphics g = screen.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
		}
	}
	
	/**
	 * Draws the screen from the currentLayer onto this window.
	 */
	public void draw() {
		if(currentLayer == null) return;
		
		if (lastResized != 0 && System.currentTimeMillis() - lastResized > RESIZE_TIME) {
			lastResized = 0;
			
			width = frame.getWidth();
			height = frame.getHeight();
			
			screen = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
			canvas.setSize(frame.getWidth(), frame.getHeight());
			graphics = bufferStrategy.getDrawGraphics();
		}
		
		if(lastResized == 0) {
			Graphics2D g = screen.createGraphics();
			
			g.setRenderingHint(
				    RenderingHints.KEY_ANTIALIASING,
				    RenderingHints.VALUE_ANTIALIAS_ON);
			
			currentLayer.draw(g);
			
			graphics.drawImage(screen, 0, 0, screen.getWidth(), screen.getHeight(), null);
			
			bufferStrategy.show();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
		currentLayer.getDeepChildren()
		.filter(c -> c instanceof Hoverable)
		.forEach(c -> {
			Hoverable h = (Hoverable) c;
			if(c.isInside(e.getX(), e.getY())) {
				h.setHovered(true);
				h.hover(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY());
			} else if(h.isHovered() && c.isVisible()){
				h.setHovered(false);
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		/*This will detect recursively (thanks to UIContainer.getChildrenAt)
		every child container that is clickable and are on the right spot. Then trigger their click
		method from the highest layer to the lower (except the current layer, and again recursively).*/
		
		Iterator<Clickable> it;
		if(layerConstrained) {
			it = currentLayer.getChildrenAt(e.getX(), e.getY()).filter(UIContainer::isVisible)
				.takeWhile(c -> c instanceof Clickable).map(c -> (Clickable) c).iterator();
		} else {
			it = currentLayer.getDeepChildren().filter(UIContainer::isVisible)
				.takeWhile(c -> c instanceof Clickable)
				.filter(c -> c.isInside(e.getX(), e.getY())).map(c -> (Clickable) c)
				.iterator();
		}
		boolean stop = false;
		while(it.hasNext() && !stop) {
			stop = it.next().click(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), e.getClickCount());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		dragStartX = e.getX();
		dragStartY = e.getY();
		previousDragX = e.getX();
		previousDragY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(dragging) dragging = false;
		draggingContainers.forEach(d -> d.stopDragging(e.getX(), e.getY()));
		draggingContainers.clear();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		int dx = e.getX() - previousDragX;
		int dy = e.getY() - previousDragY;
		
		dragging = true;
		
		Iterator<Dragable> it;
		if(layerConstrained) {
			it = currentLayer.getChildrenAt(dragStartX, dragStartY).filter(UIContainer::isVisible)
					.takeWhile(c -> c instanceof Dragable).map(c -> (Dragable) c).iterator();
		} else {
			it = currentLayer.getDeepChildren().takeWhile(c -> c instanceof Dragable).filter(UIContainer::isVisible)
				.filter(c -> c.isInside(dragStartX, dragStartY))
				.map(c -> (Dragable) c).iterator();
		}
		
		boolean stop = false;
		while(it.hasNext() && !stop) {
			Dragable drag = it.next();
			if(!draggingContainers.contains(drag)) draggingContainers.add(drag);
			stop = drag.drag(dragStartX, dragStartY, e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), dx, dy);
		}
		
		previousDragX = e.getX();
		previousDragY = e.getY();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(layerConstrained) {
			currentLayer.getChildrenAt(previousDragX, previousDragY).filter(UIContainer::isVisible)
			.filter(c -> c instanceof MouseWheelSensitive).map(c -> (MouseWheelSensitive) c)
			.findFirst().ifPresent(d -> d.mouseWheelMoved(e.getX(), e.getY(), e.getUnitsToScroll()));		
		} else {
			currentLayer.getDeepChildren().filter(c -> c instanceof MouseWheelSensitive)
				.filter(c -> c.isInside(previousDragX, previousDragY))
				.map(c -> (MouseWheelSensitive) c)
				.findFirst().ifPresent(d -> d.mouseWheelMoved(e.getX(), e.getY(), e.getUnitsToScroll()));
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	/**
	 * Sets the currentLayer to be shown and used for events such as clicks and hover.
	 * @param currentLayer The layer to be shown. Likely a whole screen layer.
	 */
	public void setCurrentLayer(UILayer currentLayer) {
		if(currentLayer == null) return;
		this.currentLayer = currentLayer;
	}
	
	/**
	 * Get the current layer shown on this window.
	 * @return The currentLayer.
	 */
	public UILayer getCurrentLayer() {
		return currentLayer;
	}
	
	/**
	 * Sets whether or not a component should be clickable or dragable even if it's not inside it's parent layer.
	 * @param layerConstrained true if it should be limited to it's parent layer.
	 */
	public void setLayerConstrained(boolean layerConstrained) {
		this.layerConstrained = layerConstrained;
	}
	
	public void setCursor(Cursor cursor) {
		frame.setCursor(cursor);
	}
	
	/**
	 * @return true if it should be limited to it's parent layer.
	 */
	public boolean isLayerConstrained() {
		return layerConstrained;
	}

	public void addKeyboardCallback(KeyListener listener) {
		canvas.addKeyListener(listener);
	}
	
	public int getMouseX() {
		return mouseX;
	}
	
	public int getMouseY() {
		return mouseY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
