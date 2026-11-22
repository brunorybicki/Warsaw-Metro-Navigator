package ui;

import model.MetroLine;
import model.Station;
import model.StationNode;
import network.data.WarsawMetroData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import routing.RouteFinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the route text against the examples in docs/CLI_EXAMPLES.md.
 */
class RouteFormatterTest {
    private RouteFinder finder;
    private RouteFormatter formatter;
    private MetroLine m1;
    private MetroLine m2;

    @BeforeEach
    void setUp() {
        var network = WarsawMetroData.createNetwork();
        finder = new RouteFinder(network);
        formatter = new RouteFormatter(network);
        m1 = network.lines().get(0);
        m2 = network.lines().get(1);
    }

    @Test
    void journeyWithATransferMatchesTheExample() {
        var expected = """
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
            """;

        assertEquals(expected, formatRoute(node(m1, "Centrum"), node(m2, "Trocka")));
    }

    @Test
    void sameStationOnDifferentLinesMatchesTheExample() {
        var expected = """
            YOUR ROUTE:
            Świętokrzyska

            TRANSFER: M1 → M2

            You are already at your destination!

            Stops: 0
            Transfers: 1

            Have a nice journey!
            """;

        assertEquals(expected, formatRoute(node(m1, "Świętokrzyska"), node(m2, "Świętokrzyska")));
    }

    @Test
    void sameStationOnDifferentLinesInTheOtherDirectionShowsTheReverseTransfer() {
        var expected = """
            YOUR ROUTE:
            Świętokrzyska

            TRANSFER: M2 → M1

            You are already at your destination!

            Stops: 0
            Transfers: 1

            Have a nice journey!
            """;

        assertEquals(expected, formatRoute(node(m2, "Świętokrzyska"), node(m1, "Świętokrzyska")));
    }

    @Test
    void identicalStartAndDestinationMatchesTheExample() {
        var expected = """
            YOUR ROUTE:
            Służew

            You are already at your destination!

            Stops: 0
            Transfers: 0

            Have a nice journey!
            """;

        assertEquals(expected, formatRoute(node(m1, "Służew"), node(m1, "Służew")));
    }

    @Test
    void journeyAlongOneLineShowsOneDirectionAndNoTransfer() {
        var expected = """
            YOUR ROUTE:
            Centrum
            ↓ M1 towards Kabaty
            Politechnika
            Pole Mokotowskie

            Stops: 2
            Transfers: 0

            Have a nice journey!
            """;

        assertEquals(expected, formatRoute(node(m1, "Centrum"), node(m1, "Pole Mokotowskie")));
    }

    @Test
    void journeyFromM2ToM1ShowsBothDirections() {
        var output = formatRoute(node(m2, "Trocka"), node(m1, "Centrum"));

        assertTrue(output.contains("↓ M2 towards Bemowo"));
        assertTrue(output.contains("TRANSFER: M2 → M1"));
        assertTrue(output.contains("↓ M1 towards Kabaty"));
    }

    @Test
    void journeyEndingWithATransferDoesNotRepeatTheStation() {
        var expected = """
            YOUR ROUTE:
            Centrum
            ↓ M1 towards Młociny
            Świętokrzyska

            TRANSFER: M1 → M2

            Stops: 1
            Transfers: 1

            Have a nice journey!
            """;

        assertEquals(expected, formatRoute(node(m1, "Centrum"), node(m2, "Świętokrzyska")));
    }

    private String formatRoute(StationNode start, StationNode destination) {
        return formatter.format(finder.findRoute(start, destination));
    }

    private StationNode node(MetroLine line, String stationName) {
        return new StationNode(new Station(stationName), line);
    }
}
