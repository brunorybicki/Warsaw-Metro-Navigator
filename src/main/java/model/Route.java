package model;

import routing.RouteCost;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A calculated journey: the node where it starts, followed by the connections taken one after another.
 * A route without connections means that the traveler is already at the destination.
 *
 * @param start       the node where the journey starts.
 * @param connections the connections of the journey, in travel order.
 */
public record Route(StationNode start, List<Connection> connections) {
    /**
     * Constructor for Route.
     *
     * @throws NullPointerException     if the start node or the connections are null.
     * @throws IllegalArgumentException if a connection does not start where the previous one ended.
     */
    public Route {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(connections, "connections must not be null");

        connections = List.copyOf(connections);

        var current = start;

        for (var connection : connections) {
            if (!connection.from().equals(current)) {
                throw new IllegalArgumentException("connection does not continue the route: " + connection);
            }

            current = connection.to();
        }
    }

    /**
     * Returns all nodes of the journey in travel order, starting with the start node.
     */
    public List<StationNode> nodes() {
        var nodes = new ArrayList<StationNode>();
        nodes.add(start);

        for (var connection : connections) {
            nodes.add(connection.to());
        }

        return nodes;
    }

    /**
     * Returns the number of stops and transfers of the journey.
     */
    public RouteCost cost() {
        var cost = RouteCost.ZERO;

        for (var connection : connections) {
            cost = switch (connection.type()) {
                case RIDE -> cost.withRide();
                case TRANSFER -> cost.withTransfer();
            };
        }

        return cost;
    }
}
