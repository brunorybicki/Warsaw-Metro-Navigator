package routing;

import model.MetroLine;
import model.Station;
import model.StationNode;
import network.MetroNetwork;
import network.data.WarsawMetroData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the route finder on the real Warsaw Metro data.
 */
class WarsawRoutesTest {
    private MetroNetwork network;
    private RouteFinder finder;
    private MetroLine m1;
    private MetroLine m2;

    @BeforeEach
    void setUp() {
        network = WarsawMetroData.createNetwork();
        finder = new RouteFinder(network);
        m1 = network.lines().get(0);
        m2 = network.lines().get(1);
    }

    @Test
    void journeyAlongM1HasNoTransfers() {
        var route = finder.findRoute(node(m1, "Centrum"), node(m1, "Kabaty"));

        assertEquals(new RouteCost(11, 0), route.cost());
    }

    @Test
    void journeyAlongM2HasNoTransfers() {
        var route = finder.findRoute(node(m2, "Rondo ONZ"), node(m2, "Szwedzka"));

        assertEquals(new RouteCost(6, 0), route.cost());
    }

    @Test
    void journeyFromM1ToM2ChangesLinesAtSwietokrzyska() {
        var route = finder.findRoute(node(m1, "Centrum"), node(m2, "Trocka"));

        var expected = List.of(
            node(m1, "Centrum"),
            node(m1, "Świętokrzyska"),
            node(m2, "Świętokrzyska"),
            node(m2, "Nowy Świat-Uniwersytet"),
            node(m2, "Centrum Nauki Kopernik"),
            node(m2, "Stadion Narodowy"),
            node(m2, "Dworzec Wileński"),
            node(m2, "Szwedzka"),
            node(m2, "Targówek Mieszkaniowy"),
            node(m2, "Trocka"));

        assertEquals(expected, route.nodes());
        assertEquals(new RouteCost(8, 1), route.cost());
    }

    @Test
    void journeyFromM2ToM1ChangesLinesAtSwietokrzyska() {
        var route = finder.findRoute(node(m2, "Trocka"), node(m1, "Centrum"));

        assertEquals(new RouteCost(8, 1), route.cost());
        assertEquals(node(m2, "Trocka"), route.nodes().getFirst());
        assertEquals(node(m1, "Centrum"), route.nodes().getLast());
    }

    @Test
    void swietokrzyskaOnM1ToSwietokrzyskaOnM2IsOnlyATransfer() {
        var route = finder.findRoute(node(m1, "Świętokrzyska"), node(m2, "Świętokrzyska"));

        assertEquals(new RouteCost(0, 1), route.cost());
    }

    @Test
    void swietokrzyskaOnM2ToSwietokrzyskaOnM1IsOnlyATransfer() {
        var route = finder.findRoute(node(m2, "Świętokrzyska"), node(m1, "Świętokrzyska"));

        assertEquals(new RouteCost(0, 1), route.cost());
    }

    @Test
    void identicalStartAndDestinationNeedNoRidesAndNoTransfers() {
        var route = finder.findRoute(node(m1, "Centrum"), node(m1, "Centrum"));

        assertEquals(List.of(node(m1, "Centrum")), route.nodes());
        assertEquals(RouteCost.ZERO, route.cost());
    }

    @Test
    void everyStationIsReachableFromEveryOtherStation() {
        var nodes = allNodes();

        for (var start : nodes) {
            for (var destination : nodes) {
                var route = finder.findRoute(start, destination);

                assertEquals(start, route.nodes().getFirst());
                assertEquals(destination, route.nodes().getLast());
            }
        }
    }

    private List<StationNode> allNodes() {
        var nodes = new ArrayList<StationNode>();

        for (var line : List.of(m1, m2)) {
            for (var station : network.stationsOf(line)) {
                nodes.add(new StationNode(station, line));
            }
        }

        return nodes;
    }

    private StationNode node(MetroLine line, String stationName) {
        return new StationNode(new Station(stationName), line);
    }
}
