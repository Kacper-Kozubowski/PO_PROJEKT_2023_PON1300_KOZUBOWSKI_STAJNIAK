package agh.opp.model.simulation.variations;

import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.interfaces.Mutation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegularMutation implements Mutation {
    private final Random random = new Random();
    private final List<Integer> allGenes = IntStream.range(0, MapOrientation.values().length).boxed().collect(Collectors.toList());
    private final int minMutation;
    private final int maxMutation;
    private final int genesLength;

    public RegularMutation(int minMutation, int maxMutation, int genesLength) {
        this.minMutation = minMutation;
        this.maxMutation = maxMutation;
        this.genesLength = genesLength;
    }

    @Override
    public void mutate(ArrayList<Integer> genes) {
        int totalMutations = Math.min(random.nextInt(maxMutation + 1) + minMutation, genesLength);
        List<Integer> toMutate = IntStream.range(0, genesLength).boxed().collect(Collectors.toList());
        Collections.shuffle(toMutate);
        changeGenes(genes, toMutate.subList(0, totalMutations));
    }

    private void changeGenes(ArrayList<Integer> genes, List<Integer> toMutate) {
        for (int index : toMutate) {
            int oldGene = genes.get(index);
            int gene = randomGene(oldGene);
            genes.set(index, gene);
        }
    }

    private int randomGene(int gene) {
        Collections.shuffle(allGenes);
        return allGenes.stream().filter(i -> i != gene).toList().get(0);
    }
}