package Cards;

import Cards.Card.Suit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author saites
 */
public class Deck implements Iterable<Card> {
    ArrayList<Card> deck;
    
    public static Deck createWithJokers() {
        return new Deck(54);
    }
    public static Deck createWithoutJokers() {
        return new Deck(52);
    }
    public static Deck createEmptyDeck() {
        return new Deck(0);
    }
    
    private Deck(int size) {
        deck = new ArrayList<>(size);
        if(size == 0) { return; }
        
        for(Suit s : Card.Suit.values()) {
            if(s == Suit.JOKER) { continue; }
            for(int j = Card.ACE; j < Card.JOKER; j++) {
                try {
                    deck.add(new Card(j, s));
                } catch (InvalidCardException ex) {
                    System.err.println(ex);
                }
            }
        }
        if(size == 54) {
            deck.add(Card.createJoker());
            deck.add(Card.createJoker());
        }
    }
    
    public void printDeck() {
        for(Card c : deck) {
            System.out.println(c);
        }
    }
    
    @Override
    public String toString() {
        String retval = "";
        for(Card c : deck) {
            retval += c.toString();
        }
        return retval;
    }
    
    public Card peek() {
        if(deck.isEmpty()) { return null; }
        return deck.get(0);
    }
    
    public Card pop() {
        if(deck.isEmpty()) { return null; }
        Card c = deck.get(0);
        deck.remove(0);
        return c;
    }
    
    public boolean isEmpty() {
        return deck.isEmpty();
    }
    
    public Card getCardAt(int i) {
        return (i >= 0 && i < deck.size()) ? deck.get(i) : null;
    }
    
    public void pushBack(Card c) {
        deck.add(c);
    }
    
    public void push(Card c) {
        deck.add(0, c);
    }
    
    public void insertAt(int index, Card c) {
        deck.add(index, c);
    }
    
    public void pushBackAll(Deck d) {
        deck.addAll(d.deck);
    }
    
    public void clear() {
        deck.clear();
    }
    
    public void resetJokers() {
        for(Card c : deck) {
            if(c.getSuit() == Suit.JOKER) {
                try {
                    c.setValue(Card.JOKER);
                } catch (InvalidCardException ex) {
                    System.err.println(ex);
                }
            }
        }
    }
    
    public Deck cut(int where) {
        Deck d = createEmptyDeck();
        d.deck.addAll(deck.subList(where, deck.size()));
        deck.removeAll(d.deck);
        return d;
    }
    
    public boolean removeCard(Card c) {
        return deck.remove(c);
    }
    
    /**
     * Removes the first instance of the card with this value of this suit from
     * the deck. This uses a simple linear search, with the assumption that 
     * decks are typically not very large. Use "removeCard" to remove a card for
     * which you have the pointer
     * @param value the value of the card
     * @param suit the suit of the card; Suit.JOKER if a joker
     * @return true if the card was found and removed; false otherwise.
     */
    public boolean removeCardByName(int value, Suit suit) {
        boolean retval = false;
        for(Card c : deck) {
            if(c.getValue() == value && c.getSuit() == suit) {
                retval = deck.remove(c);
                break;
            }
        }
        return retval;
    }
    
    public int getSize() {
        return deck.size();
    }
    
    public void shuffle() {
        Random rgen = new Random();
        int size = deck.size();
        int base[] = new int[size];
        int rnum;
        ArrayList<Card> newDeck = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            base[i] = i;
        }

        for (int i = 0; i < size; i++) {
            rnum = rgen.nextInt(size - i);
            newDeck.add(deck.get(base[rnum]));
            base[rnum] = base[size - i - 1];
        }
        
        deck = newDeck;
    }

    @Override
    public Iterator<Card> iterator() {
         return deck.iterator();
    }
}
