package agh.opp.presenter;

import agh.opp.model.elements.Animal;
import agh.opp.model.elements.Plant;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
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
    private Region leftRegion;
    @FXML
    private BorderPane root;
    @FXML
    private Button showMostCommonGenome;
    @FXML
    private Button showSpecialArea;
    private Stage stage;
    private Simulation simulation;
    private ExecutorService pool;
    private Animal followedAnimal = null;
    private ChangeListener<Number> energyListener;
    private ChangeListener<Number> plantsEatenListener;
    private ChangeListener<Number> childrenListener;
    private ChangeListener<Number> descendantsListener;
    private ChangeListener<Number> ageListener;
    private Circle markCircle;
    private Rectangle markRectangle;
    private ArrayList<Circle> genomeMarks = new ArrayList<>();
    private boolean genomeMarksShown = false;
    private ArrayList<Rectangle> areaMarks = new ArrayList<>();
    private boolean areaMarksShown = false;
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
            if (newValue != null) {
                mostCommonGenomeLabel.setText(newValue.toString());
            }
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
        showMostCommonGenome.disableProperty().bind(simulationRunningProperty);
        showSpecialArea.disableProperty().bind(simulationRunningProperty);
    }

    @FXML
    public void onStatisticsClicked(ActionEvent actionEvent) {
        worldStatistics.setVisible(!worldStatistics.isVisible());

        setStage();
        if (stage.getWidth() < 1100) {
            stage.sizeToScene();
        }
    }

    @FXML
    public void onStartClicked(ActionEvent actionEvent) {
        simulationRunningProperty.set(true);
        simulation.start();
        pool.execute(simulation);

        genomeMarksShown = false;
        areaMarksShown = false;
    }

    @FXML
    public void onStopClicked(ActionEvent actionEvent) {
        simulationRunningProperty.set(false);
        simulation.stop();
    }

    private void setStage() {
        if (this.stage == null) {
            this.stage = (Stage) root.getScene().getWindow();
        }
    }


    private void drawMap(WorldMap map) {
        clearGrid();
        int width = map.getWidth();
        int height = map.getHeight();

        for (int x = 0; x < width; x++) {mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));}
        for (int y = 0; y < height; y++) {mapGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE));}

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                Vector2d position = new Vector2d(x, y);
                Optional<WorldElement> optionalWorldElement = map.elementAt(position);

                if (optionalWorldElement.isPresent()) {
                    if (map.animalsAt(position) > 1){
//                        insertObject(x, height - y - 1, new Label("party"));
                        insertObject(x, height - y - 1, drawParty(map.animalsAt(position)));
                        addClickableRectangle(position);
                    } else {
                        WorldElement element = optionalWorldElement.get();
//                        insertObject(x, height - y - 1, new Label(String.valueOf(element)));
                        insertObject(x, height - y - 1, drawElement(element));
                        if (element instanceof Animal) {
                            addClickableRectangle(position);
                        }
                    }
                }
            }

        }
        if (followedAnimal != null && followedAnimal.getEnergy() > 0) {markPosition(followedAnimal.getPosition());}
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

    private void addClickableRectangle(Vector2d position) {
        Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE, Color.TRANSPARENT);
        insertObject(position.x(), simulation.getMap().getHeight() - position.y() - 1, rectangle);
        rectangle.setOnMouseClicked(event -> {
                unmarkPosition();
                togglAnimalInfo(simulation.getMap().getAnimalsAt(position));
                markPosition(position);
        });
    }

    private Node drawElement(WorldElement element) {
        if (element instanceof Plant) {
            if (((Plant) element).getEnergy() >= 0) {
                return new Circle(CELL_SIZE*0.1, Color.DARKGREEN);
            }
            else {
                return new Circle(CELL_SIZE*0.1, Color.DARKORANGE);
            }
        }
        else if (element instanceof Animal) {
            int val = Math.min(((Animal) element).getEnergy()*6 + 70, 255);
            return new Circle(CELL_SIZE*0.25, Color.rgb(val, 0, val));
        }
        return new Circle();
    }
    private Node drawParty(int amount) {
        int val = Math.min(amount*40 + 80, 255);
        return new Circle(CELL_SIZE*0.32, Color.rgb(val, 50, val / 2));
    }

    private void markPosition(Vector2d pos) {
        Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE, Color.TRANSPARENT);
        Circle circle = new Circle(CELL_SIZE*0.2);
        circle.setOpacity(1);
        circle.setFill(Color.CYAN);
        insertObject(pos.x(), simulation.getMap().getHeight() - pos.y() - 1, circle);
        insertObject(pos.x(), simulation.getMap().getHeight() - pos.y() - 1, rectangle);
        rectangle.setOnMouseClicked(event -> {
                animalStatistics.setVisible(false);
                partyContentsBox.setVisible(false);
                followedAnimalTextLabel.setVisible(false);

                EventHandler<ActionEvent> handler = partyContentsBox.getOnAction();
                partyContentsBox.setOnAction(null);
                partyContentsBox.setItems(null);
                partyContentsBox.setOnAction(handler);

                unmarkPosition();
                unfollowAnimal();
        });
        this.markCircle = circle;
        this.markRectangle = rectangle;
    }

    private void unmarkPosition() {
        mapGrid.getChildren().remove(markCircle);
        mapGrid.getChildren().remove(markRectangle);
        this.markCircle = null;
        this.markRectangle = null;
    }

    private void togglAnimalInfo (ArrayList<Animal> animals) {
        partyContentsBox.setVisible(true);
        followedAnimalTextLabel.setVisible(true);

        leftRegion.prefWidthProperty().bind(animalStatistics.widthProperty());
        leftRegion.setManaged(true);

        setStage();
        if (stage.getWidth() < 1100) {
            stage.sizeToScene();
        }

        ObservableList<Animal> options = FXCollections.observableArrayList(animals);

        EventHandler<ActionEvent> handler = partyContentsBox.getOnAction();
        partyContentsBox.setOnAction(null);
        partyContentsBox.setItems(options);
        partyContentsBox.setOnAction(handler);

        if (animals.size() == 1) {
            partyContentsBox.getSelectionModel().selectFirst();
        }
        else {
            animalStatistics.setVisible(false);
        }
    }

    private void unfollowAnimal() {
        if (this.followedAnimal != null) {
            followedAnimal.energyProperty().removeListener(energyListener);
            followedAnimal.plantsEatenProperty().removeListener(plantsEatenListener);
            followedAnimal.childrenProperty().removeListener(childrenListener);
            followedAnimal.removeDescendancy();
            followedAnimal.descendantsProperty().removeListener(descendantsListener);
            followedAnimal.ageProperty().removeListener(ageListener);

            this.followedAnimal = null;
        }
    }

    private void followAnimal(Animal animal) {
        this.followedAnimal = animal;

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
                    unmarkPosition();
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

    @FXML
    private void onAnimalSelected(ActionEvent actionEvent) {
        Animal animal = partyContentsBox.getValue();
        animalStatistics.setVisible(true);
        unfollowAnimal();
        followAnimal(animal);
    }

    @FXML
    private void onShowMostCommonGenomeClicked() {
        if (!genomeMarksShown) {
            if (simulation.mostCommonGenomeProperty().get() == null) {return;}
            genomeMarks = new ArrayList<>();
            genomeMarksShown = true;
            ArrayList<Animal> animals = simulation.getAnimalsWithGenome(simulation.mostCommonGenomeProperty().get());
//            Node lines = mapGrid.getChildren().get(0);

            for (Animal animal: animals) {
                Vector2d pos = animal.getPosition();
                Circle circle = new Circle(CELL_SIZE * 0.32);
                circle.setOpacity(0.8);
                circle.setFill(Color.YELLOW);
                circle.setMouseTransparent(true);
                insertObject(pos.x(), simulation.getMap().getHeight() - pos.y() - 1, circle);
                genomeMarks.add(circle);
                circle.toFront();
            }
//            lines.toBack();
        }
        else {
            genomeMarksShown = false;
            for (Circle circle: genomeMarks) {
                mapGrid.getChildren().remove(circle);
            }
            genomeMarks = new ArrayList<>();
        }
    }
    @FXML
    private void onShowSpecialAreaClicked() {
        if (!areaMarksShown) {
            areaMarksShown = true;
            Set<Vector2d> area = simulation.getPlantingController().getSpecialArea();
            Color color = simulation.getPlantingController().getSpecialAreaColor();
            Node lines = mapGrid.getChildren().get(0);

            for (Vector2d cell : area) {
                Rectangle rectangle = new Rectangle(CELL_SIZE, CELL_SIZE, color);
                rectangle.setOpacity(0.2);
                insertObject(cell.x(), simulation.getMap().getHeight() - cell.y() - 1, rectangle);
                areaMarks.add(rectangle);
                rectangle.toBack();
            }
            lines.toBack();
        }
        else {
            areaMarksShown = false;
            for (Rectangle rec: areaMarks) {
                mapGrid.getChildren().remove(rec);
            }
            areaMarks = new ArrayList<>();
        }
    }

    @Override
    public void mapChanged(WorldMap map) {
        Platform.runLater(() -> {
            drawMap(map);
        });
    }
}