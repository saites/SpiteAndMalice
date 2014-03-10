package spiteandmalice;

import Cards.Card;
import Cards.CardSlot;
import Cards.Deck;
import Cards.InvalidCardException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import spiteandmalice.GameInstance.Player;

/**
 *
 * @author saites
 */
public class SpiteAndMalice {
    
    public SpiteAndMalice() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });       
    }
    
    class GameBoard extends JPanel implements GameInstance.GameInterface {
        private GUIGameModel model;
        SMComputerPlayer computer;
        private CardSlot cs = new CardSlot();
        
        private class GameMenu extends JPanel {
            JMenuBar menuBar;
            JMenu menu;
            JMenuItem menuItem;
            
            public GameMenu() {
                setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                menuBar = new JMenuBar();
                add(menuBar);
                add(Box.createHorizontalGlue());
                
                menu = new JMenu("File");
                menu.setMnemonic(KeyEvent.VK_F);
                menuBar.add(menu);
                
                menuItem = new JMenuItem("New Game",
                             KeyEvent.VK_N);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        resetGame();
                    }
                });
                menu.add(menuItem);
                
                menu = new JMenu("Edit");
                menu.setMnemonic(KeyEvent.VK_E);
                menuBar.add(menu);
                
                menuItem = new JMenuItem("Options...",
                        KeyEvent.VK_O);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_O, ActionEvent.CTRL_MASK));
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFrame frame = new JFrame("Options");
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        OptionsWindow options = new OptionsWindow(model, computer);
                        frame.add(options);
                        frame.pack();
                        frame.setVisible(true);
                    }
                });
                menu.add(menuItem);
            }
        }
        
        public GameBoard() {
            GameMenu menuPanel = new GameMenu();
            
            Card c;
            Deck d = Deck.createEmptyDeck();
            try {
                d.push(new Card(Card.ACE, Card.Suit.CLUBS));
                d.push(new Card(2, Card.Suit.CLUBS));
                d.push(new Card(7, Card.Suit.CLUBS));
            } catch (InvalidCardException ex) {
                System.err.println(ex);
                return;
            }
            try {
                model = new GUIGameModel(4, 4, 5, d, 26);
            } catch (tooManyPayoffCardsException ex) {
                System.err.println(ex);
                return;
            }
            
            HandView hand = new HandView(model, GameInstance.Player.PLAYER1);
            hand.addMouseListener(new SMCardSlotGroupController(hand));
            PayoffView [] payoffs = new PayoffView[2];
            payoffs[0] = new PayoffView(model, GameInstance.Player.PLAYER1);
            payoffs[1] = new PayoffView(model, GameInstance.Player.PLAYER2);
            payoffs[0].addMouseListener(new SMCardSlotGroupController(payoffs[0]));
            SideStackView [] sideStackViews = new SideStackView[2];
            sideStackViews[0] = new SideStackView(model, GameInstance.Player.PLAYER1);
            sideStackViews[1] = new SideStackView(model, GameInstance.Player.PLAYER2);
            sideStackViews[0].addMouseListener(new SMCardSlotGroupController(sideStackViews[0]));
            CenterView centerview = new CenterView(model);
            centerview.addMouseListener(new SMCardSlotGroupController(centerview));
            centerview.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

            JPanel Player1Views = new JPanel(new BorderLayout());
            Player1Views.add(BorderLayout.WEST, hand);
            hand.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            Player1Views.add(BorderLayout.EAST, payoffs[0]);
            payoffs[0].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            Player1Views.add(BorderLayout.NORTH, sideStackViews[0]);
            sideStackViews[0].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

            JPanel ComputerViews = new JPanel(new BorderLayout());
            ComputerViews.add(BorderLayout.EAST, payoffs[1]);
            payoffs[1].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            ComputerViews.add(BorderLayout.SOUTH, sideStackViews[1]);
            sideStackViews[1].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

            ScoreView score = new ScoreView(model);
            score.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            model.registerObserver(score);

            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            add(menuPanel);
            add(Box.createVerticalGlue());
            add(ComputerViews);
            add(score);
            add(centerview);
            add(Player1Views);
            add(Box.createVerticalGlue());
            
            computer = new SMComputerPlayer(model, Player.PLAYER2);
            model.registerObserver(computer);
            //SMComputerPlayer computer2 = new SMComputerPlayer(model, Player.PLAYER1);
            //model.registerObserver(computer2);
            
            model.registerObserver(this);
            repaint();
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int baseVal = Math.min(getWidth(), (int)(getHeight()/1.7));
            cs.setWidth((baseVal/6)-10);
            cs.setHeight((int)(((baseVal/6)-10)*(3.0/2.0)));
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 800);
        }

        Player wasturn = Player.PLAYER1;
        boolean resetting = false;
        @Override
        public void stateChanged() {
            repaint();
            if(resetting) { return; }
            if(model.getPayoffs()[Player.PLAYER1.pn].getSize() == 0) {
                JOptionPane.showMessageDialog(this,
                    "You won!",
                    "Congratulations!",
                    JOptionPane.PLAIN_MESSAGE);
                resetting = true;
                resetGame();
            } else if(model.getPayoffs()[Player.PLAYER2.pn].getSize() == 0) {
                JOptionPane.showMessageDialog(this,
                    "You lost!",
                    "You Suck!",
                    JOptionPane.PLAIN_MESSAGE);
                resetting = true;
                resetGame();
            }
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
    
    protected void resetGame() {
        frame.remove(gb);
        gb = new GameBoard();
        frame.add(gb);
        frame.pack();
        frame.setVisible(true);
    }

    GameBoard gb = new GameBoard();
    JFrame frame = new JFrame("Spite And Malice");
    protected void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gb);
        frame.pack();
        frame.setVisible(true);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpiteAndMalice s = new SpiteAndMalice();
    }
}
