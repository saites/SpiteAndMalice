package spiteandmalice;

import Cards.CardSlot;
import Cards.Deck;
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
public class CenterView extends JPanel implements 
        GUIGameModel.GUIGameModelListener,
        SMCardSlotGroupController.CardSlotGroupInterface { 
    GUIGameModel model;
    SMCardSlot [] cardSlots;
    private static final int X_OFFSET = 5;
    private static final int X_TRANSLATE = 5;
    private static final int Y_TRANSLATE = 5;
    private AffineTransform invertMouse;

    public CenterView(GUIGameModel model) {
        this.model = model;
        model.registerObserver(this);
        cardSlots = new SMCardSlot[model.numCenterStacks];
        Deck [] stacks = model.getCenterStacks();
        for(int i = 0; i < stacks.length; i++) {
            Deck d = stacks[i];
            cardSlots[i] = new SMCardSlot(d.peek(), SMCardSlot.SlotType.CENTER, i);
        }
    }
    
    private void updateCardSlots() {
        Deck [] stacks = model.getCenterStacks();
        for(int i = 0; i < stacks.length; i++) {
            Deck d = stacks[i];
            cardSlots[i].setCard(d.peek());
        }
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
        if(selected > model.numCenterStacks-1) { return null; }
        return cardSlots[selected];
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
            cs.draw(g);
            g2.translate(CardSlot.WIDTH+X_OFFSET, 0);
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
        //repaint();
    }    

    @Override
    public void toggleSelection(SMCardSlot cs) {
        model.toggleSelected(cs);
    }
}
