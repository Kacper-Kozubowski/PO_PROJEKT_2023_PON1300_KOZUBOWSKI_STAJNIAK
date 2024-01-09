package agh.opp.model.elements;

import agh.opp.model.tools.interfaces.WorldElement;
import agh.opp.model.tools.Vector2d;

public class Plant implements WorldElement {
    private final Vector2d position;
    private final int energy;

    public Plant(Vector2d position, int energy) {
        this.position = position;
        this.energy = energy;
    }

    public int getEnergy() {return energy;}
    @Override
    public Vector2d getPosition() {return position;}

    @Override
    public String toString(){return ".";}
}