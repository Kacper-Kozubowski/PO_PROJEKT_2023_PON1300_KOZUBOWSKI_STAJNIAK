package agh.opp.model.tools.interfaces;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Vector2d;

import java.util.*;

public interface WorldMap {
    Set<Vector2d> getArea();
    int getWidth();
    int getHeight();

    void addAnimal(Animal animal);
    void removeAnimal(Animal animal);
    void moveAnimal(Animal animal, Vector2d newPosition, MapOrientation newOrientation);
    List<Animal> getAnimals();
    List<Animal> hierarchy(Vector2d position);
    Set<Vector2d> getAnimalPositions();

    void addPlant(Plant plant);
    void removePlant(Plant plant);
    Map<Vector2d, Plant> getPlants();
    Optional<Plant> getPlantOnPosition(Vector2d position);

    Optional<WorldElement> elementAt(Vector2d position);
    int animalsAt(Vector2d position);
}