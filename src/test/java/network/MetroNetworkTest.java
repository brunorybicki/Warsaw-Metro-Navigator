package network;

import model.MetroLine;
import model.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetroNetworkTest {
    private final MetroLine lineA = new MetroLine("A");
    private final MetroLine lineB = new MetroLine("B");

    private final List<Station> stationsOfA = List.of(new Station("One"), new Station("Two"), new Station("Three"));
    private final List<Station> stationsOfB = List.of(new Station("Four"), new Station("Two"));

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
    void askingForStationsOfAnUnknownLineIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> network.stationsOf(lineA));
    }
}
