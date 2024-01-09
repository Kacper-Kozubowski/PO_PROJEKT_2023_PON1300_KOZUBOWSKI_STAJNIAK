package agh.opp.model.elements;

import agh.opp.model.tools.Vector2d;

public class PoisonousPlant extends Plant{
    public PoisonousPlant(Vector2d position, int energy) {
        super(position, -energy);
    }
    @Override
    public String toString(){return "*";}
}