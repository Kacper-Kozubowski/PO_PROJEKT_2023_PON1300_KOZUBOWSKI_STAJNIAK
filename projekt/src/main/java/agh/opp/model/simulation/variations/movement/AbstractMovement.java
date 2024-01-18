package agh.opp.model.simulation.variations.movement;

import agh.opp.model.elements.Animal;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.MapMovementController;
import agh.opp.model.tools.interfaces.WorldMap;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMovement implements MapMovementController {

    protected final WorldMap map;

    public AbstractMovement(WorldMap map) {this.map = map;}

    @Override
    public void moveAll() {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Animal> animals = map.getAnimals();

        for (Animal animal: animals){
            animal.age();
            pool.execute(() -> moveAnimal(animal));
        }

        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Movement controller thread was interrupted while waiting for termination.", e);
        }
    }

    protected MapOrientation newOrientation(Animal animal){
        int turn = animal.getNextTurn();
        return animal.getOrientation().turnRight(turn);
    }

    protected Vector2d newPosition(Animal animal, MapOrientation newOrientation){
        Vector2d step = newOrientation.toVector();
        return animal.getPosition().add(step);
    }

    abstract void moveAnimal(Animal animal);
}