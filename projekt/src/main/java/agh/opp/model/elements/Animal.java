package agh.opp.model.elements;

import agh.opp.model.tools.interfaces.Genome;
import agh.opp.model.tools.interfaces.WorldElement;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Vector2d;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

import java.util.*;


public class Animal implements WorldElement {
    private final Genome genome;
    private Vector2d position;
    private MapOrientation orientation;

    private IntegerProperty energy = new SimpleIntegerProperty();
    private IntegerProperty age = new SimpleIntegerProperty();
    private IntegerProperty children = new SimpleIntegerProperty();
    private IntegerProperty plantsEaten = new SimpleIntegerProperty();
    private IntegerProperty descendants = new SimpleIntegerProperty();
    private Set<Animal> descendantsSet = new HashSet<>();
    private Animal lastChild;
    private Set<Animal> childrenSet = new HashSet<>();
    private Map<Animal, ChangeListener<Number>> animalListeners = new HashMap<>();
    private final String id;

    public Animal(Genome genome, Vector2d position, int energy, int id) {
        this.genome = genome;
        this.position = position;
        this.energy.set(energy);
        this.orientation = MapOrientation.generateRandom();
        this.id = "Z" + id;
    }

    public int getNextTurn() {return genome.getCurrentGene();}

    public void updatePosition(Vector2d position) {this.position = position;}

    public void updateOrientation(MapOrientation orientation){this.orientation = orientation;}

    public void newChild(int lostEnergy, Animal child) {
        energy.set(energy.get() - lostEnergy);
        childrenSet.add(child);
        lastChild = child;
        children.set(children.get() + 1);

    }

    public void eat(Plant plant) {
        energy.set(energy.get() + plant.getEnergy());
        plantsEaten.set(plantsEaten.get() + 1);
    }

    public void age() {
        age.set(age.get() + 1);
        energy.set(energy.get() - 1);
    }

    public boolean dead(){return (energy.get() <= 0);}

    @Override
    public Vector2d getPosition() {return position;}

    public MapOrientation getOrientation() {return orientation;}

    public Genome getGenome() {return genome;}

    public int getAge() {return age.get();}

    public int getEnergy() {return energy.get();}

    public int totalChildren() {return children.get();}
    public int getDescendants() {return descendants.get();}
    public int getPlantsEaten() {return plantsEaten.get();}
    public Set<Animal> getDescendantsSet() {return descendantsSet;}

    public String getId(){return id;}

    public IntegerProperty energyProperty() {return energy;}
    public IntegerProperty ageProperty() {return age;}
    public IntegerProperty childrenProperty() {return children;}
    public IntegerProperty plantsEatenProperty() {return plantsEaten;}
    public IntegerProperty descendantsProperty() {return descendants;}

    public void initializeDescendancy() {
        descendantsSet = findAllDescendants();
        descendants.set(descendantsSet.size() - 1);
        for (Animal animal : descendantsSet) {
            addChildListener(animal);
        }
    }
    public void removeDescendancy() {
        this.descendantsSet = new HashSet<>();
        this.descendants.set(0);

        Set<Animal> animals = animalListeners.keySet();
        for (Animal animal: animals) {
            animal.childrenProperty().removeListener(animalListeners.get(animal));
        }
    }

    private void addChildListener(Animal animal){
        javafx.beans.value.ChangeListener<Number> listener = (source, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (!this.descendantsSet.contains(animal.lastChild)) {
                    this.descendants.set(descendants.get() + 1);
                    this.descendantsSet.add(animal.lastChild);
                    this.addChildListener(animal.lastChild);
                }
            });
        };
        animalListeners.put(animal, listener);
        animal.childrenProperty().addListener(listener);
    }
    private Set<Animal> findAllDescendants() {
        Set<Animal> childrenBelow = new HashSet<>();
        for (Animal child: childrenSet) {
            childrenBelow.addAll(child.findAllDescendants());
        }
        childrenBelow.add(this);
        return childrenBelow;
    }

    @Override
    public String toString(){
        if (dead()) return "X";
        return getId() + " | "+ getEnergy();}
}