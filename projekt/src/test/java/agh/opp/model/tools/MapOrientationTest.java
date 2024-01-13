package agh.opp.model.tools;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MapOrientationTest {

    @Test
    void toVector() {
        assertEquals(new Vector2d(0, 1), MapOrientation.NORTH.toVector());
        assertEquals(new Vector2d(1, 1), MapOrientation.NORTHEAST.toVector());
        assertEquals(new Vector2d(1, 0), MapOrientation.EAST.toVector());
        assertEquals(new Vector2d(1, -1), MapOrientation.SOUTHEAST.toVector());
        assertEquals(new Vector2d(0, -1), MapOrientation.SOUTH.toVector());
        assertEquals(new Vector2d(-1, -1), MapOrientation.SOUTHWEST.toVector());
        assertEquals(new Vector2d(-1, 0), MapOrientation.WEST.toVector());
        assertEquals(new Vector2d(-1, 1), MapOrientation.NORTHWEST.toVector());
    }

    @Test
    void turnRightFirst() {
        MapOrientation first = MapOrientation.values()[0];
        assertEquals(MapOrientation.values()[0], first.turnRight(0));
        assertEquals(MapOrientation.values()[1], first.turnRight(1));
        assertEquals(MapOrientation.values()[2], first.turnRight(2));
        assertEquals(MapOrientation.values()[3], first.turnRight(3));
        assertEquals(MapOrientation.values()[4], first.turnRight(4));
        assertEquals(MapOrientation.values()[5], first.turnRight(5));
        assertEquals(MapOrientation.values()[6], first.turnRight(6));
        assertEquals(MapOrientation.values()[7], first.turnRight(7));
    }

    @Test
    void turnRightLast() {
        MapOrientation last = MapOrientation.values()[7];
        assertEquals(MapOrientation.values()[7], last.turnRight(0));
        assertEquals(MapOrientation.values()[0], last.turnRight(1));
        assertEquals(MapOrientation.values()[1], last.turnRight(2));
        assertEquals(MapOrientation.values()[2], last.turnRight(3));
        assertEquals(MapOrientation.values()[3], last.turnRight(4));
        assertEquals(MapOrientation.values()[4], last.turnRight(5));
        assertEquals(MapOrientation.values()[5], last.turnRight(6));
        assertEquals(MapOrientation.values()[6], last.turnRight(7));
    }

    @Test
    void opposite() {
        assertEquals(MapOrientation.SOUTH, MapOrientation.NORTH.opposite());
        assertEquals(MapOrientation.SOUTHWEST, MapOrientation.NORTHEAST.opposite());
        assertEquals(MapOrientation.WEST, MapOrientation.EAST.opposite());
        assertEquals(MapOrientation.NORTHWEST, MapOrientation.SOUTHEAST.opposite());
        assertEquals(MapOrientation.NORTH, MapOrientation.SOUTH.opposite());
        assertEquals(MapOrientation.NORTHEAST, MapOrientation.SOUTHWEST.opposite());
        assertEquals(MapOrientation.EAST, MapOrientation.WEST.opposite());
        assertEquals(MapOrientation.SOUTHEAST, MapOrientation.NORTHWEST.opposite());
    }

    @Test
    void values() {
        MapOrientation[] values = {MapOrientation.NORTH, MapOrientation.NORTHEAST, MapOrientation.EAST, MapOrientation.SOUTHEAST, MapOrientation.SOUTH, MapOrientation.SOUTHWEST, MapOrientation.WEST, MapOrientation.NORTHWEST};
        assertArrayEquals(values, MapOrientation.values());
    }
}