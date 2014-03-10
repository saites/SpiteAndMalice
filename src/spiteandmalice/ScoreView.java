package spiteandmalice;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import spiteandmalice.GameInstance.Player;

/**
 *
 * @author saites
 */
public class ScoreView extends JPanel implements GameInstance.GameInterface {
    GameInstance model;
    JLabel p1Score = new javax.swing.JLabel();
    JLabel p2Score = new javax.swing.JLabel();
        
    public ScoreView(GameInstance model) {
        this.model = model;
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        p1Score.setAlignmentX(Box.CENTER_ALIGNMENT);
        p2Score.setAlignmentX(Box.CENTER_ALIGNMENT);
        add(Box.createHorizontalGlue());
        add(p1Score);
        add(Box.createRigidArea(new Dimension(25, 0)));
        add(p2Score);
        add(Box.createHorizontalGlue());
        setScores();
    }
    
    public final void setScores() {
        p1Score.setText("P1: " + model.getPayoffs()[0].getSize());
        p2Score.setText("P2: " + model.getPayoffs()[1].getSize());
    }
    
    @Override
    public void stateChanged() {
        setScores();
    }

    @Override
    public void handChanged(Player p) {}

    @Override
    public void payoffChanged(Player p) {}

    @Override
    public void sideStackChanged(Player p) {}

    @Override
    public void centerChanged() {}

}
