package spiteandmalice;

/**
 *
 * @author saites
 */
class tooManyPayoffCardsException extends Exception {
    private int numCardsInPayoff;
    
    public tooManyPayoffCardsException(int numCardsInPayoff) {
        this.numCardsInPayoff = numCardsInPayoff;
    }

    @Override
    public String toString() {
        return "Attempted to make payoff with too many cards: " + numCardsInPayoff + " must be less than 26";
    }
}
