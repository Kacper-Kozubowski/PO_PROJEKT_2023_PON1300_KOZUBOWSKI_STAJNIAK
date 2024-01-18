package agh.opp.model.simulation.variations.planting;

import agh.opp.model.elements.Plant;
import agh.opp.model.elements.PoisonousPlant;
import agh.opp.model.tools.Randomizer;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.WorldMap;

import java.util.*;
import java.util.stream.Collectors;

public class PoisonousPlanting extends AbstractPlanting {
    private final Randomizer randomizer = new Randomizer();
    private final Set<Vector2d> poisonousArea = new HashSet<>();
    private final Set<Vector2d> normalArea;

    public PoisonousPlanting(WorldMap map, int height, int width, int startPlant, int energy) {
        super(map, energy);
        normalArea = new HashSet<>(map.getArea());
        getPoisonousArea(height, width);
        plant(startPlant);
    }

    @Override
    public void plant(int toPlant) {
        Set<Vector2d> taken = map.getPlants().keySet();
        List<Vector2d> free = normalArea.stream().filter(i -> !taken.contains(i)).collect(Collectors.toList());
        Collections.shuffle(free);


        for (int planted = 0; planted < toPlant; planted++) {
            boolean mapFull = (free.size() - planted) <= 0;
            if (!mapFull) {
                Vector2d position = free.get(planted);
                if (poisonousArea.contains(position) && randomizer.coinToss()) {
                    Plant plant = new PoisonousPlant(position, plantEnergy);
                    map.addPlant(plant);
                } else {
                    Plant plant = new Plant(position, plantEnergy);
                    map.addPlant(plant);
                }
            } else {
                return;
            }
        }
    }

    private void getPoisonousArea(int height, int width) {
        int size = (height * width) / 5;
        int a = (int) Math.sqrt(size);

        Random random = new Random();
        int x1 = random.nextInt(height - a + 1);
        int y1 = random.nextInt(width - a + 1);

        for (int i = 0; i < a; i++) {
            int x = x1 + i;
            for (int j = 0; j < a; j++) {
                int y = y1 + j;
                poisonousArea.add(new Vector2d(x, y));
            }
        }
    }
}