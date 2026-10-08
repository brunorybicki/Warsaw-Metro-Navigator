package routing;

/**
 * The cost of a route. Routes are compared by fewer stops first, then by fewer transfers.
 *
 * @param stops     the number of rides between adjacent stations.
 * @param transfers the number of lines changes.
 */
public record RouteCost(int stops, int transfers) implements Comparable<RouteCost> {
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

    /**
     * {@inheritDoc}
     *
     * <p>Fewer stops always win. Transfers only break ties between routes with the same number of stops,
     * so the ordering stays correct for networks that have more than one transfer station.
     */
    @Override
    public int compareTo(RouteCost other) {
        if (stops != other.stops) {
            return Integer.compare(stops, other.stops);
        }

        return Integer.compare(transfers, other.transfers);
    }
}
