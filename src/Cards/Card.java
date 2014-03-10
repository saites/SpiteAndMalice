package Cards;

/**
 *
 * @author saites
 */
public class Card {
    private int value;
    private Suit suit;
    public final static int ACE = 1;
    public final static int JACK = 11;
    public final static int QUEEN = 12;
    public final static int KING = 13;
    public final static int JOKER = 14;
    
    public static enum Suit {
        HEARTS,
        SPADES,
        DIAMONDS,
        CLUBS,
        JOKER;
        
        @Override
        public String toString() {
            switch(this) {
                case CLUBS:
                    return "\u2663";
                case HEARTS:
                    return "\u2665";
                case DIAMONDS:
                    return "\u2666";
                case SPADES:
                    return "\u2660";
                default:
                    return "";
            }
        }
    }
    
    public Card(int value, Suit suit) throws InvalidCardException {
        if(value < 1 || value > 14) {
            throw new InvalidCardException(value);
        }
        if(value == 14) { suit = Suit.JOKER; }
        this.value = value;
        this.suit = suit;
    }
    
    public static Card createJoker() {
        try {
            return new Card(JOKER, Suit.JOKER);
        } catch (InvalidCardException ex) {
            System.err.println(ex);
            return null;
        }
    }
    
    public int getValue() {
        return value;
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    public void setValue(int value) throws InvalidCardException {
        if(value < 1 || value > 14) {
            throw new InvalidCardException(value);
        }
        this.value = value;
        if(value == 14) { suit = Suit.JOKER; }
    }

    @Override
    public String toString() {
        switch (value) {
            case ACE:
                return "A" + suit.toString();
            case JACK:
                return "J" + suit.toString();
            case QUEEN:
                return "Q" + suit.toString();
            case KING:
                return "K" + suit.toString();
            case JOKER:
                return "JOKER";
            default:
                return value + suit.toString();
        }
    }
}
