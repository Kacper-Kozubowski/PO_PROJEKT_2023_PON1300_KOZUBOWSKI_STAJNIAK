package agh.opp.model.elements;

import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.genome.RegularGenome;
import agh.opp.model.tools.interfaces.Genome;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    private final ArrayList<Integer> genes = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7));
    private final Genome regularGenome = new RegularGenome(genes);
    private final Vector2d position = new Vector2d(10, 10);
    private final int energy = 100;
    private final Animal regularAnimal = new Animal(regularGenome, position, energy, 1);


    @Test
    void getNextTurn() {
        int turn = regularAnimal.getNextTurn();
        int index = genes.indexOf(turn);
        int nextTurn = genes.get((index + 1) % genes.size());
        assertEquals(nextTurn, regularAnimal.getNextTurn());
    }

    @Test
    void updatePosition() {
        Vector2d newPosition = new Vector2d(100, 100);
        regularAnimal.updatePosition(newPosition);
        assertEquals(newPosition, regularAnimal.getPosition());
        assertNotEquals(position, regularAnimal.getPosition());
    }

    @Test
    void updateOrientation() {
        MapOrientation oldOrientation = regularAnimal.getOrientation();
        MapOrientation newOrientation = oldOrientation.opposite();
        regularAnimal.updateOrientation(newOrientation);
        assertEquals(newOrientation, regularAnimal.getOrientation());
        assertNotEquals(oldOrientation, regularAnimal.getOrientation());
    }

    @Test
    void meal() {
        Plant plant = new Plant(position, energy);
        regularAnimal.eat(plant);
        assertEquals(energy*2,regularAnimal.getEnergy());
        assertNotEquals(energy, regularAnimal.getEnergy());
    }

    @Test
    void newChild(){
        int lostEnergy = 50;
        regularAnimal.newChild(lostEnergy);
        assertEquals(energy-lostEnergy, regularAnimal.getEnergy());
        assertEquals(1, regularAnimal.totalChildren());
    }

    @Test
    void age() {
        assertEquals(0, regularAnimal.getAge());
        regularAnimal.age();
        assertEquals(1, regularAnimal.getAge());
    }

    @Test
    void dead() {
        assertFalse(regularAnimal.dead());
        regularAnimal.newChild(energy);
        assertTrue(regularAnimal.dead());
    }
}