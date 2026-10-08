package model;

import java.util.Objects;

/**
 * A station on a particular metro line. The same physical station on two lines gives two distinct nodes.
 *
 * @param station the physical station.
 * @param line    the line on which the station is visited.
 */
public record StationNode(Station station, MetroLine line) {
    /**
     * Constructor for StationNode.
     *
     * @throws NullPointerException if the station or the line is null.
     */
    public StationNode {
        Objects.requireNonNull(station, "station must not be null");
        Objects.requireNonNull(line, "line must not be null");
    }
}
