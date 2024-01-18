package agh.opp.model.tools.interfaces;

import java.util.List;

public interface Genome{

    int getCurrentGene();

    int getCurrentGeneIndex();

    List<Integer> getLeftGenes(int n);

    List<Integer> getRightGenes(int n);

    String toString();
}