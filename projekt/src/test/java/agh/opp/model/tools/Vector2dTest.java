package agh.opp.model.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2dTest {
    private final Vector2d vector = new Vector2d(5, 10);

    @Test
    void testToString() {
        String string = "(5, 10)";
        assertEquals(string, vector.toString());
    }

    @Test
    void addVector() {
        Vector2d newVector = new Vector2d(10, 10);
        Vector2d finalVector = new Vector2d(15, 20);
        assertEquals(finalVector, vector.add(newVector));
    }

    @Test
    void addNegativeVector() {
        Vector2d newVector = new Vector2d(-10, -10);
        Vector2d finalVector = new Vector2d(-5, 0);
        assertEquals(finalVector, vector.add(newVector));
    }

    @Test
    void x() {
        assertEquals(5, vector.x());
    }

    @Test
    void y() {
        assertEquals(10, vector.y());
    }
}