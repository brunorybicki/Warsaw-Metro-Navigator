package network;

import model.Connection;
import model.ConnectionType;
import model.MetroLine;
import model.Station;
import model.StationNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetroNetworkTest {
    private final MetroLine lineA = new MetroLine("A");
    private final MetroLine lineB = new MetroLine("B");

    private final List<Station> stationsOfA = List.of(new Station("One"), new Station("Two"), new Station("Three"));
    private final List<Station> stationsOfB = List.of(new Station("Four"), new Station("Two"));

    private final StationNode oneOnA = new StationNode(new Station("One"), lineA);
    private final StationNode twoOnA = new StationNode(new Station("Two"), lineA);
    private final StationNode threeOnA = new StationNode(new Station("Three"), lineA);
    private final StationNode twoOnB = new StationNode(new Station("Two"), lineB);

    private MetroNetwork network;

    @BeforeEach
    void setUp() {
        network = new MetroNetwork();
    }

    @Test
    void emptyNetworkHasNoLines() {
        assertEquals(List.of(), network.lines());
    }

    @Test
    void linesAreReturnedInTheOrderTheyWereAdded() {
        network.addLine(lineB, stationsOfB);
        network.addLine(lineA, stationsOfA);

        assertEquals(List.of(lineB, lineA), network.lines());
    }

    @Test
    void stationsAreReturnedInTravelOrder() {
        network.addLine(lineA, stationsOfA);

        assertEquals(stationsOfA, network.stationsOf(lineA));
    }

    @Test
    void stationsOfDifferentLinesAreKeptSeparate() {
        network.addLine(lineA, stationsOfA);
        network.addLine(lineB, stationsOfB);

        assertEquals(stationsOfB, network.stationsOf(lineB));
    }

    @Test
    void addingTheSameLineTwiceIsRejected() {
        network.addLine(lineA, stationsOfA);

        assertThrows(IllegalArgumentException.class, () -> network.addLine(lineA, stationsOfB));
    }

    @Test
    void addingALineWithoutStationsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> network.addLine(lineA, List.of()));
    }

    @Test
    void addingALineThatRepeatsAStationIsRejected() {
        var repeatingStations = List.of(new Station("One"), new Station("Two"), new Station("One"));

        assertThrows(IllegalArgumentException.class, () -> network.addLine(lineA, repeatingStations));
    }

    @Test
    void askingForStationsOfAnUnknownLineIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> network.stationsOf(lineA));
    }

    @Test
    void adjacentStationsAreConnectedByRidesInBothDirections() {
        network.addLine(lineA, stationsOfA);

        var expected = Set.of(
            new Connection(twoOnA, oneOnA, ConnectionType.RIDE),
            new Connection(twoOnA, threeOnA, ConnectionType.RIDE));

        assertEquals(expected, Set.copyOf(network.connectionsFrom(twoOnA)));
    }

    @Test
    void terminalHasOnlyOneConnection() {
        network.addLine(lineA, stationsOfA);

        assertEquals(List.of(new Connection(oneOnA, twoOnA, ConnectionType.RIDE)), network.connectionsFrom(oneOnA));
    }

    @Test
    void lineWithOneStationHasNoConnections() {
        network.addLine(lineA, List.of(new Station("One")));

        assertEquals(List.of(), network.connectionsFrom(oneOnA));
    }

    @Test
    void transferConnectsTwoNodesInBothDirections() {
        network.addLine(lineA, stationsOfA);
        network.addLine(lineB, stationsOfB);

        network.addTransfer(twoOnA, twoOnB);

        assertTrue(network.connectionsFrom(twoOnA).contains(new Connection(twoOnA, twoOnB, ConnectionType.TRANSFER)));
        assertTrue(network.connectionsFrom(twoOnB).contains(new Connection(twoOnB, twoOnA, ConnectionType.TRANSFER)));
    }

    @Test
    void transferOfANodeWithItselfIsRejected() {
        network.addLine(lineA, stationsOfA);

        assertThrows(IllegalArgumentException.class, () -> network.addTransfer(twoOnA, twoOnA));
    }

    @Test
    void transferOfAnUnknownNodeIsRejected() {
        network.addLine(lineA, stationsOfA);

        assertThrows(IllegalArgumentException.class, () -> network.addTransfer(twoOnA, twoOnB));
    }

    @Test
    void askingForConnectionsOfAnUnknownNodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> network.connectionsFrom(oneOnA));
    }
}
