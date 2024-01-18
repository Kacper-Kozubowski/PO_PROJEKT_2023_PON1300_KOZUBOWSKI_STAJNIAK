package agh.opp.model.simulation.variations.movement;

import agh.opp.model.elements.Animal;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.WorldMap;

public class RegularMovement extends AbstractMovement{
    public RegularMovement(WorldMap map) {super(map);}

    @Override
    void moveAnimal(Animal animal) {
        MapOrientation newOrientation = newOrientation(animal);
        Vector2d newPosition = newPosition(animal, newOrientation);
        map.moveAnimal(animal, newPosition, newOrientation);
    }
}