package spiteandmalice;

import Cards.CardSlot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
public class PayoffView extends JPanel implements 
        GUIGameModel.GUIGameModelListener, 
        SMCardSlotGroupController.CardSlotGroupInterface {
    GUIGameModel model;
    SMCardSlot cardSlot;
    GameInstance.Player player;
    private static final int X_TRANSLATE = 5;
    private static final int Y_TRANSLATE = 5;
    public static final Font FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    private AffineTransform invertMouse;
    
    public PayoffView(GUIGameModel model, GameInstance.Player p) {
        this.model = model;
        model.registerObserver(this);
        cardSlot = new SMCardSlot(model.getPlayerPayoff(p).peek(), SMCardSlot.SlotType.PAYOFF, 0);
        this.player = p;
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
        cardSlot.setCard(model.getPlayerPayoff(player).peek());
        if(model.getSelected() == cardSlot) {
            g2.setXORMode(Color.GREEN);
        }
        cardSlot.draw(g);
        g2.setPaintMode();
        Font saveFont = g2.getFont();
        g2.setFont(FONT);
        FontMetrics m = g.getFontMetrics(FONT);
        String number = "" + model.getPayoffs()[player.pn].getSize();
        int fontHeight = m.getAscent()-10;
        int fontWidth = m.stringWidth(number);
        g2.drawString(number, (actualSize.width-fontWidth-X_TRANSLATE)/2, (actualSize.height+fontHeight-Y_TRANSLATE)/2);
        
        g2.setFont(saveFont);
        g2.setTransform(saveTransform);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CardSlot.WIDTH+(X_TRANSLATE*2), CardSlot.HEIGHT+(Y_TRANSLATE*2));
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
        y = (int)invertPoint.getY();
        x+=X_TRANSLATE;
        y+=Y_TRANSLATE;
        if(x < CardSlot.WIDTH && y < CardSlot.HEIGHT) {
            return cardSlot;
        } else {
            return null;
        }
    }

    @Override
    public void toggleSelection(SMCardSlot cs) {
        model.toggleSelected(cs);
    }
}
