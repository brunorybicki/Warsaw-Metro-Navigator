package ui;

/**
 * Thrown when the user's input ends, for example, after Ctrl+D or Ctrl+Z, before a valid choice was made.
 * It has its own type so that the application can end calmly in this case without hiding other errors.
 */
public class InputEndedException extends RuntimeException {
    /**
     * Constructor for InputEndedException.
     */
    public InputEndedException() {
        super("the input ended before a valid choice was made");
    }
}
