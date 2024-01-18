package agh.opp.model.simulation.variations.planting;

import agh.opp.model.elements.Plant;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.PlantingController;
import agh.opp.model.tools.interfaces.WorldMap;


public abstract class AbstractPlanting implements PlantingController {
    protected final WorldMap map;
    protected final int plantEnergy;

    public AbstractPlanting(WorldMap map, int energy) {
        this.map = map;
        this.plantEnergy = energy;
    }

    protected void addPlant(Vector2d position){
        Plant plant = new Plant(position, plantEnergy);
        map.addPlant(plant);
    }
}