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
 * Indicates something may have an action when the mouse is dragging on top of it.
 * @author LeFlo
 *
 */
public interface Dragable {
	
	/**
	 * Fires when the mouse move while dragging (pressing a left click button)
	 * 
	 * @param dragStartX The X position the drag started at on the window.
	 * @param dragStartY The Y position the drag started at on the window.
	 * @param screenX The X position of the mouse on the screen.
	 * @param screenY The Y position of the mouse on the screen.
	 * @param x The X position of the mouse on the window.
	 * @param y The Y position of the mouse on the window.
	 * @param dx The amount of pixel on the x-axis the mouse moved since last call of this method.
	 * @param dy The amount of pixel on the y-axis the mouse moved since last call of this method.
	 * @return true if no further action should be processed and this is a terminal operation.
	 */
	public boolean drag(int dragStartX, int dragStartY, int screenX, int screenY, int x, int y, int dx, int dy);
	
	public void stopDragging(int stopDragX, int stopDragY);

}
