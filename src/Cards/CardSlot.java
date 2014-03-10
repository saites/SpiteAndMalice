package Cards;

import Cards.Card.Suit;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author saites
 */
public class CardSlot {
    Card card;
    public int x = 0, y = 0;
    public static int WIDTH = 60;
    public static int HEIGHT = 90;
    public static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final int FONT_X_OFFSET = 5;
    public static final int FONT_Y_OFFSET = 5;
    public int fontSize;
    
    public void setWidth(int width) {
        WIDTH = width;
    }
    public void setHeight(int height) {
        HEIGHT = height;
    }
    
    public CardSlot(Card c) {
        this.card = c;
    }
    public CardSlot() {
        this.card = null;
    }
    
    public Card getCard() {
        return card;
    }
    public void setCard(Card c) {
        this.card = c;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        RoundRectangle2D roundedRectangle = 
                new RoundRectangle2D.Float(x, y, WIDTH, HEIGHT, 10, 10);
        Color saveColor = g2.getColor();
        
        if(card != null) {
            g2.setColor(Color.WHITE);
            g2.fill(roundedRectangle);
        }
        
        g2.setColor(Color.BLACK);
        g2.draw(roundedRectangle);
        
        if(card == null) {
            g2.setColor(saveColor);
            return;
        }

        g2.setFont(FONT);
        FontMetrics m = g.getFontMetrics(FONT);
        fontSize = m.getHeight();
        String symbol = card.toString();
        
        if(card.getSuit() == Suit.CLUBS || card.getSuit() == Suit.SPADES) {
            g2.setColor(Color.BLACK);
        } else if(card.getSuit() == Suit.HEARTS || card.getSuit() == Suit.DIAMONDS) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.BLUE);
        }
        g2.drawString(symbol, FONT_X_OFFSET, FONT_Y_OFFSET + m.getHeight());
        
        AffineTransform saveTransform = g2.getTransform();
        g2.rotate(Math.PI, WIDTH/2, HEIGHT/2);
        g2.drawString(symbol, FONT_X_OFFSET, FONT_Y_OFFSET + m.getHeight());
        g2.setTransform(saveTransform);
        
        g2.setColor(saveColor);
    }
}
