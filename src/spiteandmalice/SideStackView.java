package spiteandmalice;

import Cards.CardSlot;
import Cards.Deck;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import spiteandmalice.SMCardSlotGroupController.CardSlotGroupInterface;

/**
 *
 * @author saites
 */
public class SideStackView extends JPanel implements 
        GUIGameModel.GUIGameModelListener, CardSlotGroupInterface {
    GUIGameModel model;
    SMCardSlot [][] cardSlots;
    int biggestDeck = 0;
    GameInstance.Player player;
    private static final int X_OFFSET = 5;
    private static final int Y_OFFSET = 23;
    private static final int MAX_CARDS = 4;
    private static final int X_TRANSLATE = 5;
    private static final int Y_TRANSLATE = 5;
    private AffineTransform invertMouse;
    
    public SideStackView(GUIGameModel model, GameInstance.Player p) {
        this.model = model;
        model.registerObserver(this);
        cardSlots = new SMCardSlot[model.numSideStacks][MAX_CARDS];
        this.player = p;
        for(int i = 0; i < cardSlots.length; i++) {
            for(int j = 0; j < MAX_CARDS; j++) {
                cardSlots[i][j] = new SMCardSlot(null,
                        SMCardSlot.SlotType.SIDESTACK, i);
            }
        }
        updateCardSlots();
    }
    
    private void updateCardSlots() {
        Deck [] decks = model.getPlayerSideStack(player);
        for(int i = 0; i < cardSlots.length; i++) {
            for(int j = 0; j < MAX_CARDS; j++) {
                cardSlots[i][j].setCard(
                        decks[i].getCardAt(Math.min(decks[i].getSize(), MAX_CARDS)-j-1));
            }
        }
    }

    @Override
    public SMCardSlot getCardSlotFromCoordinates(int x, int y) {
        Point2D invertPoint = invertMouse.transform(new Point2D.Double(x, y), null);
        x = (int)invertPoint.getX();
        if(x < X_TRANSLATE
            || ((x-X_TRANSLATE) % (SMCardSlot.WIDTH + X_OFFSET)) > CardSlot.WIDTH) {
                return null;
            }
        int selected = x / (CardSlot.WIDTH + X_OFFSET);
        if(selected > model.numSideStacks-1) { return null; }
        SMCardSlot retval = null;
        if(model.getSelected() == null || model.getSelected().getSlotType() != SMCardSlot.SlotType.HAND) {
            //return first non-empty card slot
            for(int i = MAX_CARDS-1; i >= 0; i--) {
                if(cardSlots[selected][i].getCard() != null) {
                    retval = cardSlots[selected][i];
                    break;
                }
            }
        } else {
            //return first empty card slot
            retval = cardSlots[selected][0];
        }
        
        return retval;       
    }

    @Override
    public void toggleSelection(SMCardSlot cs) {
        model.toggleSelected(cs);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform saveTransform = g2.getTransform();
        Dimension actualSize = getPreferredSize();
        
        if(player == GameInstance.Player.PLAYER2) {
            g2.rotate(Math.PI, (getWidth())/2, (getHeight())/2);
        }
        g2.translate(((getWidth()-actualSize.width)/2 + X_TRANSLATE), ((getHeight()-actualSize.height)/2 + Y_TRANSLATE));
        try {
            invertMouse = g2.getTransform().createInverse();
        } catch (NoninvertibleTransformException ex) {
            System.err.println(ex);
        }
        updateCardSlots();
        for(CardSlot [] cs : cardSlots) {
            if(model.getSelected() == cs[0]) {
                g2.setXORMode(Color.GREEN);
            }
            cs[0].draw(g);
            g2.setPaintMode();
            g2.translate(0, Y_OFFSET);
            int i;
            for(i = 1; i < MAX_CARDS; i++) {
                CardSlot c = cs[i];
                if(c.getCard() == null) { break; }
                if(model.getSelected() == c) {
                    g2.setXORMode(Color.GREEN);
                }
                c.draw(g);
                g2.setPaintMode();
                g2.translate(0, Y_OFFSET);
            }
            g2.translate((CardSlot.WIDTH+X_OFFSET), -1*Y_OFFSET*i);
        }
        g2.setTransform(saveTransform);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                (CardSlot.WIDTH + X_OFFSET)*cardSlots.length-X_OFFSET+(X_TRANSLATE*2),
                (Y_OFFSET * MAX_CARDS)+CardSlot.HEIGHT+(Y_TRANSLATE*2));
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
}
