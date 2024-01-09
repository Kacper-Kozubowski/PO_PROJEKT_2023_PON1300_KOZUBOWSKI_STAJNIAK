package agh.opp.model.elements;

import agh.opp.model.tools.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlantTest {
    private final Plant plant = new Plant(new Vector2d(10, 10), 100);

    @Test
    void getEnergy() {
        assertEquals(100, plant.getEnergy());
    }
}