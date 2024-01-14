package agh.opp.model.tools.interfaces;

import agh.opp.model.elements.Plant;

public interface MapCleaner {
    void cleanDeadAnimals();
    void cleanEatenPlants();
    void removePlant(Plant plant);
}