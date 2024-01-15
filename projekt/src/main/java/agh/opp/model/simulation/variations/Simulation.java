package agh.opp.model.simulation.variations;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Simulation implements Runnable {
    private final HashSet<MapChangeListener> observers = new HashSet<>();
    private final MapCleaner cleaner;
    private final MapMovementController movementController;
    private final ReproductionController reproductionController;
    private final PlantingController plantingController;
    private final WorldMap map;
    private final int dailyPlant;
    private boolean stopped = false;

    private IntegerProperty animalPopulation = new SimpleIntegerProperty();
    private IntegerProperty plantPopulation = new SimpleIntegerProperty();
    private IntegerProperty emptySquares = new SimpleIntegerProperty();
    private IntegerProperty averageEnegry = new SimpleIntegerProperty();
    private IntegerProperty averageLifespan = new SimpleIntegerProperty();
    private IntegerProperty averageOffspring = new SimpleIntegerProperty();
    private int deadAnimals = 0;


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

            Platform.runLater(this::updateStatistics);
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

    public void updateStatistics() {
        int animalPopulation = 0;
        int offspringCount = 0;
        int totalLifeSpan = averageLifespan.get() * deadAnimals;
        int totalEnergy = 0;
        Set<Vector2d> worldElementsPositions = Stream.of(map.getAnimalPositions(), map.getPlants().keySet()).flatMap(Set::stream).collect(Collectors.toSet());

        for (Animal animal: map.getAnimals()) {
            animalPopulation++;
            offspringCount += animal.totalChildren();
            totalEnergy += animal.getEnergy();

            if (animal.dead()) {
                totalLifeSpan += animal.getAge();
                this.deadAnimals++;
            }
        }

        this.animalPopulation.set(animalPopulation);
        this.averageOffspring.set(offspringCount / animalPopulation);
        this.plantPopulation.set(map.getPlants().size());
        this.emptySquares.set((map.getWidth() * map.getHeight()) - worldElementsPositions.size());
        this.averageEnegry.set(totalEnergy / animalPopulation);
        this.averageLifespan.set(totalLifeSpan / deadAnimals);
    }

    public IntegerProperty animalPopulationProperty() {return animalPopulation;}
    public IntegerProperty plantPopulationProperty() {return plantPopulation;}
    public IntegerProperty emptySquaresProperty() {return emptySquares;}
    public IntegerProperty averageEnergyProperty() {return averageEnegry;}
    public IntegerProperty averageLifespanProperty() {return averageLifespan;}
    public IntegerProperty averageOffspringProperty() {return averageOffspring;}

    public void mapChanged() {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(map);
        }
    }
}