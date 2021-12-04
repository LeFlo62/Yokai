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

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Basically a UIContainer with the power
 * to have children, and have a background.
 * Drawing something is not mendatoy.
 * 
 * This should be used to represent a GUI, whether it be
 * for a whole screen GUI or not.
 */
public class UILayer extends UIContainer {
	
	protected Window window;
	
	public UILayer(Window window, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.window = window;
	}
	
	@Override
	public void draw(Graphics g) {
		drawBackground(g);
		//Draw children
		drawChildren(g);
	}
	
	protected final void drawChildren(Graphics g) {
		children.entrySet().stream().sorted((e1,e2) -> e2.getKey().compareTo(e1.getKey())).flatMap(e -> e.getValue().stream()).filter(UIContainer::isVisible).forEach(c -> c.draw(g));
	}
	
	public void drawBackground(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(x, y, width, height);
	}
	
	public void add(UIContainer container) {
		this.add(0, container);
	}
	
	public void add(int layer, UIContainer container) {
		if(container == null || this.equals(container)) return; //Prevent infinite loop while drawing.
		if(!children.containsKey(layer)) {
			children.put(layer, new ArrayList<>());
		}
		children.get(layer).add(container);
	}
	
	public Window getWindow() {
		return window;
	}

}
