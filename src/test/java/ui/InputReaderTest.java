package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputReaderTest {
    private ByteArrayOutputStream outputBytes;

    @BeforeEach
    void setUp() {
        outputBytes = new ByteArrayOutputStream();
    }

    @Test
    void validChoiceIsReturned() {
        assertEquals(2, reader("2\n").readChoice(2));
    }

    @Test
    void lowestAndHighestNumbersAreValid() {
        assertEquals(1, reader("1\n").readChoice(21));
        assertEquals(21, reader("21\n").readChoice(21));
    }

    @Test
    void surroundingSpacesAreIgnored() {
        assertEquals(2, reader(" 2 \n").readChoice(2));
    }

    @Test
    void promptIsFollowedByABlankLine() {
        reader("1\n").readChoice(2);

        assertEquals("> \n", output());
    }

    @Test
    void numberAboveTheRangeIsAskedAgain() {
        var choice = reader("3\n1\n").readChoice(2);

        assertEquals(1, choice);
        assertEquals("""
            >\s
            Invalid choice. Try again (choose a number between 1 and 2):
            >\s
            """, output());
    }

    @Test
    void zeroIsAskedAgain() {
        assertEquals(1, reader("0\n1\n").readChoice(2));
        assertEquals(1, countErrors());
    }

    @Test
    void negativeNumberIsAskedAgain() {
        assertEquals(1, reader("-1\n1\n").readChoice(2));
        assertEquals(1, countErrors());
    }

    @Test
    void textWithSymbolsIsAskedAgain() {
        assertEquals(10, reader("a&\n10\n").readChoice(21));
        assertEquals("Invalid choice. Try again (choose a number between 1 and 21):", errorLine());
    }

    @Test
    void symbolIsAskedAgain() {
        assertEquals(15, reader("*\n15\n").readChoice(18));
        assertEquals("Invalid choice. Try again (choose a number between 1 and 18):", errorLine());
    }

    @Test
    void emptyLineIsAskedAgain() {
        assertEquals(2, reader("\n2\n").readChoice(2));
        assertEquals(1, countErrors());
    }

    @Test
    void numberTooLongForAnIntIsAskedAgain() {
        assertEquals(2, reader("99999999999\n2\n").readChoice(2));
        assertEquals(1, countErrors());
    }

    @Test
    void everyInvalidAttemptIsAskedAgain() {
        assertEquals(1, reader("x\n0\n5\n1\n").readChoice(2));
        assertEquals(3, countErrors());
    }

    @Test
    void endOfInputIsReported() {
        assertThrows(IllegalStateException.class, () -> reader("x\n").readChoice(2));
    }

    @Test
    void maximumBelowOneIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> reader("1\n").readChoice(0));
    }

    private InputReader reader(String typedText) {
        var output = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);

        return new InputReader(new Scanner(typedText), output);
    }

    /**
     * Returns everything written so far, with line endings unified so the tests pass on every system.
     */
    private String output() {
        return outputBytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }

    private long countErrors() {
        return output().lines().filter(line -> line.startsWith("Invalid choice.")).count();
    }

    private String errorLine() {
        return output().lines().filter(line -> line.startsWith("Invalid choice.")).findFirst().orElseThrow();
    }
}
