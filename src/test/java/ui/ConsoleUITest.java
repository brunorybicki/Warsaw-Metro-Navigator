package ui;

import model.Station;
import model.StationNode;
import network.MetroNetwork;
import network.data.WarsawMetroData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the terminal dialogue against docs/CLI_EXAMPLES.md. The typed answers do not appear in the captured
 * output, so every prompt "> " is followed directly by the blank line printed after the answer.
 */
class ConsoleUITest {
    private MetroNetwork network;
    private ByteArrayOutputStream outputBytes;

    @BeforeEach
    void setUp() {
        network = WarsawMetroData.createNetwork();
        outputBytes = new ByteArrayOutputStream();
    }

    @Test
    void welcomeListsTheLinesFromTheirFirstToTheirLastStation() {
        consoleUI("").showWelcome();

        assertEquals("""
            WELCOME TO THE WARSAW METRO NAVIGATOR!

            Available lines:
            M1 - Młociny → Kabaty
            M2 - Bemowo → Bródno

            """, output());
    }

    @Test
    void destinationIsChosenFromTheLineAndStationMenus() {
        var destination = consoleUI("2\n15\n").chooseDestination();

        assertEquals(new StationNode(new Station("Trocka"), network.lines().get(1)), destination);
        assertEquals("""
            Choose your destination metro line:
            (1) M1
            (2) M2
            >\s
            Choose your destination station on the M2 line:
            (1) Bemowo
            (2) Ulrychów
            (3) Księcia Janusza
            (4) Młynów
            (5) Płocka
            (6) Rondo Daszyńskiego
            (7) Rondo ONZ
            (8) Świętokrzyska
            (9) Nowy Świat-Uniwersytet
            (10) Centrum Nauki Kopernik
            (11) Stadion Narodowy
            (12) Dworzec Wileński
            (13) Szwedzka
            (14) Targówek Mieszkaniowy
            (15) Trocka
            (16) Zacisze
            (17) Kondratowicza
            (18) Bródno
            >\s
            DESTINATION:
            M2 Trocka

            """, output());
    }

    @Test
    void startIsConfirmedUnderTheStartHeading() {
        var start = consoleUI("1\n10\n").chooseStart();

        assertEquals(new StationNode(new Station("Centrum"), network.lines().get(0)), start);
        assertTrue(output().startsWith("Choose your starting metro line:\n"));
        assertTrue(output().contains("Choose your starting station on the M1 line:\n(1) Młociny\n"));
        assertTrue(output().endsWith("START:\nM1 Centrum\n\n"));
    }

    @Test
    void invalidLineAndStationAreAskedAgainWithTheRightRange() {
        var start = consoleUI("3\n1\na&\n10\n").chooseStart();

        assertEquals(new StationNode(new Station("Centrum"), network.lines().get(0)), start);
        assertTrue(output().contains("Invalid choice. Try again (choose a number between 1 and 2):"));
        assertTrue(output().contains("Invalid choice. Try again (choose a number between 1 and 21):"));
    }

    @Test
    void searchingMessageIsFollowedByABlankLine() {
        consoleUI("").showSearching();

        assertEquals("Finding the best route...\n\n", output());
    }

    @Test
    void routeTextIsPrintedAsItIs() {
        consoleUI("").showRoute("YOUR ROUTE:\nCentrum\n");

        assertEquals("YOUR ROUTE:\nCentrum\n", output());
    }

    private ConsoleUI consoleUI(String typedText) {
        var output = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);
        var inputReader = new InputReader(new Scanner(typedText), output);

        return new ConsoleUI(network, inputReader, output);
    }

    /**
     * Returns everything written so far, with line endings unified so the tests pass on every system.
     */
    private String output() {
        return outputBytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }
}
