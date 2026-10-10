package routing;

import model.Connection;
import model.Route;
import model.StationNode;
import network.MetroNetwork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Finds a route between two station nodes by following the connections of a metro network.
 * <p>
 * The search returns the first route it finds. That is the best route as long as the network has only one way
 * between any two nodes, as in a network of lines that cross at single stations. A network with alternative
 * ways would need a search that compares route costs.
 */
public class RouteFinder {
    private final MetroNetwork network;

    /**
     * Constructor for RouteFinder.
     *
     * @param network the network in which routes are searched.
     * @throws NullPointerException if the network is null.
     */
    public RouteFinder(MetroNetwork network) {
        this.network = Objects.requireNonNull(network, "network must not be null");
    }

    /**
     * Finds a route from the start node to the destination node.
     *
     * @param start       the node where the journey starts.
     * @param destination the node where the journey ends.
     * @return the route from the start node to the destination node.
     * @throws NullPointerException     if a node is null.
     * @throws IllegalArgumentException if the start node is not in the network or the destination cannot be reached.
     */
    public Route findRoute(StationNode start, StationNode destination) {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(destination, "destination must not be null");

        var path = new ArrayList<Connection>();

        if (!walk(start, destination, new HashSet<>(), path)) {
            throw new IllegalArgumentException("no route from " + start + " to " + destination);
        }

        return new Route(start, path);
    }

    /**
     * Walks from the current node towards the destination and tells whether it got there. The connections taken
     * are left in the path. A connection that leads to a dead end is taken back out of the path. Visited nodes are
     * skipped, so the search never turns back along a connection it came by.
     */
    private boolean walk(StationNode current, StationNode destination, Set<StationNode> visited, List<Connection> path) {
        if (current.equals(destination)) {
            return true;
        }

        visited.add(current);

        for (var connection : network.connectionsFrom(current)) {
            if (!visited.contains(connection.to())) {
                path.add(connection);

                if (walk(connection.to(), destination, visited, path)) {
                    return true;
                }

                path.removeLast();
            }
        }

        return false;
    }
}
