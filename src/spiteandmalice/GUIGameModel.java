package spiteandmalice;

import Cards.Deck;
import java.util.ArrayList;

/**
 *
 * @author saites
 */
public class GUIGameModel extends GameInstance implements GameInstance.GameInterface{
    private ArrayList<GUIGameModelListener> listeners = new ArrayList<>();
    private SMCardSlot selected = null;
    public Player turn = Player.PLAYER1;
    
    public Player getTurn() {
        return turn;
    }
    
    public GUIGameModel(int numSideStacks, int numCenterStacks, 
            int numCardsInHand, Deck jokersCantBe, int numCardsInPayoff) 
            throws tooManyPayoffCardsException {
        super(numSideStacks, numCenterStacks, numCardsInHand, jokersCantBe,
                numCardsInPayoff);
    }
    
    public SMCardSlot getSelected() {
        return selected;
    }
    private void select(SMCardSlot cs) {
        if(selected != null && selected.getCard() != null) {
            if(moveCard(selected, cs)) {
                selected = null;
                notifyStateChanged();
            } else {
                selected = cs;
            }
        } else {
            selected = cs;
        }
    }
    private void unselect() {
        selected = null;
    }
    public void toggleSelected(SMCardSlot cs) {
        if(turn != Player.PLAYER1) { return; }
        if(selected == cs) {
            unselect();
        } else {
            select(cs);
        }
        notifySelectedChanged();
    }
    
    public void changeTurn(Player player) {
        turn = player;
        dealFromStock(turn);
        notifyStateChanged();
    }
     
    private boolean moveCard(SMCardSlot from, SMCardSlot to) {
        boolean retval = false;
        switch (to.getSlotType()) {
            case SIDESTACK:
                if(from.getSlotType() != SMCardSlot.SlotType.HAND) { break; }
                retval = stackFromHand(Player.PLAYER1, to.getSlotNumber(), from.getSlotNumber());
                changeTurn(Player.PLAYER2);
                break;
            case CENTER:
                switch(from.getSlotType()) {
                    case SIDESTACK:
                        retval = playFromStack(Player.PLAYER1, to.getSlotNumber(), from.getSlotNumber());
                        break;
                    case HAND:
                        retval = playFromHand(Player.PLAYER1, to.getSlotNumber(), from.getSlotNumber());
                        break;
                    case PAYOFF:
                        retval = playFromPayoff(Player.PLAYER1, to.getSlotNumber());
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return retval;
    }

    @Override
    public void stateChanged() {
        notifyStateChanged();
    }

    @Override
    public void handChanged(Player p) {}

    @Override
    public void payoffChanged(Player p) {}

    @Override
    public void sideStackChanged(Player p) {}

    @Override
    public void centerChanged() {}
    
    public interface GUIGameModelListener {
        public abstract void stateChanged();
        public abstract void selectedChanged();
    }
    
    private void notifySelectedChanged() {
        for(GUIGameModelListener gi : listeners) {
            gi.selectedChanged();
        }
    }
    private void notifyStateChanged() {
        for(GUIGameModelListener gi : listeners) {
            gi.stateChanged();
        }
    }
    public void registerObserver(GUIGameModelListener gi) {
        listeners.add(gi);
    }
    public void removeObserver(GUIGameModelListener gi) {
        listeners.remove(gi);
    }
}
