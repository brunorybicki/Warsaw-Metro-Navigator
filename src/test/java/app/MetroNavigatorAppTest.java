package app;

import network.data.WarsawMetroData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import routing.RouteFinder;
import ui.ConsoleUI;
import ui.InputReader;
import ui.RouteFormatter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the whole application with typed answers and compares the output with docs/CLI_EXAMPLES.md.
 * The typed answers do not appear in the captured output, so every prompt "> " is followed directly by
 * the next printed line.
 */
class MetroNavigatorAppTest {
    private ByteArrayOutputStream outputBytes;

    @BeforeEach
    void setUp() {
        outputBytes = new ByteArrayOutputStream();
    }

    @Test
    void journeyWithTransferAndInvalidAnswersMatchesTheFirstExample() {
        run("3\n1\na&\n10\n2\n*\n15\n");

        assertEquals("""
            WELCOME TO THE WARSAW METRO NAVIGATOR!

            Available lines:
            M1 - Młociny → Kabaty
            M2 - Bemowo → Bródno

            Choose your starting metro line:
            (1) M1
            (2) M2
            >\s
            Invalid choice. Try again (choose a number between 1 and 2):
            >\s
            Choose your starting station on the M1 line:
            (1) Młociny
            (2) Wawrzyszew
            (3) Stare Bielany
            (4) Słodowiec
            (5) Marymont
            (6) Plac Wilsona
            (7) Dworzec Gdański
            (8) Ratusz Arsenał
            (9) Świętokrzyska
            (10) Centrum
            (11) Politechnika
            (12) Pole Mokotowskie
            (13) Racławicka
            (14) Wierzbno
            (15) Wilanowska
            (16) Służew
            (17) Ursynów
            (18) Stokłosy
            (19) Imielin
            (20) Natolin
            (21) Kabaty
            >\s
            Invalid choice. Try again (choose a number between 1 and 21):
            >\s
            START:
            M1 Centrum

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
            Invalid choice. Try again (choose a number between 1 and 18):
            >\s
            DESTINATION:
            M2 Trocka

            Finding the best route...

            YOUR ROUTE:
            Centrum
            ↓ M1 towards Młociny
            Świętokrzyska

            TRANSFER: M1 → M2

            Świętokrzyska
            ↓ M2 towards Bródno
            Nowy Świat-Uniwersytet
            Centrum Nauki Kopernik
            Stadion Narodowy
            Dworzec Wileński
            Szwedzka
            Targówek Mieszkaniowy
            Trocka

            Stops: 8
            Transfers: 1

            Have a nice journey!
            """, output());
    }

    @Test
    void sameStationOnDifferentLinesEndsWithTheTransferOnly() {
        run("1\n9\n2\n8\n");

        assertTrue(output().endsWith("""
            YOUR ROUTE:
            Świętokrzyska

            TRANSFER: M1 → M2

            You are already at your destination!

            Stops: 0
            Transfers: 1

            Have a nice journey!
            """));
    }

    @Test
    void identicalStartAndDestinationNeedsNoJourney() {
        run("1\n16\n1\n16\n");

        assertTrue(output().endsWith("""
            YOUR ROUTE:
            Służew

            You are already at your destination!

            Stops: 0
            Transfers: 0

            Have a nice journey!
            """));
    }

    @Test
    void endOfInputEndsTheProgramCalmly() {
        run("1\n");

        assertTrue(output().endsWith("(21) Kabaty\n> \nInput ended. Goodbye!\n"));
    }

    private void run(String typedText) {
        var network = WarsawMetroData.createNetwork();
        var output = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);
        var consoleUI = new ConsoleUI(network, new InputReader(new Scanner(typedText), output), output);

        new MetroNavigatorApp(consoleUI, new RouteFinder(network), new RouteFormatter(network)).run();
    }

    /**
     * Returns everything written so far, with line endings unified so the tests pass on every system.
     */
    private String output() {
        return outputBytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }
}
