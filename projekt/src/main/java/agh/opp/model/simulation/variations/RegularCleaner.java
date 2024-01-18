package agh.opp.model.simulation.variations;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
import agh.opp.model.tools.interfaces.MapCleaner;
import agh.opp.model.tools.interfaces.WorldMap;

import java.util.LinkedList;
import java.util.List;

public class RegularCleaner implements MapCleaner {
    private final WorldMap map;
    private final List<Plant> eatenPlants = new LinkedList<>();

    public RegularCleaner(WorldMap map) {this.map = map;}

    @Override
    public void cleanDeadAnimals() {
        List<Animal> deadAnimals = map.getAnimals().stream().filter(Animal::dead).toList();

        for (Animal animal: deadAnimals){
            map.removeAnimal(animal);
        }
    }

    @Override
    public void cleanEatenPlants() {
        for (Plant plant: eatenPlants){
            map.removePlant(plant);
        }
        eatenPlants.clear();
    }

    @Override
    public synchronized void removePlant(Plant plant) {eatenPlants.add(plant);}
}