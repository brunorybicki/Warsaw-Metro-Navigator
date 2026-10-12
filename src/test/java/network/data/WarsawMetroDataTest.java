package network.data;

import model.Connection;
import model.ConnectionType;
import model.MetroLine;
import model.Station;
import model.StationNode;
import network.MetroNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarsawMetroDataTest {
    private MetroNetwork network;
    private MetroLine m1;
    private MetroLine m2;

    @BeforeEach
    void setUp() {
        network = WarsawMetroData.createNetwork();
        m1 = network.lines().get(0);
        m2 = network.lines().get(1);
    }

    @Test
    void networkHasLinesM1AndM2() {
        assertEquals(List.of(new MetroLine("M1"), new MetroLine("M2")), network.lines());
    }

    @Test
    void m1Has21StationsFromMlocinyToKabaty() {
        var stations = network.stationsOf(m1);

        assertEquals(21, stations.size());
        assertEquals(new Station("Młociny"), stations.getFirst());
        assertEquals(new Station("Kabaty"), stations.getLast());
    }

    @Test
    void m2Has18StationsFromBemowoToBrodno() {
        var stations = network.stationsOf(m2);

        assertEquals(18, stations.size());
        assertEquals(new Station("Bemowo"), stations.getFirst());
        assertEquals(new Station("Bródno"), stations.getLast());
    }

    @Test
    void swietokrzyskaIsBetweenRatuszArsenalAndCentrumOnM1() {
        var stations = network.stationsOf(m1);
        var index = stations.indexOf(new Station("Świętokrzyska"));

        assertEquals(new Station("Ratusz Arsenał"), stations.get(index - 1));
        assertEquals(new Station("Centrum"), stations.get(index + 1));
    }

    @Test
    void swietokrzyskaIsBetweenRondoOnzAndNowySwiatOnM2() {
        var stations = network.stationsOf(m2);
        var index = stations.indexOf(new Station("Świętokrzyska"));

        assertEquals(new Station("Rondo ONZ"), stations.get(index - 1));
        assertEquals(new Station("Nowy Świat-Uniwersytet"), stations.get(index + 1));
    }

    @Test
    void swietokrzyskaConnectsM1AndM2InBothDirections() {
        var onM1 = new StationNode(new Station("Świętokrzyska"), m1);
        var onM2 = new StationNode(new Station("Świętokrzyska"), m2);

        assertTrue(network.connectionsFrom(onM1).contains(new Connection(onM1, onM2, ConnectionType.TRANSFER)));
        assertTrue(network.connectionsFrom(onM2).contains(new Connection(onM2, onM1, ConnectionType.TRANSFER)));
    }

    @Test
    void swietokrzyskaIsTheOnlyTransfer() {
        var transfers = network.lines().stream()
            .flatMap(line -> network.stationsOf(line).stream().map(station -> new StationNode(station, line)))
            .flatMap(node -> network.connectionsFrom(node).stream())
            .filter(connection -> connection.type() == ConnectionType.TRANSFER)
            .toList();

        assertEquals(2, transfers.size());
    }
}
