package agh.opp.model.maps;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
import agh.opp.model.tools.Boundary;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.OutOfBoundaryException;
import agh.opp.model.tools.Vector2d;

import java.util.Optional;

public class RegularMap extends AbstractMap {
    private final Boundary boundary;

    public RegularMap(int width, int height) {
        super(width, height);
        this.boundary = new Boundary(width, height);
    }

    @Override
    public void moveAnimal(Animal animal, Vector2d newPosition, MapOrientation newOrientation){
        try {
            Vector2d normalizedPosition = boundary.normalizePosition(newPosition);
            removeAnimal(animal);
            animal.updatePosition(normalizedPosition);
            animal.updateOrientation(newOrientation);
            addAnimal(animal);
        } catch (OutOfBoundaryException e) {
            animal.updateOrientation(newOrientation.opposite());
        }
    }

    @Override
    public Optional<Plant> getPlantOnPosition(Vector2d position){
        try {
            Vector2d normalizedPosition = boundary.normalizePosition(position);
            return Optional.ofNullable(plants.get(normalizedPosition));
        } catch (OutOfBoundaryException e) {
            return Optional.empty();
        }
    }
}