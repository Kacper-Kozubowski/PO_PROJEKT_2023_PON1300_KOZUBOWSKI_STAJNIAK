package agh.opp.model.tools.interfaces;

import agh.opp.model.elements.Animal;

public interface ReproductionController {
    void reproduce(Animal animal1, Animal animal2);
    void initializeMap(int initialAnimals, int initialEnergy);
    void addNewAnimals();
}
