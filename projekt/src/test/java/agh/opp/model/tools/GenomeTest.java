package agh.opp.model.tools;

import agh.opp.model.tools.genome.MadGenome;
import agh.opp.model.tools.genome.RegularGenome;
import agh.opp.model.tools.interfaces.Genome;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {
    private final ArrayList<Integer> genes = new ArrayList<>(List.of(0,1,2,3,4,5,6,7));
    private final Genome regularGenome = new RegularGenome(genes);
    private final Genome madGenome = new MadGenome(genes);

    @Test
    void getCurrentGeneRegular(){
        int gene = regularGenome.getCurrentGene();
        int index = genes.indexOf(gene);
        int nextGene = genes.get((index + 1) % 8);
        assertEquals(nextGene, regularGenome.getCurrentGene());
    }

    private final List<Integer> left4 = List.of(0, 1, 2, 3);
    @Test
    void getLeftGenesRegular() {assertIterableEquals(left4, regularGenome.getLeftGenes(4));}
    @Test
    void getLeftGenesMad() {assertIterableEquals(left4, madGenome.getLeftGenes(4));}


    private final List<Integer> right4 = List.of( 4, 5, 6, 7);
    @Test
    void getRightGenesRegular() {assertIterableEquals(right4, regularGenome.getRightGenes(4));}
    @Test
    void getRightGenesMadr() {assertIterableEquals(right4, madGenome.getRightGenes(4));}
}