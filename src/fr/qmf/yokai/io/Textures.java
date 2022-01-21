package fr.qmf.yokai.io;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import fr.qmf.yokai.Main;

/**
 * Textures loads textures from the jar when needed and caches them for later use.
 * @author LeFlo
 *
 */
public class Textures {
	
	private static Map<String, BufferedImage> textures = new HashMap<>();
	
	public static BufferedImage getTexture(String path) {
		if(!textures.containsKey(path)) {
			try {
				BufferedImage image = ImageIO.read(Main.class.getResourceAsStream("/assets/textures/" + path + ".png"));
				textures.put(path, image);
				return image;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return textures.get(path);
	}

}
