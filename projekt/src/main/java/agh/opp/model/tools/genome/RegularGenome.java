package agh.opp.model.tools.genome;


import java.util.ArrayList;

public class RegularGenome extends AbstractGenome {
    public RegularGenome(ArrayList<Integer> genes) {super(genes);}

    @Override
    protected void nextIndex() {index = (index + 1) % genes.size();}
}
