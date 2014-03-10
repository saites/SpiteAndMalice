package spiteandmalice;

import Cards.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author saites
 */
public class GameInstance {
    public final static int P1 = 0;
    public final static int P2 = 1;
    public final static int numPlayers = 2; //CANNOT CHANGE THIS NUMBER WITHOUT ALTERING CODE!
    public final static int numDecks = 2; //CANNOT CHANGE THIS NUMBER WITHOUT ALTERING CODE!
    public final int numCardsInHand;
    public final int numSideStacks;
    public final int numCenterStacks;
    public final Deck jokersCantBe;
    public final int numCardsInPayoff;
    private ArrayList<GameInterface> observers = new ArrayList<>();
    private Deck stock;
    private Deck [] payoffs = new Deck[numPlayers];
    private Deck [][] sideStacks;
    private Deck [] centerStacks;
    private Card [][] hands;
    
    
    public int getNumCardsInHand() {
        return numCardsInHand;
    }

    public int getNumSideStacks() {
        return numSideStacks;
    }

    public int getNumCenterStacks() {
        return numCenterStacks;
    }
    
    
        
    public Deck getJokersCantBe() {
        return jokersCantBe;
    }
    public boolean jokerCanBe(Card c) {
        for(Card card : jokersCantBe) {
            if(c.getValue() == card.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    public Deck getStock() {
        return stock;
    }
    
    public Deck [] getPayoffs() {
        return payoffs;
    }

    public Deck[][] getSideStacks() {
        return sideStacks;
    }

    public Deck[] getCenterStacks() {
        return centerStacks;
    }

    public Card[][] getHands() {
        return hands;
    }
    
    public Card [] getPlayerHand(Player p) {
        return getHands()[p.pn];
    }
    public Deck getPlayerPayoff(Player p) {
        return payoffs[p.pn];
    }
    public Deck [] getPlayerSideStack(Player p) {
        return sideStacks[p.pn];
    }
    
    public GameInstance(int numSideStacks, int numCenterStacks, 
            int numCardsInHand, Deck jokersCantBe, int numCardsInPayoff) 
            throws tooManyPayoffCardsException {
        if(numCardsInPayoff > 26) {
            throw new tooManyPayoffCardsException(numCardsInPayoff);
        }
        
        this.numSideStacks = numSideStacks;
        this.numCenterStacks = numCenterStacks;
        this.numCardsInHand = numCardsInHand;
        this.jokersCantBe = jokersCantBe;
        this.numCardsInPayoff = numCardsInPayoff;
        
        sideStacks = new Deck[numPlayers][numSideStacks];
        centerStacks = new Deck[numCenterStacks];
        hands = new Card[numPlayers][numCardsInHand];
        
        payoffs[P1] = Deck.createWithoutJokers();
        payoffs[P1].shuffle();
        payoffs[P2] = payoffs[P1].cut(numCardsInPayoff);
        stock = Deck.createWithJokers();
        stock.pushBack(Card.createJoker());
        stock.pushBack(Card.createJoker());
        stock.shuffle();
        
        for(int i = 0; i < numSideStacks; i++) {
            sideStacks[P1][i] = Deck.createEmptyDeck();
            sideStacks[P2][i] = Deck.createEmptyDeck();
        }
        for(int i = 0; i < numCenterStacks; i++) {
            centerStacks[i] = Deck.createEmptyDeck();
        }
        for(int i = 0; i < numCardsInHand; i++) {
            hands[P1][i] = stock.pop();
            hands[P2][i] = stock.pop();
        }
    }
    public boolean dealFromStock(Player p) {
        if(stock.isEmpty()) { return false; }
        boolean retval = false;
        for(int i = 0; i < hands[p.pn].length; i++) {
            Card c = hands[p.pn][i];
            if(c != null) { continue; }
            retval = true;
            hands[p.pn][i] = stock.pop();
        }
        notifyHandChanged(p);
        return retval;
    }
    
    public boolean playFromPayoff(Player p, int centerNum) {
        if(centerNum < 0 || centerNum > centerStacks.length) {
            return false;
        }
        boolean retval = playTopCard(payoffs[p.pn], centerNum);
        if(retval) { 
            notifyPayoffChanged(p);
        }
        return retval;
    }
    
    public boolean playFromStack(Player p, int centerNum, int stackNum) {
        if(stackNum < 0 || centerNum < 0 || stackNum > sideStacks[p.pn].length 
                || centerNum > centerStacks.length) {
            return false;
        }
        boolean retval = playTopCard(sideStacks[p.pn][stackNum], centerNum);
        if(retval) { 
            notifySideStackChanged(p);
        }
        return retval;
    }
    
    public boolean playFromHand(Player p, int centerNum, int handNum) {
        if(centerNum < 0 || handNum < 0 || centerNum > centerStacks.length
                || handNum > numCardsInHand) {
            return false;
        }
        Card c = hands[p.pn][handNum];
        if(c == null) { return false; }
        boolean retval = playCard(c, centerNum);
        if(retval) {
            hands[p.pn][handNum] = null;
            boolean noCards = true;
            for(Card card : hands[p.pn]) {
                if(card != null) {
                    noCards = false;
                    break;
                }
            }
            if(noCards) {
                for(int i = 0; i < numCardsInHand; i++) {
                    dealFromStock(p);
                }
            }
            notifyHandChanged(p);
        }
        return retval;
    }
    
    public boolean stackFromHand(Player p, int stackNum, int handNum) {
        if(stackNum < 0 || handNum < 0 || stackNum > sideStacks[p.pn].length 
                || handNum > hands[p.pn].length) {
            return false;
        }
        Card c = hands[p.pn][handNum];
        if(c == null) { return false; }
        sideStacks[p.pn][stackNum].push(c);
        hands[p.pn][handNum] = null;
        notifySideStackChanged(p);
        return true;
    }
    
    private boolean playTopCard(Deck d, int centerNum) {
        Card c;
        if((c = d.peek()) == null) { return false; }
        boolean retval = playCard(c, centerNum);
        if(retval) { d.pop(); }
        return retval;
    }
    
    private boolean playCard(Card c, int centerNum) {
        Card stackTop = centerStacks[centerNum].peek();
        if(c.getValue() == Card.JOKER) {
            try {
                if(stackTop == null) {
                    c.setValue(Card.ACE);
                } else {
                    c.setValue(stackTop.getValue() + 1);
                }
                if(!jokerCanBe(c)) {
                    c.setValue(Card.JOKER);
                    return false;
                }
            } catch (InvalidCardException ex) {
                System.err.println(ex);
            }
        } else {
            if(stackTop != null && c.getValue() != stackTop.getValue() + 1) {
                return false; 
            } else if(stackTop == null && c.getValue() != Card.ACE) {
                return false;
            }
        }
        centerStacks[centerNum].push(c);
        if(centerStacks[centerNum].getSize() == 13) {
            centerStacks[centerNum].resetJokers();
            stock.pushBackAll(centerStacks[centerNum]);
            centerStacks[centerNum].clear();
            stock.shuffle();
        }
        notifyCenterChanged();
        return true;
    }
    
    public static interface GameInterface {
        public abstract void stateChanged();
        public abstract void handChanged(Player p);
        public abstract void payoffChanged(Player p);
        public abstract void sideStackChanged(Player p);
        public abstract void centerChanged();
    }
    
    public void registerObserver(GameInterface gi) {
        observers.add(gi);
    }
    public boolean removeObserver(GameInterface gi) {
        return observers.remove(gi);
    }
    
    private void notifyStateChanged() {
        for(GameInterface gi : observers) {
            gi.stateChanged();
        }
    }
    
    private void notifyHandChanged(Player p) {
        for(GameInterface gi : observers) {
            gi.handChanged(p);
            gi.stateChanged();
        }
    }
    private void notifyPayoffChanged(Player p) {
        for(GameInterface gi : observers) {
            gi.payoffChanged(p);
            gi.stateChanged();
        }
    }
    private void notifySideStackChanged(Player p) {
        for(GameInterface gi : observers) {
            gi.sideStackChanged(p);
            gi.stateChanged();
        }
    }
    private void notifyCenterChanged() {
        for(GameInterface gi : observers) {
            gi.centerChanged();
            gi.stateChanged();
        }
    }
    
    public static enum Player {
        PLAYER1(P1),
        PLAYER2(P2);
        
        public int pn;
        private Player(int pn) {
            this.pn = pn;
        }
    }
    public static enum PileType {
        SIDESTACK,
        CENTER,
        STOCK,
        PAYOFF,
        HAND,
        NONE;
    }
}
