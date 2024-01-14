package agh.opp.presenter;


import agh.opp.model.maps.RegularMap;
import agh.opp.model.simulation.variations.RegularCleaner;
import agh.opp.model.simulation.variations.RegularMutation;
import agh.opp.model.simulation.variations.Simulation;
import agh.opp.model.simulation.variations.movement.PoisonousWorldMovement;
import agh.opp.model.simulation.variations.movement.RegularMovement;
import agh.opp.model.simulation.variations.planting.PoisonousPlanting;
import agh.opp.model.simulation.variations.planting.RegularPlanting;
import agh.opp.model.simulation.variations.reproduction.MadAnimalsReproduction;
import agh.opp.model.simulation.variations.reproduction.RegularAnimalsReproduction;
import agh.opp.model.tools.interfaces.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitializeSimulation {
    private static int totalSimulations = 0;
    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    private final ObservableList<String> mapsVariants = FXCollections.observableArrayList("kula ziemska");
    private final ObservableList<String> plantingVariants = FXCollections.observableArrayList("zalesione równiki", "zatrute owoce");
    private final ObservableList<String> mutationVariants = FXCollections.observableArrayList("pełna losowość");
    private final ObservableList<String> behaviourVariants = FXCollections.observableArrayList("pełna predestynacja", "nieco szaleństwa");

    @FXML
    private ComboBox<String> mapType;
    @FXML
    private Spinner<Integer> widthSpinner;
    @FXML
    private Spinner<Integer> heightSpinner;
    @FXML
    private Spinner<Integer> initialPlantSpinner;
    @FXML
    private Spinner<Integer> plantEnergySpinner;
    @FXML
    private Spinner<Integer> dailyGrowthSpinner;
    @FXML
    private ComboBox<String> plantingType;
    @FXML
    private Spinner<Integer> initialAnimalSpinner;
    @FXML
    private Spinner<Integer> startAnimalEnergySpinner;
    @FXML
    private Spinner<Integer> fullAnimalSpinner;
    @FXML
    private Spinner<Integer> reproductionEnergySpinner;
    @FXML
    private Spinner<Integer> minMutationSpinner;
    @FXML
    private Spinner<Integer> maxMutationSpinner;
    @FXML
    private ComboBox<String> mutationType;
    @FXML
    private Spinner<Integer> genomeLengthSpinner;
    @FXML
    private ComboBox<String> behaviourType;


    @FXML
    public void initialize() {
        mapType.setItems(mapsVariants);
        plantingType.setItems(plantingVariants);
        mutationType.setItems(mutationVariants);
        behaviourType.setItems(behaviourVariants);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Błąd wejściowy");
        alert.setHeaderText(message);
        alert.show();
    }

    public void initializeSimulation(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane viewRoot = loader.load();
        SimulationPresenter presenter = loader.getController();
        configureSimulation(presenter);
        presenter.setSimulationPool(pool);

        Stage newStage = new Stage();
        configureSimulationStage(newStage, viewRoot);
        newStage.show();
    }

    private void configureSimulation(SimulationPresenter presenter){
        int width = widthSpinner.getValue();
        int height = heightSpinner.getValue();
        int genomeLength = genomeLengthSpinner.getValue();
        int dailyPlant = dailyGrowthSpinner.getValue();

        WorldMap map = setMap(width, height);
        PlantingController plantingController = setPlantingController(map, height, width);
        MapMovementController mapMovementController = setMapMovementController(map);
        Mutation mutation = setMutation(genomeLength);
        MapCleaner mapCleaner = new RegularCleaner(map);
        ReproductionController reproductionController = setReproductionController(map,genomeLength, mutation);

        Simulation simulation = new Simulation(mapCleaner, mapMovementController, reproductionController, plantingController, map, dailyPlant);
        presenter.setSimulation(simulation);
        presenter.setSimulationPool(pool);
        simulation.subscribe(presenter);
        simulation.mapChanged();
    }

    private WorldMap setMap(int width, int height){
        String selectedType = mapType.getValue();
        if (selectedType == null){
            showAlert("Wprowadź wariant mapy");
            throw new NullPointerException("Map variant is null");
        }

        if (selectedType.equals("kula ziemska")) return new RegularMap(width, height);
        showAlert("Nieobsługiwany typ mapy.");
        throw new IllegalArgumentException("Unknown map type.");
    }

    private PlantingController setPlantingController(WorldMap map, int height, int width){
        String selectedType = plantingType.getValue();
        if (selectedType == null){
            showAlert("Wprowadź wariant wzrostu roślin");
            throw new NullPointerException("Planting variant is null");
        }
        int startPlant = initialPlantSpinner.getValue();
        int plantEnergy = plantEnergySpinner.getValue();

        return switch (selectedType) {
            case "zalesione równiki" -> new RegularPlanting(map, height, width, startPlant, plantEnergy);
            case "zatrute owoce" -> new PoisonousPlanting(map, height, width, startPlant, plantEnergy);
            default -> {
                showAlert("Nieobsługiwany wariant wzrostu roślin");
                throw new IllegalArgumentException("Unknown planting variant");
            }
        };
    }

    private MapMovementController setMapMovementController(WorldMap map){
        String selectedType = plantingType.getValue();
        if (selectedType == null){
            showAlert("Wprowadź wariant wzrostu roślin");
            throw new NullPointerException("Planting variant is null");
        }

        return switch (selectedType) {
            case "zalesione równiki" -> new RegularMovement(map);
            case "zatrute owoce" -> new PoisonousWorldMovement(map);
            default -> {
                showAlert("Nieobsługiwany wariant poruszania się");
                throw new IllegalArgumentException("Unknown planting variant.");
            }
        };
    }

    private Mutation setMutation(int genomeLength){
        String selectedType = mutationType.getValue();
        if (selectedType == null){
            showAlert("Wprowadź wariant mutacji");
            throw new NullPointerException("Mutation variant is null");
        }

        int minMutation = minMutationSpinner.getValue();
        int maxMutation = maxMutationSpinner.getValue();

        if (selectedType.equals("pełna losowość")) return new RegularMutation(minMutation, maxMutation, genomeLength);
        showAlert("Nieobsługiwany wariant mutacji.");
        throw new IllegalArgumentException("Unknown mutation variant.");
    }

    private ReproductionController setReproductionController(WorldMap map, int genomeLength, Mutation mutation){
        String selectedType = behaviourType.getValue();
        if (selectedType == null){
            showAlert("Wprowadź wariant zachowania");
            throw new NullPointerException("Behaviour variant is null");
        }

        int minEnergy = fullAnimalSpinner.getValue();
        int reproductionEnergy = reproductionEnergySpinner.getValue();
        int initialAnimals = initialAnimalSpinner.getValue();
        int initialEnergy = startAnimalEnergySpinner.getValue();

        return switch (selectedType) {
            case "pełna predestynacja" -> new RegularAnimalsReproduction(map, genomeLength, mutation, minEnergy, reproductionEnergy, initialAnimals, initialEnergy);
            case "nieco szaleństwa" -> new MadAnimalsReproduction(map, genomeLength, mutation, minEnergy, reproductionEnergy, initialAnimals, initialEnergy);
            default -> {
                showAlert("Nieobsługiwany wariant zachowania");
                throw new IllegalArgumentException("Unknown behaviour variant.");
            }
        };
    }


    private synchronized void configureSimulationStage(Stage stage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        stage.setScene(scene);
        totalSimulations++;
        stage.setTitle("World No. " + totalSimulations);
        stage.minWidthProperty().bind(viewRoot.minWidthProperty());
        stage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}