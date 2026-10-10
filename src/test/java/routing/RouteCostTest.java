package routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteCostTest {
    @Test
    void zeroCostHasNoStopsAndNoTransfers() {
        assertEquals(0, RouteCost.ZERO.stops());
        assertEquals(0, RouteCost.ZERO.transfers());
    }

    @Test
    void rideAddsOneStopAndNoTransfers() {
        var cost = new RouteCost(2, 1).withRide();

        assertEquals(new RouteCost(3, 1), cost);
    }

    @Test
    void transferAddsOneTransferAndNoStops() {
        var cost = new RouteCost(2, 1).withTransfer();

        assertEquals(new RouteCost(2, 2), cost);
    }

    @Test
    void negativeStopsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RouteCost(-1, 0));
    }

    @Test
    void negativeTransfersAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RouteCost(0, -1));
    }
}
