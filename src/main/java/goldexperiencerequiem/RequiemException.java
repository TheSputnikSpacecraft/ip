package goldexperiencerequiem;

/**
 * Represents exceptions specific to the Requiem chatbot.
 */
public class RequiemException extends Exception {
    /**
     * Creates a new RequiemException with the specified message.
     *
     * @param message The error message to be displayed.
     */
    public RequiemException(String message) {
        super(message);
    }
}
