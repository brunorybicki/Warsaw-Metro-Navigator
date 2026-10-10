package network;

import model.Connection;
import model.ConnectionType;
import model.MetroLine;
import model.Station;
import model.StationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A generic metro network made of lines, each with an ordered list of stations.
 * It knows nothing about any particular city.
 */
public class MetroNetwork {
    private final Map<MetroLine, List<Station>> stationsByLine = new LinkedHashMap<>();
    private final Map<StationNode, List<Connection>> connectionsByNode = new HashMap<>();

    /**
     * Adds a line together with its stations, in travel order from one terminal to the other.
     * Adjacent stations of the line are connected by rides in both directions.
     *
     * @param line     the line to add.
     * @param stations the stations of the line, in order.
     * @throws NullPointerException     if the line or the stations are null.
     * @throws IllegalArgumentException if the line is already in the network, has no stations or repeats a station.
     */
    public void addLine(MetroLine line, List<Station> stations) {
        requireNewLine(line, stations);

        stationsByLine.put(line, List.copyOf(stations));
        addRides(line, stations);
    }

    /**
     * Connects two nodes by a transfer in both directions.
     * <p>
     * A transfer is a connection of its own, between two distinct nodes, because the same physical station
     * on two lines is two nodes. Keeping it separate from a ride lets the route cost count it as a transfer
     * and not as a stop.
     *
     * @param first  the node on one line.
     * @param second the node on the other line.
     * @throws NullPointerException     if a node is null.
     * @throws IllegalArgumentException if a node is not in the network or both nodes are the same.
     */
    public void addTransfer(StationNode first, StationNode second) {
        requireKnownNode(first);
        requireKnownNode(second);

        if (first.equals(second)) {
            throw new IllegalArgumentException("a node cannot be transferred to itself: " + first);
        }

        connectBothWays(first, second, ConnectionType.TRANSFER);
    }

    /**
     * Returns the lines of the network, in the order in which they were added.
     */
    public List<MetroLine> lines() {
        return List.copyOf(stationsByLine.keySet());
    }

    /**
     * Returns the stations of a line, in travel order. The first and the last one are the terminals of the line.
     *
     * @param line the line whose stations are requested.
     * @throws IllegalArgumentException if the line is not in the network.
     */
    public List<Station> stationsOf(MetroLine line) {
        var stations = stationsByLine.get(line);

        if (stations == null) {
            throw new IllegalArgumentException("line is not in the network: " + line);
        }

        return stations;
    }

    /**
     * Returns the connections that start at a node.
     *
     * @param node the node whose outgoing connections are requested.
     * @throws NullPointerException     if the node is null.
     * @throws IllegalArgumentException if the node is not in the network.
     */
    public List<Connection> connectionsFrom(StationNode node) {
        requireKnownNode(node);

        return List.copyOf(connectionsByNode.get(node));
    }

    /**
     * Fails fast when the line cannot be added: it is null, already known, or its stations are missing or repeated.
     * A repeated station would make two nodes of the line identical and break its connections.
     */
    private void requireNewLine(MetroLine line, List<Station> stations) {
        Objects.requireNonNull(line, "line must not be null");
        Objects.requireNonNull(stations, "stations must not be null");

        if (stationsByLine.containsKey(line)) {
            throw new IllegalArgumentException("line is already in the network: " + line.lineName());
        }

        if (stations.isEmpty()) {
            throw new IllegalArgumentException("line must have at least one station: " + line.lineName());
        }

        if (Set.copyOf(stations).size() != stations.size()) {
            throw new IllegalArgumentException("line must not repeat a station: " + line.lineName());
        }
    }

    /**
     * Creates a node for every station of the line and connects each pair of adjacent nodes by rides.
     */
    private void addRides(MetroLine line, List<Station> stations) {
        var nodes = stations.stream()
            .map(station -> new StationNode(station, line))
            .toList();

        for (var node : nodes) {
            connectionsByNode.put(node, new ArrayList<>());
        }

        for (int i = 1; i < nodes.size(); i++) {
            connectBothWays(nodes.get(i - 1), nodes.get(i), ConnectionType.RIDE);
        }
    }

    /**
     * Adds a connection of the given type from the first node to the second one and another one back.
     */
    private void connectBothWays(StationNode first, StationNode second, ConnectionType type) {
        connectionsByNode.get(first).add(new Connection(first, second, type));
        connectionsByNode.get(second).add(new Connection(second, first, type));
    }

    /**
     * Fails fast when the node is null or does not belong to this network.
     */
    private void requireKnownNode(StationNode node) {
        Objects.requireNonNull(node, "node must not be null");

        if (!connectionsByNode.containsKey(node)) {
            throw new IllegalArgumentException("node is not in the network: " + node);
        }
    }
}
