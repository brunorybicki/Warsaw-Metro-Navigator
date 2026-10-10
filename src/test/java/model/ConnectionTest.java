package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionTest {
    private final MetroLine line = new MetroLine("A");
    private final StationNode one = new StationNode(new Station("One"), line);
    private final StationNode two = new StationNode(new Station("Two"), line);

    @Test
    void connectionKeepsItsNodesAndType() {
        var connection = new Connection(one, two, ConnectionType.RIDE);

        assertEquals(one, connection.from());
        assertEquals(two, connection.to());
        assertEquals(ConnectionType.RIDE, connection.type());
    }

    @Test
    void connectionsWithTheSameNodesAndTypeAreEqual() {
        assertEquals(new Connection(one, two, ConnectionType.RIDE), new Connection(one, two, ConnectionType.RIDE));
    }

    @Test
    void connectionIsDirected() {
        assertNotEquals(new Connection(one, two, ConnectionType.RIDE), new Connection(two, one, ConnectionType.RIDE));
    }

    @Test
    void nullStartNodeIsRejected() {
        assertThrows(NullPointerException.class, () -> new Connection(null, two, ConnectionType.RIDE));
    }

    @Test
    void nullEndNodeIsRejected() {
        assertThrows(NullPointerException.class, () -> new Connection(one, null, ConnectionType.RIDE));
    }

    @Test
    void nullTypeIsRejected() {
        assertThrows(NullPointerException.class, () -> new Connection(one, two, null));
    }
}
