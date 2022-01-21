package fr.qmf.yokai.game.gui.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import fr.qmf.yokai.YokaiGame;
import fr.qmf.yokai.game.GameController;
import fr.qmf.yokai.game.GameStorage;
import fr.qmf.yokai.game.YokaiType;
import fr.qmf.yokai.io.Textures;
import fr.qmf.yokai.ui.UILayer;
import fr.qmf.yokai.ui.Window;

public class HintsLayer extends UILayer {
	private static final float FLIPPING_TIME = 0.5f;
	
	public static final int DEFAULT_HINT_SIZE = 100;
	public static final int HINT_MARGIN = 18;
	public static final int UNDISCOVERED_HINT_Y_OFFSET = 8;
	public static final int HINT_ROWS = 2;
	public static final int HINT_DECK_X_OFFSET = 2;
	public static final int HINT_DECK_Y_OFFSET = 1;

	private YokaiGame game;
	private GameController controller;


	public HintsLayer(YokaiGame game, GameController controller, Window window, UILayer parent) {
		super(window, parent, 0,0,0,0);
		this.game = game;
		this.controller = controller;
	}
	
	@Override
	public void draw(Graphics2D g) {
		GameStorage storage = game.getGameStorage();
		
		controller.detectGameDeckEdges();
		
		BufferedImage back = Textures.getTexture("hints/back");
		
		for(int d = -(storage.getHints().length-storage.getDiscoveredHints().size()-storage.getPlacedHints().size())+1; d <= 0; d++) {
			g.drawImage(back, (controller.getMaxCardX()+HINT_DECK_X_OFFSET)*(DEFAULT_HINT_SIZE + HINT_MARGIN),
					UNDISCOVERED_HINT_Y_OFFSET*d+(controller.getMinCardY())*(DEFAULT_HINT_SIZE + HINT_MARGIN),
					DEFAULT_HINT_SIZE,
					DEFAULT_HINT_SIZE, null);
		}
		
		for(int i = 0; i < storage.getDiscoveredHints().size(); i++) {
			byte hint = storage.getDiscoveredHints().get(i).byteValue();
			
			if(controller.isDraggingHint() && hint == controller.getHintDragged()) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			}
			
			YokaiType[] yokaiTypes = YokaiType.getYokaiFromHint(hint);
			BufferedImage texture = Textures.getTexture("hints/" + YokaiType.getYokaisString(yokaiTypes));
			
			double x = (controller.getMaxCardX()+HINT_DECK_X_OFFSET+i%HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
			double y = (controller.getMinCardY()+HINT_DECK_Y_OFFSET+i/HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
			
			double width = DEFAULT_HINT_SIZE;
			
			if(i == storage.getDiscoveredHints().size()-1) {
				if(controller.getOldSize() != storage.getDiscoveredHints().size()) {
					if(controller.getOldSize() < storage.getDiscoveredHints().size()) {
						controller.setHintFlippingAdvance(0f);
					}
					controller.setOldSize(storage.getDiscoveredHints().size());
				}
				if(controller.getHintFlippingAdvance() < 1) {
					if(controller.getHintFlippingAdvance() <= 0.5) {
						texture = back;
					}
					
					x = controller.getHintFlippingAdvance()*(controller.getMaxCardX()+HINT_DECK_X_OFFSET+i%HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					x += (1-controller.getHintFlippingAdvance())*(controller.getMaxCardX()+HINT_DECK_X_OFFSET)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					
					x += DEFAULT_HINT_SIZE*(-Math.abs(controller.getHintFlippingAdvance()-0.5) + 0.5);
					
					y = controller.getHintFlippingAdvance()*(controller.getMinCardY()+HINT_DECK_Y_OFFSET+i/HINT_ROWS)*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					y += (1-controller.getHintFlippingAdvance())*(controller.getMinCardY())*(DEFAULT_HINT_SIZE + HINT_MARGIN);
					
					width *= Math.abs(1-2*controller.getHintFlippingAdvance());
					
					controller.setHintFlippingAdvance((float) (controller.getHintFlippingAdvance() + 1/(FLIPPING_TIME*game.getTargetFPS())));
				}
			}
			
			g.drawImage(texture, (int)x,
					(int)y,
					(int) width,
					DEFAULT_HINT_SIZE, null);
		
			if(controller.isDraggingHint() && hint == controller.getHintDragged()) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}
		}
		
		if(controller.isDraggingHint()) {
			BufferedImage texture = Textures.getTexture("hints/" + YokaiType.getYokaisString(YokaiType.getYokaiFromHint(controller.getHintDragged())));
			
			int dragCardSizeDelta = 7;
			int shadowOffset = 5;
			
			int hoverAlpha = (int)(28+100d*Math.abs(Math.sin((double)(System.currentTimeMillis())/500d)));
			Color hoverColor = new Color(255,255,255,hoverAlpha);
			if(!storage.isInsideBoard(controller.getHoverCardX(), controller.getHoverCardY()) || storage.getBoard()[controller.getHoverCardY()][controller.getHoverCardX()] == null || storage.getBoard()[controller.getHoverCardY()][controller.getHoverCardX()].hasHint()) {
				hoverColor = new Color(255,45,45,hoverAlpha);
			}
			
			g.setColor(hoverColor);
			g.fillRect(controller.getHoverCardX()*(DEFAULT_HINT_SIZE + HINT_MARGIN), controller.getHoverCardY()*(DEFAULT_HINT_SIZE + HINT_MARGIN), DEFAULT_HINT_SIZE, DEFAULT_HINT_SIZE);
			
			g.setTransform(new AffineTransform());
			
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRoundRect((int)(window.getMouseX()-(controller.getXCardOffset()+dragCardSizeDelta+shadowOffset)*controller.getZoom()),
					(int)(window.getMouseY()-(controller.getYCardOffset()+dragCardSizeDelta+shadowOffset)*controller.getZoom()),
								(int)((DEFAULT_HINT_SIZE)*controller.getZoom()), (int)((DEFAULT_HINT_SIZE)*controller.getZoom()), 4*shadowOffset, 4*shadowOffset);
			
			g.drawImage(texture, (int)(window.getMouseX()-(controller.getXCardOffset()+dragCardSizeDelta)*controller.getZoom()),
					(int)(window.getMouseY()-(controller.getYCardOffset()+dragCardSizeDelta)*controller.getZoom()),
								(int)((DEFAULT_HINT_SIZE+2*dragCardSizeDelta)*controller.getZoom()), (int)((DEFAULT_HINT_SIZE+2*dragCardSizeDelta)*controller.getZoom()), null);
		
		}
	}
	
}
