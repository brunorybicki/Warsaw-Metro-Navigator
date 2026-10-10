package routing;

/**
 * The cost of a route: how many stops it has and how many times the traveler changes lines.
 *
 * @param stops     the number of rides between adjacent stations.
 * @param transfers the number of line changes.
 */
public record RouteCost(int stops, int transfers) {
    /**
     * The cost of staying where you are.
     */
    public static final RouteCost ZERO = new RouteCost(0, 0);

    /**
     * Constructor for RouteCost.
     *
     * @throws IllegalArgumentException if the number of stops or transfers is negative.
     */
    public RouteCost {
        if (stops < 0) {
            throw new IllegalArgumentException("stops must not be negative");
        }

        if (transfers < 0) {
            throw new IllegalArgumentException("transfers must not be negative");
        }
    }

    /**
     * Returns this cost increased by one ride.
     */
    public RouteCost withRide() {
        return new RouteCost(stops + 1, transfers);
    }

    /**
     * Returns this cost increased by one transfer.
     */
    public RouteCost withTransfer() {
        return new RouteCost(stops, transfers + 1);
    }
}
