package spiteandmalice;

import Cards.Card;
import Cards.Deck;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import spiteandmalice.GameInstance.Player;

/**
 *
 * @author saites
 */
public class SMComputerPlayer implements ActionListener, 
        GUIGameModel.GUIGameModelListener {

    private GUIGameModel model;
    private Timer t;
    private GameInstance.Player player;
    private int speed;
    
    public void setSpeed(int value) {
        speed = value;
        t.setDelay(speed);
    }
    public int getSpeed() {
        return speed;
    }

    public SMComputerPlayer(GUIGameModel model, GameInstance.Player player) {
        t = new Timer(250, this);
        this.player = player;
        this.model = model;
    }

    public void computerTakeTurn() {
        if(!t.isRunning()) {
            t.start();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!makeComputerMove()) {
            t.stop();
            computerStackCard();
            if(player == Player.PLAYER2) {
                model.changeTurn(Player.PLAYER1);
            } else {
                model.changeTurn(Player.PLAYER2);
            }
        }
    }
    
    private boolean stackSameCard() {
        Deck[] sideStacks = model.getSideStacks()[player.pn];
        Card[] hand = model.getHands()[player.pn];
        
        for (int i = 0; i < model.numSideStacks; i++) {
            for (int j = 0; j < model.numCardsInHand; j++) {
                if (sideStacks[i].peek() != null && hand[j] != null
                        && sideStacks[i].peek().getValue() == hand[j].getValue()) {
                    boolean otherCards = false;
                    for (int k = 0; k < sideStacks[i].getSize(); k++) {
                        if (sideStacks[i].getCardAt(k).getValue() != hand[j].getValue()) {
                            otherCards = true;
                            break;
                        }
                    }
                    if (otherCards) {
                        continue;
                    }
                    model.stackFromHand(player, i, j);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean stackEmptyStack() {
        Deck[] sideStacks = model.getSideStacks()[player.pn];
        Card[] hand = model.getHands()[player.pn];
        
        for (int i = 0; i < model.numSideStacks; i++) {
            if (sideStacks[i].peek() == null) {
                for (int j = 0; j < model.numCardsInHand; j++) {
                    if (hand[j] != null) {
                        model.stackFromHand(player, i, j);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean stackOnLarger() {
        Deck[] sideStacks = model.getSideStacks()[player.pn];
        Card[] hand = model.getHands()[player.pn];
        
        int higherStack = -1, lowerHand = -1, distance = Integer.MAX_VALUE;
        for (int i = 0; i < model.numSideStacks; i++) {
            for (int j = 0; j < model.numCardsInHand; j++) {
                if (sideStacks[i].peek() != null && hand[j] != null
                        && sideStacks[i].peek().getValue() - hand[j].getValue() > 1
                        && sideStacks[i].peek().getValue() - hand[j].getValue() < distance) {
                    distance = sideStacks[i].peek().getValue() - hand[j].getValue();
                    higherStack = i;
                    lowerHand = j;
                }
            }
        }
        
        if (higherStack != -1) {
            model.stackFromHand(player, higherStack, lowerHand);
            return true;
        } else {
            return false;
        }
    }
    
    private int laststack = 0;
    private void stackOnNextStack() {
         for (int i = 0; i < model.numCardsInHand; i++) {
            if (model.getHands()[player.pn][i] != null) {
                model.stackFromHand(player, laststack++, i);
                laststack %= model.numSideStacks;
            }
        }
    }

    private void computerStackCard() {
        if(!stackSameCard() && !stackEmptyStack() && !stackOnLarger()) {
            stackOnNextStack(); 
        }
    }

    private boolean computerPlayFromStack() {
        boolean retval = false;
        for (int i = 0; i < model.numSideStacks; i++) {
            for (int j = 0; j < model.numCenterStacks; j++) {
                if (model.playFromStack(player, j, i)) {
                    retval = true;
                    break;
                }
            }
            if (retval) {
                break;
            }
        }
        return retval;
    }

    private boolean computerPlayFromHand() {
        boolean retval = false;
        for (int i = 0; i < model.numCardsInHand; i++) {
            for (int j = 0; j < model.numCenterStacks; j++) {
                if (model.playFromHand(player, j, i)) {
                    retval = true;
                    break;
                }
            }
            if (retval) {
                break;
            }
        }
        return retval;
    }

    private boolean computerPlayPayoff() {
        boolean retval = false;
        for (int i = 0; i < model.numCenterStacks; i++) {
            if (model.playFromPayoff(player, i)) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    private boolean makeComputerMove() {
        if (computerPlayPayoff()) {
            return true;
        }
        if (computerPlayFromHand()) {
            return true;
        }
        if (computerPlayFromStack()) {
            return true;
        }
        return false;
    }

    @Override
    public void stateChanged() {
        if(model.getTurn() == player) {
            computerTakeTurn();
        }
    }

    @Override
    public void selectedChanged() {}
}
