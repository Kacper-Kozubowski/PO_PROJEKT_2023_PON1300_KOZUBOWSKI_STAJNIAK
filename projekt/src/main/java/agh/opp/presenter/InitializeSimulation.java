package agh.opp.presenter;


import agh.opp.model.maps.RegularMap;
import agh.opp.model.simulation.variations.RegularCleaner;
import agh.opp.model.simulation.variations.RegularMutation;
import agh.opp.model.simulation.variations.Simulation;
import agh.opp.model.simulation.variations.StatsSaver;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

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
    private Button savePreset;
    @FXML
    private ComboBox<String> selectPresetBox;
    @FXML
    private CheckBox saveStatistics;
    private final Preferences pref = Preferences.userNodeForPackage(InitializeSimulation.class);


    @FXML
    public void initialize() {
        mapType.setItems(mapsVariants);
        plantingType.setItems(plantingVariants);
        mutationType.setItems(mutationVariants);
        behaviourType.setItems(behaviourVariants);
        setPresetBox(getPresets());
        selectPresetBox.getSelectionModel().select(this.pref.get("Preset", "Default"));
        loadConfig(this.pref);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Błąd wejściowy");
        alert.setHeaderText(message);
        alert.show();
    }

    public void initializeSimulation(ActionEvent actionEvent) throws IOException {
        saveConfig(pref);
        this.pref.put("Preset", selectPresetBox.getValue());

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
        saveStatisticsCheck(simulation);
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

    private void loadConfig(Preferences pref) {

        mapType.getSelectionModel().select(pref.get("mapType", "kula ziemska"));
        widthSpinner.getValueFactory().setValue(pref.getInt("width", 8));
        heightSpinner.getValueFactory().setValue(pref.getInt("height", 8));

        initialPlantSpinner.getValueFactory().setValue(pref.getInt("initialPlants", 5));
        plantEnergySpinner.getValueFactory().setValue(pref.getInt("plantEnergy", 3));
        dailyGrowthSpinner.getValueFactory().setValue(pref.getInt("dailyGrowth", 2));
        plantingType.getSelectionModel().select(pref.get("plantingType","zalesione równiki"));

        initialAnimalSpinner.getValueFactory().setValue(pref.getInt("initialAnimals", 4));
        startAnimalEnergySpinner.getValueFactory().setValue(pref.getInt("startAnimalEnergy", 5));
        fullAnimalSpinner.getValueFactory().setValue(pref.getInt("fullAnimal", 5));
        reproductionEnergySpinner.getValueFactory().setValue(pref.getInt("reproductionEnergy", 6));
        minMutationSpinner.getValueFactory().setValue(pref.getInt("minMutation", 1));
        maxMutationSpinner.getValueFactory().setValue(pref.getInt("maxMutation", 2));
        mutationType.getSelectionModel().select(pref.get("mutationType", "pełna losowość"));
        genomeLengthSpinner.getValueFactory().setValue(pref.getInt("genomeLength", 5));
        behaviourType.getSelectionModel().select(pref.get("behaviourType", "pełna predestynacja"));
    }

    private void saveConfig(Preferences pref) {

        pref.put("mapType", mapType.getValue());
        pref.putInt("width", widthSpinner.getValue());
        pref.putInt("height", heightSpinner.getValue());

        pref.putInt("initialPlants", initialPlantSpinner.getValue());
        pref.putInt("plantEnergy", plantEnergySpinner.getValue());
        pref.putInt("dailyGrowth", dailyGrowthSpinner.getValue());
        pref.put("plantingType", plantingType.getValue());

        pref.putInt("initialAnimals", initialAnimalSpinner.getValue());
        pref.putInt("startAnimalEnergy", startAnimalEnergySpinner.getValue());
        pref.putInt("fullAnimal", fullAnimalSpinner.getValue());
        pref.putInt("reproductionEnergy", reproductionEnergySpinner.getValue());
        pref.putInt("minMutation", minMutationSpinner.getValue());
        pref.putInt("maxMutation", maxMutationSpinner.getValue());
        pref.put("mutationType", mutationType.getValue());
        pref.putInt("genomeLength", genomeLengthSpinner.getValue());
        pref.put("behaviourType", behaviourType.getValue());
    }

    @FXML
    public void openSavePresetWindow() {
        Stage stage = new Stage();
        stage.setTitle("Save preset");

        Label label = new Label("Wprowadź nazwę szablonu");
        TextField textField = new TextField();
        Button button = new Button("Zapisz");

        textField.setMaxWidth(200);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                savePreset(textField.getText());
                stage.close();
            }
        });

        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setAlignment(textField, Pos.CENTER);
        BorderPane.setAlignment(button, Pos.CENTER);

        BorderPane bPane = new BorderPane();
        bPane.setPrefSize(350, 120);
        bPane.setStyle("-fx-alignment: center;" + "-fx-padding: 10");
        bPane.setTop(label);
        bPane.setCenter(textField);
        bPane.setBottom(button);

        stage.setScene(new Scene(bPane));
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(savePreset.getScene().getWindow());
        stage.show();
    }
    private void savePreset(String presetName) {
        Preferences pref = Preferences.userNodeForPackage(InitializeSimulation.class);


        Preferences newPref = pref.node(presetName);
        saveConfig(newPref);
        addToPresetsList(presetName);
    }

    private void addToPresetsList(String presetName) {
        Preferences pref = Preferences.userNodeForPackage(InitializeSimulation.class);

        int presetCount = pref.getInt("presetCount", 0);
        ArrayList<String> presetList = new ArrayList<>();
        presetList.add(presetName);
        presetList.addAll(getPresets());
        Collections.sort(presetList);

        for (int index = 0; index < presetCount + 1; index++) {
            pref.put("%d".formatted(index), presetList.get(index));
        }
        pref.putInt("presetCount", presetCount + 1);

        setPresetBox(presetList);
        selectPresetBox.getSelectionModel().select(presetName);
    }

    private ArrayList<String> getPresets() {
        Preferences pref = Preferences.userNodeForPackage(InitializeSimulation.class);

        ArrayList<String> presetList = new ArrayList<>();
        int presetCount = pref.getInt("presetCount", 0);

        for (int index = 0; index < presetCount; index++) {
            presetList.add(pref.get("%d".formatted(index), ""));
        }
        return presetList;
    }
    private void setPresetBox(ArrayList<String> presetList) {
        ArrayList<String> presets = new ArrayList<>();
        presets.add("Default");
        presets.addAll(presetList);

        selectPresetBox.setItems(FXCollections.observableArrayList(presets));
    }
    @FXML
    private void deletePresets() {
        Preferences pref = Preferences.userNodeForPackage(InitializeSimulation.class);
        pref.putInt("presetCount", 0);
        setPresetBox(getPresets());
    }
    @FXML
    private void onPresetSelected(ActionEvent actionEvent) {
        String presetName = selectPresetBox.getValue();
        Preferences pref = this.pref.node(presetName);
        loadConfig(pref);
    }

    private void saveStatisticsCheck(Simulation simulation) {
        if (saveStatistics.isSelected()) {
            StatsSaver statsSaver = new StatsSaver(simulation);
            simulation.subscribe(statsSaver);
        }
    }
}