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
import javafx.beans.value.ChangeListener;
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
import javafx.scene.shape.Circle;
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
    @FXML
    private Label genomeLabel;
    @FXML
    private Label currentGeneLabel;
    @FXML
    private Label energyLabel;
    @FXML
    private Label plantsEatenLabel;
    @FXML
    private Label childrenLabel;
    @FXML
    private Label descendantsLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label followedAnimalTextLabel;
    @FXML
    private Label followedAnimalLabel;
    private Simulation simulation;
    private ExecutorService pool;
    private Animal followedAnimal = null;
    private ChangeListener<Number> energyListener;
    private ChangeListener<Number> plantsEatenListener;
    private ChangeListener<Number> childrenListener;
    private ChangeListener<Number> descendantsListener;
    private ChangeListener<Number> ageListener;
    private Circle cirlce;
    private final static double maxCELL_WIDTH = 600;
    private final static double maxCELL_HEIGHT = 600;
    private double CELL_SIZE;

    private final BooleanProperty simulationRunningProperty = new SimpleBooleanProperty(false);


    public void setSimulationPool(ExecutorService pool) {this.pool = pool;}
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        CELL_SIZE = Math.min((maxCELL_WIDTH / (simulation.getMap().getHeight() + 1)), (maxCELL_HEIGHT / (simulation.getMap().getWidth() + 1)));
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
        simulation.mostCommonGenomeProperty().addListener((source, oldValue, newValue) -> {
            mostCommonGenomeLabel.setText(newValue.toString());
        });
        simulation.averageEnergyProperty().addListener((source, oldValue, newValue) -> {
            averageEnergyLabel.setText("%.2f".formatted(newValue.floatValue()));
        });
        simulation.averageLifespanProperty().addListener((source, oldValue, newValue) -> {
            averageLifespanLabel.setText("%.2f".formatted(newValue.floatValue()));
        });
        simulation.averageOffspringProperty().addListener((source, oldValue, newValue) -> {
            averageOffspringLabel.setText("%.2f".formatted(newValue.floatValue()));
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

        for (int x = 0; x < width; x++) {mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));}
        for (int y = 0; y < height; y++) {mapGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE));}

        if (followedAnimal != null) {markFollowedAnimal();}

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                Vector2d position = new Vector2d(x, y);
                Optional<WorldElement> optionalWorldElement = map.elementAt(position);

                if (optionalWorldElement.isPresent()) {
                    if (map.animalsAt(position) > 1){
                        insertObject(x, height - y - 1, new Label("party"));
                        Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE, Color.TRANSPARENT);
                        insertObject(x, height - y - 1, rectangle);
                        rectangle.setOnMouseClicked(event -> displayPartyContents(map.getAnimalsAt(position)));
                    } else {
                        WorldElement element = optionalWorldElement.get();
                        insertObject(x, height - y - 1, new Label(String.valueOf(element)));
                        if (element instanceof Animal) {
                            Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE, Color.TRANSPARENT);
                            insertObject(x, height - y - 1, rectangle);
                            rectangle.setOnMouseClicked(event -> togglAnimalInfo((Animal) element));
                        }
                    }
                }
            }
        }
    }

    private void markFollowedAnimal() {
        Vector2d pos = followedAnimal.getPosition();
        Circle circle = new Circle(CELL_SIZE*0.2);
        circle.setOpacity(0.5);
        circle.setFill(Color.CRIMSON);
        insertObject(pos.x(), simulation.getMap().getHeight() - pos.y() - 1, circle);
        circle.setOnMouseClicked(event -> togglAnimalInfo(followedAnimal));
        this.cirlce = circle;

    }
    private void unmarkFollowedAnimal() {
        this.followedAnimal = null;
        cirlce.setFill(Color.TRANSPARENT);
        mapGrid.getChildren().remove(cirlce);
    }
    private void unfollowAnimal() {
        if (this.followedAnimal != null) {
            followedAnimal.energyProperty().removeListener(energyListener);
            followedAnimal.plantsEatenProperty().removeListener(plantsEatenListener);
            followedAnimal.childrenProperty().removeListener(childrenListener);
            followedAnimal.removeDescendancy();
            followedAnimal.descendantsProperty().removeListener(descendantsListener);
            followedAnimal.ageProperty().removeListener(ageListener);

            unmarkFollowedAnimal();
            this.followedAnimal = null;
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

    private void togglAnimalInfo(Animal animal) {
        if (this.followedAnimal != null) {
            if (this.followedAnimal.equals(animal)) {
                animalStatistics.setVisible(false);
                followedAnimalTextLabel.setVisible(false);
                followedAnimalLabel.setVisible(false);
                followedAnimalLabel.setText("");
                unfollowAnimal();
                return;
            }
            unfollowAnimal();
        }
        animalStatistics.setVisible(true);
        followedAnimalTextLabel.setVisible(true);
        followedAnimalLabel.setVisible(true);
        followedAnimalLabel.setText(animal.toString());
        followAnimal(animal);
    }

    private void followAnimal(Animal animal) {
        this.followedAnimal = animal;
        markFollowedAnimal();

        genomeLabel.setText((animal.getGenome().toString()));
        currentGeneLabel.setText("%d".formatted(animal.getGenome().getCurrentGeneIndex()));
        energyLabel.setText("%d".formatted(animal.getEnergy()));
        plantsEatenLabel.setText("%d".formatted(animal.getPlantsEaten()));
        childrenLabel.setText("%d".formatted(animal.totalChildren()));
        descendantsLabel.setText("%d".formatted(animal.getDescendants()));
        ageLabel.setText("%d".formatted(animal.getAge()));

        this.energyListener = (source, oldValue, newValue) -> {
            Platform.runLater(() -> {
                energyLabel.setText("%d".formatted(newValue.intValue()));
                currentGeneLabel.setText("%d".formatted(animal.getGenome().getCurrentGene()));
                if (newValue.intValue() <= 0) {
                    unmarkFollowedAnimal();
                }
            });
        };
        this.plantsEatenListener = (source, oldValue, newValue) -> {
            Platform.runLater(() -> plantsEatenLabel.setText("%d".formatted(newValue.intValue())));
        };
        this.childrenListener = (source, oldValue, newValue) -> {
            Platform.runLater(() -> childrenLabel.setText("%d".formatted(newValue.intValue())));
        };
        Platform.runLater(animal::initializeDescendancy);
        this.descendantsListener = (source, oldValue, newValue) -> {
            Platform.runLater(() -> descendantsLabel.setText("%d".formatted(newValue.intValue())));
        };
        this.ageListener = (source, oldValue, newValue) -> {
            Platform.runLater(() -> ageLabel.setText("%d".formatted(newValue.intValue())));
        };

        animal.energyProperty().addListener(energyListener);
        animal.plantsEatenProperty().addListener(plantsEatenListener);
        animal.childrenProperty().addListener(childrenListener);
        animal.descendantsProperty().addListener(descendantsListener);
        animal.ageProperty().addListener(ageListener);
    }

    private void displayPartyContents(ArrayList<Animal> animals) {
        partyContentsBox.setVisible(true);
        ObservableList<Animal> options = FXCollections.observableArrayList(animals);
        partyContentsBox.setItems(options);
    }
    @FXML
    private void onPartyContentSelected(ActionEvent actionEvent) {
        Animal animal = partyContentsBox.getValue();
        togglAnimalInfo(animal);
    }

    @FXML
    private void onShowMostCommonGenomeClicked() {
        ArrayList<Animal> animals = simulation.getAnimalsWithGenome(simulation.mostCommonGenomeProperty().get());
        for (Animal animal: animals) {
            Vector2d pos = animal.getPosition();
            Circle circle = new Circle(CELL_SIZE*0.25);
            circle.setOpacity(0.3);
            circle.setFill(Color.BLUE);
            insertObject(pos.x(), simulation.getMap().getHeight() - pos.y() - 1, circle);
        }
    }

    @Override
    public void mapChanged(WorldMap map) {
        Platform.runLater(() -> {
            drawMap(map);
        });
    }
}