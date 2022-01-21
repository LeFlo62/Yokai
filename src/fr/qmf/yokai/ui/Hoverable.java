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

/**
 * Indicates something may have an action when hovered with mouse.
 * 
 * @author LeFlo
 *
 */
public interface Hoverable {
	
	/**
	 * Fires everytime the mouse move ontop of this.
	 * 
	 * @param screenX The X position on the screen.
	 * @param screenY The Y position on the screen.
	 * @param x The X position on the window.
	 * @param y The Y position on the window.
	 */
	public void hover(int screenX, int screenY, int x, int y);
	
	/**
	 * Is this being hovered ?
	 * @return true if this is hovered.
	 */
	public boolean isHovered();
	
	/**
	 * Sets whether or not this is hovered.
	 * @param hovered Should it be hovered right now ?
	 */
	public void setHovered(boolean hovered);

}
