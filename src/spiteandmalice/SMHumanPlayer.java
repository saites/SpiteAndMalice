package spiteandmalice;

import spiteandmalice.GameInstance.Player;

/**
 *
 * @author saites
 */
public class SMHumanPlayer implements GUIGameModel.GUIGameModelListener{
    private Player player;
    private GUIGameModel model;
    
    public SMHumanPlayer(GUIGameModel model, Player player) {
        this.player = player;
        this.model = model;
    }
    
    @Override
    public void stateChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void selectedChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
