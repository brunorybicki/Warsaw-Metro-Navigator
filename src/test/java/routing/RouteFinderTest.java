package routing;

import model.MetroLine;
import model.Station;
import model.StationNode;
import network.MetroNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the route finder on a small synthetic network, so it is independent of any real city.
 */
class RouteFinderTest {
    private final MetroLine lineA = new MetroLine("A");
    private final MetroLine lineB = new MetroLine("B");

    private final StationNode oneOnA = node(lineA, "One");
    private final StationNode twoOnA = node(lineA, "Two");
    private final StationNode threeOnA = node(lineA, "Three");
    private final StationNode fourOnB = node(lineB, "Four");
    private final StationNode twoOnB = node(lineB, "Two");
    private final StationNode fiveOnB = node(lineB, "Five");

    private MetroNetwork network;
    private RouteFinder finder;

    @BeforeEach
    void setUp() {
        network = new MetroNetwork();
        network.addLine(lineA, stations("One", "Two", "Three"));
        network.addLine(lineB, stations("Four", "Two", "Five"));
        network.addTransfer(twoOnA, twoOnB);

        finder = new RouteFinder(network);
    }

    @Test
    void routeAlongOneLineGoesThroughTheStationsInBetween() {
        var route = finder.findRoute(oneOnA, threeOnA);

        assertEquals(List.of(oneOnA, twoOnA, threeOnA), route.nodes());
        assertEquals(new RouteCost(2, 0), route.cost());
    }

    @Test
    void routeAlongOneLineWorksInTheOppositeDirection() {
        var route = finder.findRoute(threeOnA, oneOnA);

        assertEquals(List.of(threeOnA, twoOnA, oneOnA), route.nodes());
        assertEquals(new RouteCost(2, 0), route.cost());
    }

    @Test
    void routeBetweenLinesGoesThroughTheTransfer() {
        var route = finder.findRoute(oneOnA, fiveOnB);

        assertEquals(List.of(oneOnA, twoOnA, twoOnB, fiveOnB), route.nodes());
        assertEquals(new RouteCost(2, 1), route.cost());
    }

    @Test
    void routeBetweenLinesWorksInTheOppositeDirection() {
        var route = finder.findRoute(fourOnB, threeOnA);

        assertEquals(List.of(fourOnB, twoOnB, twoOnA, threeOnA), route.nodes());
        assertEquals(new RouteCost(2, 1), route.cost());
    }

    @Test
    void routeBetweenTheSameStationOnTwoLinesIsOnlyATransfer() {
        var route = finder.findRoute(twoOnA, twoOnB);

        assertEquals(List.of(twoOnA, twoOnB), route.nodes());
        assertEquals(new RouteCost(0, 1), route.cost());
    }

    @Test
    void routeFromAStationToItselfHasNoConnections() {
        var route = finder.findRoute(twoOnA, twoOnA);

        assertEquals(List.of(twoOnA), route.nodes());
        assertEquals(RouteCost.ZERO, route.cost());
    }

    @Test
    void unreachableDestinationIsRejected() {
        var lineC = new MetroLine("C");
        var sevenOnC = node(lineC, "Seven");
        network.addLine(lineC, stations("Seven", "Eight"));

        assertThrows(IllegalArgumentException.class, () -> finder.findRoute(oneOnA, sevenOnC));
    }

    @Test
    void startNodeOutsideTheNetworkIsRejected() {
        var unknown = node(new MetroLine("C"), "Seven");

        assertThrows(IllegalArgumentException.class, () -> finder.findRoute(unknown, oneOnA));
    }

    @Test
    void nullStartIsRejected() {
        assertThrows(NullPointerException.class, () -> finder.findRoute(null, oneOnA));
    }

    @Test
    void nullDestinationIsRejected() {
        assertThrows(NullPointerException.class, () -> finder.findRoute(oneOnA, null));
    }

    @Test
    void nullNetworkIsRejected() {
        assertThrows(NullPointerException.class, () -> new RouteFinder(null));
    }

    private StationNode node(MetroLine line, String stationName) {
        return new StationNode(new Station(stationName), line);
    }

    private List<Station> stations(String... names) {
        return Arrays.stream(names).map(Station::new).toList();
    }
}
