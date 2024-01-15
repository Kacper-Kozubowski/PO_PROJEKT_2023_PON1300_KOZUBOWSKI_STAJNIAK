package agh.opp.presenter;

import agh.opp.model.elements.Animal;
import agh.opp.model.simulation.variations.Simulation;
import agh.opp.model.tools.Vector2d;
import agh.opp.model.tools.interfaces.MapChangeListener;
import agh.opp.model.tools.interfaces.WorldElement;
import agh.opp.model.tools.interfaces.WorldMap;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class SimulationPresenter implements MapChangeListener {
    @FXML
    private Button statisticsButton;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private GridPane mapGrid;
    @FXML
    private VBox worldStatistics;
    @FXML
    private VBox animalStatistics;
    @FXML
    private Label animalPopulationLabel;
    @FXML
    private Label plantPopulationLabel;
    @FXML
    private Label emptySquaresLabel;
    @FXML
    private Label mostCommonGenomeLabel;
    @FXML
    private Label averageEnergyLabel;
    @FXML
    private Label averageLifespanLabel;
    @FXML
    private Label averageOffspringLabel;
    @FXML
    private ComboBox<Animal> partyContentsBox;
    private Simulation simulation;
    private ExecutorService pool;

    private final static double CELL_WIDTH = 50;
    private final static double CELL_HEIGHT = 50;

    private final BooleanProperty simulationRunningProperty = new SimpleBooleanProperty(false);


    public void setSimulationPool(ExecutorService pool) {this.pool = pool;}
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        simulation.mapChanged();
        simulation.animalPopulationProperty().addListener((source, oldValue, newValue) -> {
            animalPopulationLabel.setText("%d".formatted(newValue.intValue()));
        });
        simulation.plantPopulationProperty().addListener((source, oldValue, newValue) -> {
            plantPopulationLabel.setText("%d".formatted(newValue.intValue()));
        });
        simulation.emptySquaresProperty().addListener((source, oldValue, newValue) -> {
            emptySquaresLabel.setText("%d".formatted(newValue.intValue()));
        });
        simulation.averageEnergyProperty().addListener((source, oldValue, newValue) -> {
            averageEnergyLabel.setText("%d".formatted(newValue.intValue()));
        });
        simulation.averageLifespanProperty().addListener((source, oldValue, newValue) -> {
            averageLifespanLabel.setText("%d".formatted(newValue.intValue()));
        });
        simulation.averageOffspringProperty().addListener((source, oldValue, newValue) -> {
            averageOffspringLabel.setText("%d".formatted(newValue.intValue()));
        });
    }

    @FXML
    private void initialize() {
        stopButton.setDisable(true);
        stopButton.disableProperty().bind(simulationRunningProperty.not());
        startButton.disableProperty().bind(simulationRunningProperty);
    }

    @FXML
    public void onStatisticsClicked(ActionEvent actionEvent) {
        worldStatistics.setVisible(!worldStatistics.isVisible());
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
                        Rectangle rectangle = new Rectangle(CELL_WIDTH, CELL_HEIGHT, Color.TRANSPARENT);
                        insertObject(x, height - y - 1, rectangle);
                        rectangle.setOnMouseClicked(event -> displayPartyContents(map.getAnimalsAt(position)));
                    } else {
                        WorldElement element = optionalWorldElement.get();
                        insertObject(x, height - y - 1, new Label(String.valueOf(element)));
                        if (element instanceof Animal) {
                            Rectangle rectangle = new Rectangle(CELL_WIDTH, CELL_HEIGHT, Color.TRANSPARENT);
                            insertObject(x, height - y - 1, rectangle);
                            rectangle.setOnMouseClicked(event -> displayAnimalInfo((Animal) element));
                        }
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

    private void displayAnimalInfo(Animal animal) {
        animalStatistics.setVisible(!animalStatistics.isVisible());
    }

    private void displayPartyContents(ArrayList<Animal> animals) {
//        partyContentsBox.setVisible(!partyContentsBox.isVisible());
        ObservableList<Animal> options = FXCollections.observableArrayList(animals);
        partyContentsBox.setItems(options);
    }

    @Override
    public void mapChanged(WorldMap map) {
        Platform.runLater(() -> {
            drawMap(map);
        });
    }
}