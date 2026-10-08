package model;

/**
 * The kind of connection between two station nodes.
 */
public enum ConnectionType {
    /**
     * A ride between adjacent stations on the same line.
     */
    RIDE,

    /**
     * A change between lines at the same physical station.
     */
    TRANSFER
}
