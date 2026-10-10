package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StationNodeTest {
    private final Station station = new Station("Świętokrzyska");
    private final MetroLine lineA = new MetroLine("A");
    private final MetroLine lineB = new MetroLine("B");

    @Test
    void nodeKeepsItsStationAndLine() {
        var node = new StationNode(station, lineA);

        assertEquals(station, node.station());
        assertEquals(lineA, node.line());
    }

    @Test
    void nodesWithTheSameStationAndLineAreEqual() {
        assertEquals(new StationNode(station, lineA), new StationNode(new Station("Świętokrzyska"), new MetroLine("A")));
    }

    @Test
    void sameStationOnDifferentLinesGivesDistinctNodes() {
        assertNotEquals(new StationNode(station, lineA), new StationNode(station, lineB));
    }

    @Test
    void nullStationIsRejected() {
        assertThrows(NullPointerException.class, () -> new StationNode(null, lineA));
    }

    @Test
    void nullLineIsRejected() {
        assertThrows(NullPointerException.class, () -> new StationNode(station, null));
    }
}
