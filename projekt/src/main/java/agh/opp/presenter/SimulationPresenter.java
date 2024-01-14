package agh.opp.presenter;

import agh.opp.model.simulation.variations.Simulation;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.MapChangeListener;
import agh.opp.model.tools.interfaces.WorldElement;
import agh.opp.model.tools.interfaces.WorldMap;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class SimulationPresenter implements MapChangeListener {
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private GridPane mapGrid;
    private Simulation simulation;
    private ExecutorService pool;

    private final static double CELL_WIDTH = 50;
    private final static double CELL_HEIGHT = 50;

    private final BooleanProperty simulationRunningProperty = new SimpleBooleanProperty(false);


    public void setSimulationPool(ExecutorService pool) {this.pool = pool;}
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        simulation.mapChanged();
    }

    @FXML
    private void initialize() {
        stopButton.setDisable(true);
        stopButton.disableProperty().bind(simulationRunningProperty.not());
        startButton.disableProperty().bind(simulationRunningProperty);
    }

    @FXML
    public void onStartClicked(ActionEvent actionEvent) {
        simulationRunningProperty.set(true);
        simulation.start();
        pool.execute(simulation);
    }

    @FXML
    public void onStopClicked(ActionEvent actionEvent) {
        simulationRunningProperty.set(false);
        simulation.stop();
    }


    private void drawMap(WorldMap map) {
        clearGrid();
        int width = map.getWidth();
        int height = map.getHeight();

        for (int x = 0; x < width; x++) {mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));}
        for (int y = 0; y < height; y++) {mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));}

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                Vector2d position = new Vector2d(x, y);
                Optional<WorldElement> optionalWorldElement = map.elementAt(position);

                if (optionalWorldElement.isPresent()) {
                    if (map.animalsAt(position) > 1){
                        insertObject(x, height - y - 1, new Label("party"));
                    } else {
                        WorldElement element = optionalWorldElement.get();
                        insertObject(x, height - y - 1, new Label(String.valueOf(element)));
                    }
                }
            }
        }
    }


    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    private void insertObject(int x, int y, Node object) {
        mapGrid.add(object, x, y, 1, 1);
        GridPane.setHalignment(object, HPos.CENTER);
        GridPane.setValignment(object, VPos.CENTER);
    }

    @Override
    public void mapChanged(WorldMap map) {
        Platform.runLater(() -> {
            drawMap(map);
        });
    }
}