package model;

import org.junit.jupiter.api.Test;
import routing.RouteCost;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {
    private final MetroLine lineA = new MetroLine("A");
    private final MetroLine lineB = new MetroLine("B");

    private final StationNode oneOnA = new StationNode(new Station("One"), lineA);
    private final StationNode twoOnA = new StationNode(new Station("Two"), lineA);
    private final StationNode twoOnB = new StationNode(new Station("Two"), lineB);
    private final StationNode threeOnB = new StationNode(new Station("Three"), lineB);

    @Test
    void routeWithoutConnectionsContainsOnlyTheStartNode() {
        var route = new Route(oneOnA, List.of());

        assertEquals(List.of(oneOnA), route.nodes());
        assertEquals(RouteCost.ZERO, route.cost());
    }

    @Test
    void nodesAreListedInTravelOrder() {
        var route = new Route(oneOnA, List.of(
            new Connection(oneOnA, twoOnA, ConnectionType.RIDE),
            new Connection(twoOnA, twoOnB, ConnectionType.TRANSFER),
            new Connection(twoOnB, threeOnB, ConnectionType.RIDE)));

        assertEquals(List.of(oneOnA, twoOnA, twoOnB, threeOnB), route.nodes());
    }

    @Test
    void ridesCountAsStopsAndTransfersCountAsTransfers() {
        var route = new Route(oneOnA, List.of(
            new Connection(oneOnA, twoOnA, ConnectionType.RIDE),
            new Connection(twoOnA, twoOnB, ConnectionType.TRANSFER),
            new Connection(twoOnB, threeOnB, ConnectionType.RIDE)));

        assertEquals(new RouteCost(2, 1), route.cost());
    }

    @Test
    void transferOnlyRouteHasNoStops() {
        var route = new Route(twoOnA, List.of(new Connection(twoOnA, twoOnB, ConnectionType.TRANSFER)));

        assertEquals(new RouteCost(0, 1), route.cost());
    }

    @Test
    void connectionThatDoesNotContinueTheRouteIsRejected() {
        var connections = List.of(new Connection(twoOnA, threeOnB, ConnectionType.RIDE));

        assertThrows(IllegalArgumentException.class, () -> new Route(oneOnA, connections));
    }
}
