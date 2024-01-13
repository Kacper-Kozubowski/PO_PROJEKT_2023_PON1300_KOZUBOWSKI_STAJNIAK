package agh.opp.model.tools.genome;

import agh.opp.model.tools.interfaces.Genome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractGenome implements Genome {
    protected final ArrayList<Integer> genes;
    protected int index;

    public AbstractGenome(ArrayList<Integer> genes) {
        this.genes = genes;
        setRandomIndex();
    }

    protected void setRandomIndex(){
        List<Integer> allowed = IntStream.range(0, genes.size())
                .filter(i -> i != index).boxed().collect(Collectors.toList());
        Collections.shuffle(allowed);
        index = allowed.get(0);
    }

    @Override
    public int getCurrentGene(){
        int gene = genes.get(index);
        nextIndex();
        return gene;
    }

    @Override
    public List<Integer> getLeftGenes(int n) {return genes.subList(0, n);}

    @Override
    public List<Integer> getRightGenes(int n){
        int start = genes.size() - n;
        return genes.subList(start, genes.size());
    }

    @Override
    public String toString() {return (genes + " | current ID: " + index);}

    protected abstract void nextIndex();
}
