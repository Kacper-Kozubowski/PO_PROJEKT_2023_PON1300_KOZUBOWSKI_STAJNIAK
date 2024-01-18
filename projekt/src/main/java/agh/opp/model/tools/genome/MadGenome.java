package agh.opp.model.tools.genome;

import java.util.ArrayList;
import java.util.Random;

public class MadGenome extends AbstractGenome {
    private final Random random = new Random();

    public MadGenome(ArrayList<Integer> genes) {
        super(genes);
    }

    @Override
    protected void nextIndex() {
        if (madness()){
            setRandomIndex();
        } else {
            index = (index + 1) % genes.size();
        }
    }

    private boolean madness() {
        int madness = random.nextInt(100) + 1;
        return (madness >= 80);
    }
}
