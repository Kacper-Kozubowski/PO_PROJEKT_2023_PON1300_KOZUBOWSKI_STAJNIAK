package agh.opp.model.tools.interfaces;

import java.util.List;

public interface Genome{

    int getCurrentGene();

    List<Integer> getLeftGenes(int n);

    List<Integer> getRightGenes(int n);

    String toString();
}