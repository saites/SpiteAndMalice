package spiteandmalice;

import Cards.Deck;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import spiteandmalice.GameInstance.Player;

/**
 *
 * @author saites
 */
public class OptionsWindow extends JPanel {
    GUIGameModel model;
    
    int computerSpeed;
    int numCardsInPayoff;
    int numCardsInHand;
    int numCenterStacks;
    int numSideStacks;
    Deck jokersCantBe;
    
    JLabel speedLabel;
    JSlider speedSlider;
    JLabel payoffLabel;
    JSlider payoffSlider;
    JLabel handLabel;
    JSlider handSlider;
    JLabel sideLabel;
    JSlider sideSlider;
    JLabel centerLabel;
    JSlider centerSlider;
    public OptionsWindow(GUIGameModel model, SMComputerPlayer computer) {
        this.model = model;
        
        speedLabel = new JLabel("Computer Speed:");
        speedSlider = new JSlider(0, 100, computer.getSpeed());
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        
        payoffLabel = new JLabel("Payoff Size:");
        payoffSlider = new JSlider(1, 26, model.numCardsInPayoff);
        payoffSlider.setMajorTickSpacing(4);
        payoffSlider.setPaintTicks(true);
        payoffSlider.setPaintLabels(true);
        
        handLabel = new JLabel("Hand Size:");
        handSlider = new JSlider(1, 12, model.numCardsInHand);
        handSlider.setMajorTickSpacing(1);
        handSlider.setPaintTicks(true);
        handSlider.setPaintLabels(true);
        
        sideLabel = new JLabel("Number of Side Stacks:");
        sideSlider = new JSlider(1, 8, model.numSideStacks);
        sideSlider.setMajorTickSpacing(1);
        sideSlider.setPaintTicks(true);
        sideSlider.setPaintLabels(true);
        
        centerLabel = new JLabel("Number of Center Stacks:");
        centerSlider = new JSlider(1, 8, model.numCenterStacks);
        centerSlider.setMajorTickSpacing(1);
        centerSlider.setPaintTicks(true);
        centerSlider.setPaintLabels(true);
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(speedLabel);
        add(speedSlider);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(payoffLabel);
        add(payoffSlider);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(handLabel);
        add(handSlider);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(sideLabel);
        add(sideSlider);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(centerLabel);
        add(centerSlider);
    }
    
    class OptionsController implements GameInstance.GameInterface {

        @Override
        public void stateChanged() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void handChanged(Player p) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void payoffChanged(Player p) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void sideStackChanged(Player p) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void centerChanged() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }

    public int getComputerSpeed() {
        return computerSpeed;
    }

    public int getNumCardsInPayoff() {
        return numCardsInPayoff;
    }

    public int getNumCardsInHand() {
        return numCardsInHand;
    }

    public int getNumCenterStacks() {
        return numCenterStacks;
    }

    public int getNumSideStacks() {
        return numSideStacks;
    }
    
    
}
