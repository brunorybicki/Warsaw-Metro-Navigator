package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StationTest {
    @Test
    void stationKeepsItsName() {
        assertEquals("Świętokrzyska", new Station("Świętokrzyska").stationName());
    }

    @Test
    void stationsWithTheSameNameAreEqual() {
        assertEquals(new Station("Centrum"), new Station("Centrum"));
    }

    @Test
    void nullNameIsRejected() {
        assertThrows(NullPointerException.class, () -> new Station(null));
    }
}
