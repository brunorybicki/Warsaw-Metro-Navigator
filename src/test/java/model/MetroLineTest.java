package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetroLineTest {
    @Test
    void lineKeepsItsName() {
        assertEquals("M1", new MetroLine("M1").lineName());
    }

    @Test
    void linesWithTheSameNameAreEqual() {
        assertEquals(new MetroLine("M2"), new MetroLine("M2"));
    }

    @Test
    void nullNameIsRejected() {
        assertThrows(NullPointerException.class, () -> new MetroLine(null));
    }
}
