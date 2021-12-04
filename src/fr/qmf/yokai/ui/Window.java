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
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 *	Represents a window shown and opened on the user screen.
 *	The dimensions are fixed and cannot be changed.
 *	A future improvements would be to be able to change them. 
 */
public class Window implements MouseListener, MouseMotionListener, MouseWheelListener {
	public static final int WIDTH = 1280, HEIGHT = 720;
	
	private String title;
	
	private JFrame frame;
	private BufferedImage screen;
	
	private Canvas canvas;
	private BufferStrategy bufferStrategy;
	private Graphics graphics;

	private UILayer currentLayer;
	
	private int previousDragX, previousDragY;
	private boolean dragging;
	
	private boolean layerConstrained = false;

	
	public Window(String title) {
		this.title = title;
		this.currentLayer = new EmptyLayer(this);
		
		initHandle();
	}
    
	/**
	 * Creates everything needed to have a window.
	 */
	private void initHandle() {
		screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		canvas.setSize(WIDTH, HEIGHT);
		
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
		
		frame.setVisible(true);
		
		frame.add(canvas);
		
		canvas.requestFocus();
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		graphics = bufferStrategy.getDrawGraphics();
	}
	
	/**
	 * Clears the screen.
	 */
	public void clear() {
		Graphics g = screen.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	}
	
	/**
	 * Draws the screen from the currentLayer onto this window.
	 */
	public void draw() {
		if(currentLayer == null) return;
		Graphics g = screen.getGraphics();
		
		currentLayer.draw(g);
		
		graphics.drawImage(screen, 0, 0, screen.getWidth(), screen.getHeight(), null);
		
		bufferStrategy.show();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		currentLayer.getDeepChildren()
		.filter(c -> c instanceof Hoverable)
		.forEach(c -> {
			if(c.isInside(e.getX(), e.getY())) {
				((Hoverable)c).setHovered(true);
				((Hoverable)c).hover(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY());
			} else {
				((Hoverable)c).setHovered(false);
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		/*This will detect recursively (thanks to UIContainer.getChildrenAt)
		every child container that is clickable and are on the right spot. Then trigger their click
		method from the highest layer to the lower (except the current layer, and again recursively).*/
		
		if(layerConstrained) {
			currentLayer.getChildrenAt(e.getX(), e.getY()).forEachOrdered(c -> System.out.println(c.getClass().getCanonicalName()));
			
			currentLayer.getChildrenAt(e.getX(), e.getY())
				.takeWhile(c -> c instanceof Clickable).map(c -> (Clickable) c)
				.forEach(c -> c.click(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), e.getClickCount()));
		} else {
			currentLayer.getDeepChildren()
				.takeWhile(c -> c instanceof Clickable)
				.filter(c -> c.isInside(e.getX(), e.getY())).map(c -> (Clickable) c)
				.forEach(c -> c.click(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), e.getClickCount()));	
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		previousDragX = e.getX();
		previousDragY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(dragging) dragging = false;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - previousDragX;
		int dy = e.getY() - previousDragY;
		
		dragging = true;
		
		if(layerConstrained) {
			currentLayer.getChildrenAt(previousDragX, previousDragY)
			.filter(c -> c instanceof Dragable).map(c -> (Dragable) c)
			.findFirst().ifPresent(d -> d.drag(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), dx, dy));		
		} else {
			currentLayer.getDeepChildren().filter(c -> c instanceof Dragable)
				.filter(c -> c.isInside(previousDragX, previousDragY))
				.map(c -> (Dragable) c)
				.findFirst().ifPresent(d -> d.drag(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), dx, dy));
		}
		
//		currentLayer.getChildren()
//		.filter(c -> c.isInside(previousDragX, previousDragY))
//		.flatMap(c -> c.getChildrenAt(previousDragX, previousDragY))
//		.filter(c -> c instanceof Dragable).map(c -> (Dragable) c)
//		.findFirst()
//		.ifPresent(d -> d.drag(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), dx, dy));
		
//		currentLayer.getChildrenMap().entrySet().stream()
//			.sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey()))
//			.flatMap(e1 -> e1.getValue().stream())
//			.filter(c -> c.isInside(previousDragX, previousDragY))
//			.flatMap(c -> c.getChildrenAt(previousDragX, previousDragY))
//			.filter(c -> c instanceof Dragable).map(c -> (Dragable) c)
//			.findFirst().ifPresent(d -> d.drag(e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), dx, dy));
		
		previousDragX = e.getX();
		previousDragY = e.getY();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(layerConstrained) {
			currentLayer.getChildrenAt(previousDragX, previousDragY)
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
	
}
