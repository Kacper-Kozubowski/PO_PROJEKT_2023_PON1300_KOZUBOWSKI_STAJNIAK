package agh.opp.model.simulation.variations.reproduction;

import agh.opp.model.elements.Animal;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.genome.MadGenome;
import agh.opp.model.tools.interfaces.Mutation;
import agh.opp.model.tools.interfaces.WorldMap;

import java.util.ArrayList;

public class MadAnimalsReproduction extends AbstractReproduction{
    public MadAnimalsReproduction(WorldMap map, int genotypeSize, Mutation mutation, int minEnergy, int reproductionEnergy, int initialAnimals, int initialEnergy) {
        super(map, genotypeSize, mutation, minEnergy, reproductionEnergy, initialAnimals, initialEnergy);
    }

    @Override
    protected Animal createNewAnimal(ArrayList<Integer> genes, Vector2d position, int energy, int id) {
        MadGenome genome = new MadGenome(genes);
        return new Animal(genome, position, energy, id);
    }
}