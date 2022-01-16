package fr.qmf.yokai.game.gui.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.Card;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.YokaiType;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;

public class HintsLayer extends UILayer {

	private YokaiGame game;
	private int maxCardX, minCardY;
	
	private boolean draggingHint;
	private byte hintDragged; // Hint being dragged.
	
	private float hintFlippingAdvance;
	private int oldSize;

	private double xCardOffset, yCardOffset; // Mouse offset from upper-left card corner in pixels.
	
	private int hoverCardX, hoverCardY; // Card being hovered while dragging.
	
	private static final float FLIPPING_TIME = 0.5f;
	
	public static final int DEFAULT_HINT_SIZE = 100;
	public static final int HINT_MARGIN = 18;
	public static final int UNDISCOVERED_HINT_Y_OFFSET = 8;
	public static final int HINT_ROWS = 2;
	public static final int HINT_DECK_X_OFFSET = 2;
	public static final int HINT_DECK_Y_OFFSET = 1;

	public HintsLayer(YokaiGame game, Window window, UILayer parent) {
		super(window, parent, 0,0,0,0);
		this.game = game;
	}
	
	@Override
	public void draw(Graphics2D g) {
		GameStorage storage = game.getGameStorage();
		
		int[] edges = game.getGameStorage().detectGameDeckEdges();
		minCardY = edges[1];
		maxCardX = edges[2];

		BufferedImage back = Textures.getTexture("hints/back");
		
		for(int d = -(storage.getHints().length-storage.getDiscoveredHints().size()-storage.getPlacedHints().size())+1; d <= 0; d++) {
			g.drawImage(back, (maxCardX+HINT_DECK_X_OFFSET)*(DEFAULT_HINT_SIZE + HINT_MARGIN),
					UNDISCOVERED_HINT_Y_OFFSET*d+(minCardY)*(DEFAULT_HINT_SIZE + HINT_MARGIN),
					DEFAULT_HINT_SIZE,
					DEFAULT_HINT_SIZE, null);
		}
		
		for(int i = 0; i < storage.getDiscoveredHints().size(); i++) {
			byte hint = storage.getDiscoveredHints().get(i).byteValue();
			
			if(draggingHint && hint == hintDragged) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			}
			
			YokaiType[] yokaiTypes = YokaiType.getYokaiFromHint(hint);
			BufferedImage texture = Textures.getTexture("hints/" + YokaiType.getYokaisString(yokaiTypes));
			
			double x = (maxCardX+HINT_DECK_X_OFFSET+i%HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
			double y = (minCardY+HINT_DECK_Y_OFFSET+i/HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
			
			double width = DEFAULT_HINT_SIZE;
			
			if(i == storage.getDiscoveredHints().size()-1) {
				if(oldSize != storage.getDiscoveredHints().size()) {
					if(oldSize < storage.getDiscoveredHints().size()) {
						hintFlippingAdvance = 0f;
					}
					oldSize = storage.getDiscoveredHints().size();
				}
				if(hintFlippingAdvance < 1) {
					if(hintFlippingAdvance <= 0.5) {
						texture = back;
					}
					
					x = hintFlippingAdvance*(maxCardX+HINT_DECK_X_OFFSET+i%HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					x += (1-hintFlippingAdvance)*(maxCardX+HINT_DECK_X_OFFSET)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					
					x += DEFAULT_HINT_SIZE*(-Math.abs(hintFlippingAdvance-0.5) + 0.5);
					
					y = hintFlippingAdvance*(minCardY+HINT_DECK_Y_OFFSET+i/HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					y += (1-hintFlippingAdvance)*(minCardY)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					
					width *= Math.abs(1-2*hintFlippingAdvance);
					
					hintFlippingAdvance += 1/(FLIPPING_TIME*game.getTargetFPS());
				}
			}
			
			g.drawImage(texture, (int)x,
					(int)y,
					(int) width,
					DEFAULT_HINT_SIZE, null);
		
			if(draggingHint && hint == hintDragged) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}
		}
		
		if(draggingHint) {
			BufferedImage texture = Textures.getTexture("hints/" + YokaiType.getYokaisString(YokaiType.getYokaiFromHint(hintDragged)));
			
			GameLayer gameLayer = (GameLayer) parent;
			int dragCardSizeDelta = 7;
			int shadowOffset = 5;
			
			int hoverAlpha = (int)(28+100d*Math.abs(Math.sin((double)(System.currentTimeMillis())/500d)));
			Color hoverColor = new Color(255,255,255,hoverAlpha);
			if(!storage.isInsideBoard(hoverCardX, hoverCardY) || storage.getBoard()[hoverCardY][hoverCardX] == null || storage.getBoard()[hoverCardY][hoverCardX].hasHint()) {
				hoverColor = new Color(255,45,45,hoverAlpha);
			}
			
			g.setColor(hoverColor);
			g.fillRect(hoverCardX*(DEFAULT_HINT_SIZE + HINT_MARGIN), hoverCardY*(DEFAULT_HINT_SIZE + HINT_MARGIN), DEFAULT_HINT_SIZE, DEFAULT_HINT_SIZE);
			
			g.setTransform(new AffineTransform());
			
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRoundRect((int)(window.getMouseX()-(xCardOffset+dragCardSizeDelta+shadowOffset)*gameLayer.getZoom()),
					(int)(window.getMouseY()-(yCardOffset+dragCardSizeDelta+shadowOffset)*gameLayer.getZoom()),
								(int)((DEFAULT_HINT_SIZE)*gameLayer.getZoom()), (int)((DEFAULT_HINT_SIZE)*gameLayer.getZoom()), 4*shadowOffset, 4*shadowOffset);
			
			g.drawImage(texture, (int)(window.getMouseX()-(xCardOffset+dragCardSizeDelta)*gameLayer.getZoom()),
					(int)(window.getMouseY()-(yCardOffset+dragCardSizeDelta)*gameLayer.getZoom()),
								(int)((DEFAULT_HINT_SIZE+2*dragCardSizeDelta)*gameLayer.getZoom()), (int)((DEFAULT_HINT_SIZE+2*dragCardSizeDelta)*gameLayer.getZoom()), null);
		
		}
	}
	
	public boolean isDragingHint() {
		return draggingHint;
	}
	
	public void setXCardOffset(double xCardOffset) {
		this.xCardOffset = xCardOffset;
	}
	
	public void setYCardOffset(double yCardOffset) {
		this.yCardOffset = yCardOffset;
	}
	
	public void setDraggingHint(boolean dragingHint) {
		this.draggingHint = dragingHint;
	}
	
	public void setHoverCardX(int hoverCardX) {
		this.hoverCardX = hoverCardX;
	}
	public void setHoverCardY(int hoverCardY) {
		this.hoverCardY = hoverCardY;
	}
	public void setHintDragged(byte hintDragged) {
		this.hintDragged = hintDragged;
	}
	public byte getHintDragged() {
		return hintDragged;
	}

}
