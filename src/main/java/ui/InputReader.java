package ui;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * Reads numbered choices typed by the user and keeps asking until a valid one is given.
 * The same method serves every menu, so the validation loop exists only once.
 */
public class InputReader {
    private final Scanner input;
    private final PrintStream output;

    /**
     * Constructor for InputReader.
     *
     * @param input  where the user's lines are read from.
     * @param output where the prompt and the error messages are written to.
     * @throws NullPointerException if the input or the output is null.
     */
    public InputReader(Scanner input, PrintStream output) {
        this.input = Objects.requireNonNull(input, "input must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * Asks for a number between 1 and the given maximum, both inclusive, until the user types a valid one.
     * Anything else, such as text, symbols, an empty line or a number out of range, is answered with an error
     * message and the question is asked again.
     *
     * @param maxChoice the highest valid number.
     * @return the number chosen by the user.
     * @throws IllegalArgumentException if the maximum is lower than 1.
     * @throws IllegalStateException    if the input ends before a valid number is typed.
     */
    public int readChoice(int maxChoice) {
        if (maxChoice < 1) {
            throw new IllegalArgumentException("maxChoice must be at least 1");
        }

        while (true) {
            output.print("> ");
            var line = readLine().trim();
            output.println();

            if (isValidChoice(line, maxChoice)) {
                return Integer.parseInt(line);
            }

            output.println("Invalid choice. Try again (choose a number between 1 and %d):".formatted(maxChoice));
        }
    }

    /**
     * Tells whether the text is a number between 1 and the maximum. The text is checked before it is converted,
     * so a number too long to fit in an int is rejected instead of making the conversion fail.
     */
    private boolean isValidChoice(String text, int maxChoice) {
        var maxDigits = String.valueOf(maxChoice).length();

        if (!text.matches("[0-9]+") || text.length() > maxDigits) {
            return false;
        }

        var choice = Integer.parseInt(text);

        return choice >= 1 && choice <= maxChoice;
    }

    /**
     * Returns the next line typed by the user.
     */
    private String readLine() {
        if (!input.hasNextLine()) {
            throw new IllegalStateException("the input ended before a valid choice was made");
        }

        return input.nextLine();
    }
}
