package ui;

import model.Connection;
import model.ConnectionType;
import model.Route;
import model.StationNode;
import network.MetroNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Turns a calculated route into the text shown to the user: the stations, the travel directions, the transfers,
 * and the totals.
 */
public class RouteFormatter {
    private final MetroNetwork network;

    /**
     * Constructor for RouteFormatter.
     *
     * @param network the network the routes belong to, used to find the travel directions.
     * @throws NullPointerException if the network is null.
     */
    public RouteFormatter(MetroNetwork network) {
        this.network = Objects.requireNonNull(network, "network must not be null");
    }

    /**
     * Returns the route as text, one line per station, direction, or transfer, followed by the totals.
     *
     * @param route the route to show.
     * @throws NullPointerException if the route is null.
     */
    public String format(Route route) {
        Objects.requireNonNull(route, "route must not be null");

        var lines = new ArrayList<String>();
        lines.add("YOUR ROUTE:");
        lines.add(nameOf(route.start()));

        addJourney(route, lines);

        if (route.cost().stops() == 0) {
            addBlankLine(lines);
            lines.add("You are already at your destination!");
        }

        addBlankLine(lines);
        lines.add("Stops: " + route.cost().stops());
        lines.add("Transfers: " + route.cost().transfers());
        lines.add("");
        lines.add("Have a nice journey!");

        return String.join("\n", lines) + "\n";
    }

    /**
     * Adds the stations, directions, and transfers of the route, after its start station.
     * <p>
     * A direction is shown only when a ride on a line begins. After a transfer, the station is shown again,
     * but only if a ride follows, because a route can also end with the transfer.
     */
    private void addJourney(Route route, List<String> lines) {
        // True when the next ride starts on a line, so it needs a direction line first.
        var rideBegins = true;
        // False right after a transfer: the station is printed again only if a ride follows.
        var stationShown = true;

        for (var connection : route.connections()) {
            if (connection.type() == ConnectionType.TRANSFER) {
                addTransfer(connection, lines);
                rideBegins = true;
                stationShown = false;
            } else {
                if (!stationShown) {
                    lines.add(nameOf(connection.from()));
                }

                if (rideBegins) {
                    lines.add(directionOf(connection));
                }

                lines.add(nameOf(connection.to()));
                rideBegins = false;
                stationShown = true;
            }
        }
    }

    /**
     * Adds a transfer between two lines, separated from the stations by blank lines.
     */
    private void addTransfer(Connection transfer, List<String> lines) {
        addBlankLine(lines);
        lines.add("TRANSFER: " + transfer.from().line().lineName() + " → " + transfer.to().line().lineName());
        lines.add("");
    }

    /**
     * Returns the line that tells on which line and towards which terminal a ride goes.
     */
    private String directionOf(Connection ride) {
        var terminal = network.terminalTowards(ride.from(), ride.to());

        return "↓ " + ride.from().line().lineName() + " towards " + terminal.stationName();
    }

    /**
     * Adds a blank line unless the previous line is already blank, so sections are never separated by two.
     */
    private void addBlankLine(List<String> lines) {
        if (!lines.getLast().isEmpty()) {
            lines.add("");
        }
    }

    /**
     * Returns the name of the station of a node, as shown to the user.
     */
    private String nameOf(StationNode node) {
        return node.station().stationName();
    }
}
