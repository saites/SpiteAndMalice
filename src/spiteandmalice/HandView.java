package spiteandmalice;

import Cards.CardSlot;
import Cards.Card;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

/**
 *
 * @author saites
 */
public class HandView extends JPanel implements 
        GUIGameModel.GUIGameModelListener,
        SMCardSlotGroupController.CardSlotGroupInterface {
    GUIGameModel model;
    SMCardSlot [] cardSlots;
    GameInstance.Player player;
    private static final int X_OFFSET = 5;
    private static final int X_TRANSLATE = 5;
    private static final int Y_TRANSLATE = 5;
    private AffineTransform invertMouse;
    
    public HandView(GUIGameModel model, GameInstance.Player p) {
        this.model = model;
        model.registerObserver(this);
        cardSlots = new SMCardSlot[model.numCardsInHand];
        Card [] cards = model.getPlayerHand(p);
        for(int i = 0; i < cardSlots.length; i++) {
            cardSlots[i] = new SMCardSlot(cards[i], SMCardSlot.SlotType.HAND, i);
        }
        this.player = p;
    }
    
    private void updateCardSlots() {
        Card [] cards = model.getPlayerHand(player);
        for(int i = 0; i < cardSlots.length; i++) {
            cardSlots[i].setCard(cards[i]);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform saveTransform = g2.getTransform();
        
        Dimension actualSize = getPreferredSize();
        g2.translate((getWidth()-actualSize.width)/2 + X_TRANSLATE, (getHeight()-actualSize.height)/2 + Y_TRANSLATE);
        try {
            invertMouse = g2.getTransform().createInverse();
        } catch (NoninvertibleTransformException ex) {
            System.err.println(ex);
        }
        updateCardSlots();
        for(CardSlot cs : cardSlots) {
            if(model.getSelected() == cs) {
                g2.setXORMode(Color.GREEN);
            }
            cs.draw(g);
            g2.setPaintMode();
            g2.translate(cs.WIDTH+X_OFFSET, 0);
        }
        g2.setTransform(saveTransform);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                (CardSlot.WIDTH + X_OFFSET)*cardSlots.length-X_OFFSET+1+(X_TRANSLATE*2),
                CardSlot.HEIGHT+(Y_TRANSLATE*2));
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public void stateChanged() {
        repaint();
    }

    @Override
    public void selectedChanged() {
        repaint();
    }

    @Override
    public SMCardSlot getCardSlotFromCoordinates(int x, int y) {
        Point2D invertPoint = invertMouse.transform(new Point2D.Double(x, y), null);
        x = (int)invertPoint.getX();
        if(x < X_TRANSLATE
            || ((x-X_TRANSLATE) % (CardSlot.WIDTH + X_OFFSET)) > CardSlot.WIDTH) {
                return null;
        }
        int selected = x / (CardSlot.WIDTH + X_OFFSET);
        if(selected > model.numCardsInHand-1) { return null; }
        return cardSlots[selected];
    }

    @Override
    public void toggleSelection(SMCardSlot cs) {
        model.toggleSelected(cs);
    }
}
