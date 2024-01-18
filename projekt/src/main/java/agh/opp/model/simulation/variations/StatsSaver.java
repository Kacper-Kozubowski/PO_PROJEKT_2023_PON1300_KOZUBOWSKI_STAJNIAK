package agh.opp.model.simulation.variations;

import agh.opp.model.tools.interfaces.MapChangeListener;
import agh.opp.model.tools.interfaces.WorldMap;
import agh.opp.presenter.SimulationPresenter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StatsSaver implements MapChangeListener {
    private final Simulation simulation;
    private final File csvFile;
    private FileWriter fileWriter;
    private ArrayList<String> stats = new ArrayList<>();

    public StatsSaver(Simulation simulation){
        this.csvFile = new File("./Statystyki.csv");
        this.simulation = simulation;
    }

    public void setStatistics(){
        this.stats = new ArrayList<>();
        this.stats.add("%d".formatted(simulation.animalPopulationProperty().get()));
        this.stats.add("%d".formatted(simulation.plantPopulationProperty().get()));
        this.stats.add("%d".formatted(simulation.emptySquaresProperty().get()));
        this.stats.add("%s".formatted(simulation.mostCommonGenomeProperty().get()));
        this.stats.add("%.2f".formatted(simulation.averageEnergyProperty().get()));
        this.stats.add("%.2f".formatted(simulation.averageLifespanProperty().get()));
        this.stats.add("%.2f".formatted(simulation.averageOffspringProperty().get()));
    }
    @Override
    public void mapChanged(WorldMap map) {
        setStatistics();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.csvFile, true))) {
            StringBuilder rowBuilder = new StringBuilder();
            for(String value: this.stats) {
                rowBuilder.append(value);
                rowBuilder.append(";");
            }
            writer.write(rowBuilder.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
