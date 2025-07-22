package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import pokedex.models.Pokemon;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;

import java.util.List;

public class AddPokemonController {

    @FXML private TextField nameField;
    @FXML private TextField pokedexNumberField;
    @FXML private TextField levelField;
    @FXML private TextField hpField;
    @FXML private TextField attackField;
    @FXML private TextField defenseField;
    @FXML private TextField speedField;
    @FXML private TextField evolvesFromField;
    @FXML private TextField evolvesToField;
    @FXML private TextField evolutionLevelField;

    @FXML private Label feedbackLabel;
    @FXML private ComboBox<String> type1ComboBox;
    @FXML private ComboBox<String> type2ComboBox;

    private final List<String> pokemonTypes = List.of(
            "Normal", "Fire", "Water", "Grass", "Electric", "Ice", "Fighting", "Poison",
            "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark",
            "Steel", "Fairy"
    );

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    public AddPokemonController(PokedexManager pokedexManager, MoveManager moveManager,
                                ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    public void initialize() {
        type1ComboBox.getItems().addAll(pokemonTypes);
        type2ComboBox.getItems().add("None");
        type2ComboBox.getItems().addAll(pokemonTypes);
        type2ComboBox.setValue("None");
    }

    @FXML
    private void handleAddPokemon(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            int number = Integer.parseInt(pokedexNumberField.getText().trim());
            int level = Integer.parseInt(levelField.getText().trim());
            int hp = Integer.parseInt(hpField.getText().trim());
            int attack = Integer.parseInt(attackField.getText().trim());
            int defense = Integer.parseInt(defenseField.getText().trim());
            int speed = Integer.parseInt(speedField.getText().trim());

            String evolvesFromText = evolvesFromField.getText().trim();
            String evolvesToText = evolvesToField.getText().trim();
            String evolutionLevelText = evolutionLevelField.getText().trim();

            Integer evolvesFrom = evolvesFromText.isEmpty() || evolvesFromText.equals("-1") ? null : Integer.parseInt(evolvesFromText);
            Integer evolvesTo = evolvesToText.isEmpty() || evolvesToText.equals("-1") ? null : Integer.parseInt(evolvesToText);
            Integer evolutionLevel = evolutionLevelText.isEmpty() ? null : Integer.parseInt(evolutionLevelText);

            String type1 = type1ComboBox.getValue();
            String type2 = type2ComboBox.getValue();
            if (type2.equals("None")) type2 = null;

            // ✅ Duplicate number check
            if (pokedexManager.hasPokemonWithNumber(number)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Number", "A Pokémon with number #" + number + " already exists.");
                return;
            }

            // ✅ Duplicate name check
            if (pokedexManager.hasPokemonWithName(name)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Name", "The name \"" + name + "\" already exists in the Pokédex.");
                return;
            }

            // Confirm add
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Pokémon Add");
            confirmAlert.setHeaderText("Are you sure you want to add this Pokémon?");
            confirmAlert.setContentText("Name: " + name + "\nType: " + type1 + (type2 != null ? "/" + type2 : ""));
            confirmAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirmAlert.showAndWait();

            if (confirmAlert.getResult() == ButtonType.NO) {
                goBackToMenu(event);
                return;
            }

            Pokemon newPokemon = new Pokemon(
                    number, name, type1, type2, level,
                    evolvesFrom, evolvesTo, evolutionLevel,
                    hp, attack, defense, speed,
                    List.of(), null
            );

            pokedexManager.addPokemon(newPokemon);
            clearFields();

            // Cry out
            Alert cryAlert = new Alert(Alert.AlertType.CONFIRMATION);
            cryAlert.setTitle("Cry Out?");
            cryAlert.setHeaderText("Let " + name + " cry out?");
            cryAlert.setContentText("Would you like to hear it shout its name?");
            cryAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            cryAlert.showAndWait();

            if (cryAlert.getResult() == ButtonType.YES) {
                showAlert(Alert.AlertType.INFORMATION, "Pokémon Cries", name.toUpperCase() + "!");
            }

            goBackToMenu(event);

        } catch (Exception e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Invalid inputs");
        }
    }

    private void goBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PokemonMenu.fxml"));
        loader.setControllerFactory(param -> new PokemonMenuController(
                pokedexManager, moveManager, itemManager, trainerManager
        ));
        Parent root = loader.load();
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void clearFields() {
        nameField.clear();
        pokedexNumberField.clear();
        levelField.clear();
        hpField.clear();
        attackField.clear();
        defenseField.clear();
        speedField.clear();
        evolvesFromField.clear();
        evolvesToField.clear();
        evolutionLevelField.clear();
        type1ComboBox.getSelectionModel().clearSelection();
        type2ComboBox.setValue("None");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}