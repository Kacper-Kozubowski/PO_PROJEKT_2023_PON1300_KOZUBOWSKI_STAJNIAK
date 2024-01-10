package agh.opp.model.tools;

import java.util.Random;

public class Randomizer {
    private final Random random = new Random();
    public boolean randomize(int chance) {
        int result = random.nextInt(100) + 1;
        return (result <= chance);
    }

    public boolean coinToss() {
        int coinThrow = random.nextInt(2);
        return (coinThrow == 0);
    }
}