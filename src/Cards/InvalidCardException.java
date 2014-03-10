package Cards;

/**
 *
 * @author saites
 */
public class InvalidCardException extends Exception {
    private int value;
    public InvalidCardException(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "Invalid value " + value + ". Must be between 0 and 14.";
    }

}
