package agh.opp.model.elements;

import agh.opp.model.tools.interfaces.Genome;
import agh.opp.model.tools.interfaces.WorldElement;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Vector2d;


public class Animal implements WorldElement {
    private final Genome genome;
    private Vector2d position;
    private MapOrientation orientation;
    private int energy;
    private int age = 0;
    private int children = 0;
    private final String id;

    public Animal(Genome genome, Vector2d position, int energy, int id) {
        this.genome = genome;
        this.position = position;
        this.energy = energy;
        this.orientation = MapOrientation.generateRandom();
        this.id = "Z" + id;
    }

    public int getNextTurn() {return genome.getCurrentGene();}

    public void updatePosition(Vector2d position) {this.position = position;}

    public void updateOrientation(MapOrientation orientation){this.orientation = orientation;}

    public void newChild(int lostEnergy) {
        energy -= lostEnergy;
        children++;
    }

    public void eat(Plant plant) {energy += plant.getEnergy();}

    public void age() {
        age++;
        energy--;
    }

    public boolean dead(){return (energy <= 0);}

    @Override
    public Vector2d getPosition() {return position;}

    public MapOrientation getOrientation() {return orientation;}

    public Genome getGenome() {return genome;}

    public int getAge() {return age;}

    public int getEnergy() {return energy;}

    public int totalChildren() {return children;}

    public String getId(){return id;}

    @Override
    public String toString(){
        if (dead()) return "X";
        return getId() + " | "+ getEnergy();}
}