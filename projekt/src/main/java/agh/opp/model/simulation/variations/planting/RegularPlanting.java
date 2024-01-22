package agh.opp.model.simulation.variations.planting;

import agh.opp.model.tools.Randomizer;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.WorldMap;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RegularPlanting extends AbstractPlanting {
    private final Randomizer random = new Randomizer();
    private final Set<Vector2d> equatorArea = new HashSet<>();
    private final Set<Vector2d> normalArea;


    public RegularPlanting(WorldMap map, int height, int width, int startPlant, int plantEnergy) {
        super(map, plantEnergy);
        generateEquatorArea(height, width);
        normalArea = new HashSet<>(map.getArea());
        normalArea.removeAll(equatorArea);
        plant(startPlant);
    }

    @Override
    public void plant(int toPlant) {
        Set<Vector2d> taken = map.getPlants().keySet();
        List<Vector2d> freeEquator = equatorArea.stream().filter(i -> !taken.contains(i)).collect(Collectors.toList());
        List<Vector2d> freeNormal = normalArea.stream().filter(i -> !taken.contains(i)).collect(Collectors.toList());
        Collections.shuffle(freeEquator);
        Collections.shuffle(freeNormal);

        int toEquator = 0;
        int toNormal = 0;

        for (int i = 0; i < toPlant; i++) {
            boolean equatorFull = (freeEquator.size() - toEquator) <= 0;
            boolean normalFull = (freeNormal.size() - toNormal) <= 0;

            if (!equatorFull && !normalFull) {
                if (pareto()) {
                    addPlant(freeEquator.get(toEquator));
                    toEquator++;
                } else {
                    addPlant(freeNormal.get(toNormal));
                    toNormal++;
                }
            } else if (!equatorFull) {
                addPlant(freeEquator.get(toEquator));
                toEquator++;
            } else if (!normalFull) {
                addPlant(freeNormal.get(toNormal));
                toNormal++;
            } else {
                return;
            }
        }
    }

    private boolean pareto() {return random.randomize(80);}

    private void generateEquatorArea(int height, int width) {
        int size = (height * width) / 5;
        int mid1, mid2;

        if (height % 2 == 0) {
            mid1 = height / 2;
            mid2 = mid1 - 1;
        } else {
            mid1 = mid2 = height / 2;
        }

        while (size > 0) {
            Set<Vector2d> equator = new HashSet<>();
            for (int x = 0; x < width; x++) {
                equator.add(new Vector2d(x, mid1));
                equator.add(new Vector2d(x, mid2));
            }

            if (equator.size() <= size) {
                equatorArea.addAll(equator);
                mid1++;
                mid2--;
                size -= equator.size();
            } else {
                List<Vector2d> random = new ArrayList<>(equator);
                Collections.shuffle(random);
                equatorArea.addAll(random.subList(0, size));
                return;
            }
        }
    }
    @Override
    public Set<Vector2d> getSpecialArea() {
        return equatorArea;
    }
    @Override
    public Color getSpecialAreaColor() {
        return Color.GREEN;
    }
}