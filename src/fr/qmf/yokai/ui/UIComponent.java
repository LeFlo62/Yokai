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
 * Basically a UIContainer contained in a UILayer
 * Drawing something is mandatory, thus a UIComponent should be
 * visible, contrary to a UILayer.
 */
public abstract class UIComponent extends UIContainer {
	
	protected UILayer layer;

	public UIComponent(UILayer layer, int x, int y, int width, int height) {
		super(layer, x, y, width, height);
		this.layer = layer;
	}
	
	/**
	 * The Layer this UIComponent is in.
	 * @return The Layer this UIComponent is in.
	 */
	public UILayer getLayer() {
		return layer;
	}

}
