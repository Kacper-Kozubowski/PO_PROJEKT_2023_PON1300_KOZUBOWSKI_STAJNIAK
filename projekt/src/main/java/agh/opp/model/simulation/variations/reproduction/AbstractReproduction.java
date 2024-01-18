package agh.opp.model.simulation.variations.reproduction;

import agh.opp.model.elements.Animal;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Randomizer;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.Mutation;
import agh.opp.model.tools.interfaces.ReproductionController;
import agh.opp.model.tools.interfaces.WorldMap;

import java.util.*;

public abstract class AbstractReproduction implements ReproductionController {
    private final WorldMap map;
    private final Mutation mutation;
    private final int genotypeSize;
    private final Randomizer randomizer = new Randomizer();

    private final List<Animal> newAnimals = new LinkedList<>();
    private final int minEnergy;
    private final int reproductionEnergy;
    private int animalsTotal = 1;


    public AbstractReproduction(WorldMap map, int genotypeSize, Mutation mutation, int minEnergy, int reproductionEnergy, int initialAnimals, int initialEnergy) {
        this.map = map;
        this.genotypeSize = genotypeSize;
        this.mutation = mutation;
        this.minEnergy = minEnergy;
        this.reproductionEnergy = reproductionEnergy;
        initializeMap(initialAnimals, initialEnergy);
    }

    @Override
    public void reproduce(Animal animal1, Animal animal2) {
        if ((animal1.getEnergy() < minEnergy) || (animal2.getEnergy() < minEnergy)) return;

        int share1 = (animal1.getEnergy() * 100) / (animal1.getEnergy() + animal2.getEnergy());
        int share2 = 100 - share1;
        int geneAmount1 = (share1 * genotypeSize) / 100;
        int geneAmount2 = genotypeSize - geneAmount1;


        boolean firstBetter = share1 > share2;
        boolean leftBetter = randomizer.coinToss();


        ArrayList<Integer> genes;
        if ((firstBetter && leftBetter) || (!firstBetter && !leftBetter)) {
            genes = makeNewGenotype(animal1, geneAmount1, animal2, geneAmount2);
        } else {
            genes = makeNewGenotype(animal2, geneAmount2, animal1, geneAmount1);
        }

        Animal animal = createNewAnimal(genes, animal1.getPosition(), reproductionEnergy * 2, animalsTotal++);
        addAnimal(animal);

        animal1.newChild(reproductionEnergy, animal);
        animal2.newChild(reproductionEnergy, animal);
    }

    private synchronized void addAnimal(Animal animal) {
        newAnimals.add(animal);
    }

    private ArrayList<Integer> makeNewGenotype(Animal leftAnimal, int left, Animal rightAnimal, int right) {
        List<Integer> leftGenes = leftAnimal.getGenome().getLeftGenes(left);
        List<Integer> rightGenes = rightAnimal.getGenome().getRightGenes(right);
        ArrayList<Integer> genes = new ArrayList<>();
        genes.addAll(leftGenes);
        genes.addAll(rightGenes);
        mutation.mutate(genes);
        return genes;
    }

    protected abstract Animal createNewAnimal(ArrayList<Integer> genes, Vector2d position, int energy, int id);

    public void initializeMap(int initialAnimals, int initialEnergy) {
        Random random = new Random();
        int values = MapOrientation.values().length;
        List<Vector2d> positions = new ArrayList<>(map.getArea());
        for (int i = 0; i < initialAnimals; i++) {
            ArrayList<Integer> genes = new ArrayList<>();
            for (int j = 0; j < genotypeSize; j++) {
                int gene = random.nextInt(values);
                genes.add(gene);
            }

            Collections.shuffle(positions);

            Animal animal = createNewAnimal(genes, positions.get(0), initialEnergy, animalsTotal++);
            map.addAnimal(animal);
        }
    }

    public void addNewAnimals() {
        for (Animal animal : newAnimals) {
            map.addAnimal(animal);
        }
        newAnimals.clear();
    }
}