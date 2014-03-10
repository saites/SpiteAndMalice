package spiteandmalice;

import Cards.Card;
import Cards.CardSlot;

/**
 *
 * @author saites
 */
public class SMCardSlot extends CardSlot {
    private SlotType slotType;
    private int slotNumber;
    
    public SlotType getSlotType() {
        return slotType;
    }
    public int getSlotNumber() {
        return slotNumber;
    }
        
    public SMCardSlot(SlotType slotType, int slotNumber) {
        super();
        this.slotType = slotType;
        this.slotNumber = slotNumber;
    }
    public SMCardSlot(Card c, SlotType slotType, int slotNumber) {
        this(slotType, slotNumber);
        this.setCard(c);
    }
    
    public static enum SlotType {
        SIDESTACK,
        CENTER,
        STOCK,
        PAYOFF,
        HAND,
        NONE;
    }
}
