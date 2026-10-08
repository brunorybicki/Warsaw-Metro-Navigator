package model;

import java.util.Objects;

/**
 * A directed connection between two station nodes, either a ride or a transfer.
 *
 * @param from the node where the connection starts.
 * @param to   the node where the connection ends.
 * @param type the kind of connection.
 */
public record Connection(StationNode from, StationNode to, ConnectionType type) {
    /**
     * Constructor for Connection.
     *
     * @throws NullPointerException if the start node, the end node, or the type is null.
     */
    public Connection {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }
}
