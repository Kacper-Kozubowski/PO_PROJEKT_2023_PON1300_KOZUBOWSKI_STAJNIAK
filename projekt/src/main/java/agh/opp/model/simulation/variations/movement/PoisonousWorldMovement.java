package agh.opp.model.simulation.variations.movement;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Randomizer;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.WorldMap;

import java.util.Optional;

public class PoisonousWorldMovement extends AbstractMovement{
    private final Randomizer random = new Randomizer();
    private final RegularMovement regularMovement;
    public PoisonousWorldMovement(WorldMap map) {
        super(map);
        regularMovement = new RegularMovement(map);
    }

    @Override
    void moveAnimal(Animal animal) {
        MapOrientation newOrientation = newOrientation(animal);
        Vector2d newPosition = newPosition(animal, newOrientation);
        move(animal, newPosition, newOrientation);
    }

    private void move(Animal animal, Vector2d newPosition, MapOrientation newOrientation){
        Optional<Plant> plant = map.getPlantOnPosition(newPosition);
        if (plant.isPresent() && poisonous(plant.get()) && dodge()){
            regularMovement.moveAnimal(animal);
        } else {
            map.moveAnimal(animal, newPosition, newOrientation);
        }
    }

    private boolean poisonous(Plant plant){return (plant.getEnergy() < 0);}

    private boolean dodge() {return random.randomize(20);}
}