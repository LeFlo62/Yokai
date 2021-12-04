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
 * Basically a UIContainer with the power<br/>
 * to have children, and have a background.<br/>
 * Drawing something is not mendatoy.<br/>
 * <br/>
 * This should be used to represent a GUI, whether it be<br/>
 * for a whole screen GUI or not.<br/>
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
		children.entrySet().stream().sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey())).flatMap(e -> e.getValue().stream()).filter(UIContainer::isVisible).forEach(c -> c.draw(g));
	}
	
	public void drawBackground(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(x, y, width, height);
	}
	
	public void add(UIContainer container) {
		this.add(0, container);
	}
	
	/**
	 * Adds the specified container to the layer's bank.<br/>
	 * Each layer has it's list containing the UIContainers.<br/>
	 * The layer number indicates the draw order as well as the logical order.<br/>
	 * The bigger it is, the nearer it is drawn. So a layer number of 0 is drawn before
	 * a layer number of 4. And a click action is performed first on a layer number of
	 * 4 and then on a layer number of 0.<br/>
	 * <br/>
	 * Be aware that UILayer may have a UILayer inside them. Then the logic is that<br/>
	 * while performing an action it will be on layer n, if there is a UILayer with multiple<br/>
	 * UIContainer at different layers, it will draw every layers of this UILayer before going at<br/>
	 * the next layer n+1. It is a depth first search algorithm.<br/>
	 * 
	 * @param layer The layer number. The bigger, the nearer.
	 * @param container The containter to add.
	 */
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
