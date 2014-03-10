package spiteandmalice;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author saites
 */
public class SMCardSlotGroupController extends MouseAdapter {
    CardSlotGroupInterface csgi;
    
    public interface CardSlotGroupInterface {
        public abstract SMCardSlot getCardSlotFromCoordinates(int x, int y);
        public abstract void toggleSelection(SMCardSlot cs);
    }
    
    public SMCardSlotGroupController(CardSlotGroupInterface csgi) {
        this.csgi = csgi;
    }
    
    @Override 
    public void mousePressed(MouseEvent e) {
        SMCardSlot cs = csgi.getCardSlotFromCoordinates(e.getX(), e.getY());
        if(cs != null) {
            csgi.toggleSelection(cs);
        }
    }
}

