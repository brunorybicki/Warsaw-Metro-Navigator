package routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void fewerStopsWinsEvenWithMoreTransfers() {
        var fewerStops = new RouteCost(3, 5);
        var moreStops = new RouteCost(4, 0);

        assertTrue(fewerStops.compareTo(moreStops) < 0);
        assertTrue(moreStops.compareTo(fewerStops) > 0);
    }

    @Test
    void fewerTransfersWinsWhenStopsAreEqual() {
        var fewerTransfers = new RouteCost(3, 1);
        var moreTransfers = new RouteCost(3, 2);

        assertTrue(fewerTransfers.compareTo(moreTransfers) < 0);
    }

    @Test
    void equalCostsCompareAsEqual() {
        assertEquals(0, new RouteCost(3, 1).compareTo(new RouteCost(3, 1)));
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
