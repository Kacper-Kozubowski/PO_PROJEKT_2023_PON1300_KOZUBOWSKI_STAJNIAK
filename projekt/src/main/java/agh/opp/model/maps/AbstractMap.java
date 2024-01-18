package agh.opp.model.maps;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
import agh.opp.model.tools.MapOrientation;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.WorldElement;
import agh.opp.model.tools.interfaces.WorldMap;

import java.util.*;

public abstract class AbstractMap implements WorldMap {
    private final Map<Vector2d, HashSet<Animal>> animals = new HashMap<>();
    protected final Map<Vector2d, Plant> plants = new HashMap<>();
    private final Set<Vector2d> area = new HashSet<>();
    private final int width;
    private final int height;


    public AbstractMap(int width, int height){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                area.add(new Vector2d(i, j));
            }
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public Set<Vector2d> getArea() {return area;}

    @Override
    public int getHeight() {return height;}

    @Override
    public int getWidth() {return width;}


    private synchronized HashSet<Animal> getAnimalField(Animal animal){
        Vector2d position = animal.getPosition();
        if (animals.containsKey(position)){
            return animals.get(position);
        }
        else{
            HashSet<Animal> mapField = new HashSet<>();
            animals.put(position, mapField);
            return mapField;
        }
    }

    @Override
    public synchronized void addAnimal(Animal animal){
        HashSet<Animal> mapField = getAnimalField(animal);
        mapField.add(animal);
    }

    @Override
    public synchronized void removeAnimal(Animal animal){
        Vector2d position = animal.getPosition();
        HashSet<Animal> mapField = animals.get(position);
        if (mapField.size() > 1){
            mapField.remove(animal);
        } else {
            animals.remove(position);
        }
    }

    @Override
    public void moveAnimal(Animal animal, Vector2d newPosition, MapOrientation newOrientation){
        removeAnimal(animal);
        animal.updatePosition(newPosition);
        animal.updateOrientation(newOrientation);
        addAnimal(animal);
    }

    @Override
    public List<Animal> getAnimals() {return animals.values()
            .stream()
            .flatMap(Set::stream).toList();
    }
    public ArrayList<Animal> getAnimalsAt(Vector2d position) {
        return new ArrayList<>(animals.get(position));
    }
    @Override
    public List<Animal> hierarchy(Vector2d position){
        return animals.get(position).stream().sorted(Comparator
                .comparing(Animal::getEnergy, Comparator.reverseOrder())
                .thenComparing(Animal::getAge, Comparator.reverseOrder())
                .thenComparing(Animal::totalChildren, Comparator.reverseOrder())).toList();
    }

    @Override
    public Set<Vector2d> getAnimalPositions(){return animals.keySet();}


    @Override
    public synchronized void addPlant(Plant plant){
        Vector2d position = plant.getPosition();
        plants.put(position, plant);
    }

    @Override
    public synchronized void removePlant(Plant plant){plants.remove(plant.getPosition());}

    @Override
    public Map<Vector2d, Plant> getPlants() {return plants;}

    @Override
    public Optional<Plant> getPlantOnPosition(Vector2d position){
        return Optional.ofNullable(plants.get(position));
    }

    @Override
    public Optional<WorldElement> elementAt(Vector2d position) {
        if (animals.containsKey(position)) {
            return Optional.ofNullable(hierarchy(position).get(0));
        }
        return Optional.ofNullable(plants.get(position));
    }

    @Override
    public int animalsAt(Vector2d position){
        if (animals.containsKey(position)){
            return animals.get(position).size();
        } else {
            return 0;
        }
    }
}