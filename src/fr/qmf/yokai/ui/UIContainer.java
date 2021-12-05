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

import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Simplest element that represent a space that may or may not have children.
 * To add a child, use a UILayer.
 */
public abstract class UIContainer {
	
	protected UIContainer parent;
	
	protected int x, y, width, height;
	
	private boolean visible = true;
	
	protected Map<Integer, List<UIContainer>> children = new HashMap<>();

	public UIContainer(int x, int y, int width, int height) {
		this(null, x, y, width, height);
	}
	
	public UIContainer(UIContainer parent, int x, int y, int width, int height) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public abstract void draw(Graphics g);
	
	/**
	 * @param x
	 * @param y
	 * @return true if and only if (x, y) are inside this UIContainer.
	 */
	public boolean isInside(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}
		
	/**
	 * Computes recursively the children at this (x,y) coordinates.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return an empty stream if (x,y) is not in UIContainer, a stream of itself if it has no children, or a stream of itself concatenated with it's children at (x,y) oredered by layers.
	 */
	public Stream<UIContainer> getChildrenAt(int x, int y){
		if(!isInside(x, y)) return Stream.empty();
		if(children.size() == 0) return Stream.of(this);
		return Stream.concat(children.entrySet().stream().sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey())).flatMap(e -> e.getValue().stream()).filter(e -> e.isInside(x, y)).flatMap(e -> e.getChildrenAt(x, y)), Stream.of(this));
	}
	
	/**
	 * Gets all the direct children of this UIContainer.
	 * 
	 * @return a stream of itself if it has no children, or a stream of itself concatenated with it's children ordered by layers.
	 */
	public Stream<UIContainer> getChildren(){
		if(children.size() == 0) return Stream.of(this);
		return Stream.concat(children.entrySet().stream().sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey())).flatMap(e -> e.getValue().stream()), Stream.of(this));
	}
	
	/**
	 * Gets all the children of this UIContainer.
	 * Children of children are included.
	 * 
	 * @return a stream of itself if it has no children, or a stream of itself concatenated with it's children and children children ordered by layers.
	 */
	public Stream<UIContainer> getDeepChildren() {
		if(children.size() == 0) return Stream.of(this);
		return Stream.concat(children.entrySet().stream().sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey())).flatMap(e -> e.getValue().stream()).flatMap(e -> e.getDeepChildren()), Stream.of(this));
	}
	
	/**
	 * The map where every child is stored by layer.
	 * 
	 * @return the children map. Key is the layer number, and a list of UIContainer associated with it.
	 */
	public Map<Integer, List<UIContainer>> getChildrenMap(){
		return children;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public boolean isVisible() {
		return (parent == null || parent.isVisible()) && visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
