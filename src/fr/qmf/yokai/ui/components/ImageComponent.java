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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.ui.UIComponent;
import fr.qmf.yokai.ui.UILayer;

public class ImageComponent extends UIComponent {

	protected Image image;
	
	protected boolean resize;
	
	/**
	 * Constructs an ImageComponent that shows the specified image.
	 * @param layer the parent layer of this ImageComponent
	 * @param image the image to be shown
	 * @param x the x position of this ImageComponent
	 * @param y the y position of this ImageComponent
	 */
	public ImageComponent(UILayer layer, BufferedImage image, int x, int y) {
		super(layer, x, y, image.getWidth(), image.getHeight());
		this.image = image;
	}
	
	/**
	 * Constructs an ImageComponent that shows an image cropping it if necessary.
	 * @param layer the parent layer of this ImageComponent
	 * @param image the image to be shown
	 * @param x the x position of this ImageComponent
	 * @param y the y position of this ImageComponent
	 * @param width the width of this ImageComponent
	 * @param height the height of this ImageComponent
	 */
	public ImageComponent(UILayer layer, Image image, int x, int y, int width, int height) {
		super(layer, x, y, width, height);
		this.image = image;
	}

	@Override
	public void draw(Graphics g) {
		if(resize) {
			g.drawImage(image, x, y, width, height, null);
		} else {
			g.drawImage(image, x, y, x+width, y+height, 0, 0, width, height, null);
		}
	}
	
	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public boolean isResize() {
		return resize;
	}
	
	/**
	 * Should the image be resized to fit the size or not.
	 * @param resize
	 */
	public void setResize(boolean resize) {
		this.resize = resize;
	}

}
