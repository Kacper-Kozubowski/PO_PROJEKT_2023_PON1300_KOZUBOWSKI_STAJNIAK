package agh.opp.model.simulation.variations;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class Simulation implements Runnable {
    private final HashSet<MapChangeListener> observers = new HashSet<>();
    private final MapCleaner cleaner;
    private final MapMovementController movementController;
    private final ReproductionController reproductionController;
    private final PlantingController plantingController;
    private final WorldMap map;
    private final int dailyPlant;
    private boolean stopped = false;


    public Simulation(MapCleaner cleaner, MapMovementController movementController, ReproductionController reproductionController, PlantingController plantingController, WorldMap map, int dailyPlant) {
        this.cleaner = cleaner;
        this.movementController = movementController;
        this.reproductionController = reproductionController;
        this.plantingController = plantingController;
        this.map = map;
        this.dailyPlant = dailyPlant;
    }


    public void run() {
        while (!stopped) {
            cleaner.cleanDeadAnimals();
            movementController.moveAll();
            eatAndReproduce();
            plantingController.plant(dailyPlant);

            mapChanged();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() {
        stopped = false;
    }

    public void stop() {
        stopped = true;
    }

    private void eatAndReproduce() {
        Set<Vector2d> positions = map.getAnimalPositions();
        for (Vector2d position : positions) {
            processField(position, map.getPlantOnPosition(position));
        }
        reproductionController.addNewAnimals();
        cleaner.cleanEatenPlants();
    }

    private void processField(Vector2d position, Optional<Plant> plant) {
        List<Animal> animals = map.hierarchy(position);
        Animal first = animals.get(0);

        if (plant.isPresent()) {
            Plant eatenPlant = plant.get();
            first.eat(eatenPlant);
            cleaner.removePlant(eatenPlant);
        }

        if (animals.size() > 1) {
            Animal second = animals.get(1);
            reproductionController.reproduce(first, second);
        }
    }


    public void subscribe(MapChangeListener observer) {
        observers.add(observer);
    }

    public void unsubscribe(MapChangeListener observer) {
        observers.remove(observer);
    }

    public void mapChanged() {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(map);
        }
    }
}