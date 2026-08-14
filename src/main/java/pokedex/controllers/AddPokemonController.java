
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
/**
 * Controller for the "Add Pokemon" screen in the Pokedex GUI.
 * Allows users to input Pokemon details, validates all fields, and adds a new Pokemon to the Pokedex.
 * Handles user confirmation, duplicate checking, evolution input, and optional cry message.
 * Returns to the Pokemon menu after a successful addition.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
public class AddPokemonController {
    // FXML Fields for user input
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
    @FXML private ComboBox<String> type1ComboBox;
    @FXML private ComboBox<String> type2ComboBox;

    // List of valid Pokemon types
    private final List<String> pokemonTypes = List.of(
            "Normal", "Fire", "Water", "Grass", "Electric", "Ice", "Fighting", "Poison",
            "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark",
            "Steel", "Fairy"
    );

    // Manager dependencies
    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller with references to all required managers.
     */
    public AddPokemonController(PokedexManager pokedexManager, MoveManager moveManager,
                                ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Initializes the ComboBoxes with Pokemon types and default selections.
     */
    @FXML
    public void initialize() {
        type1ComboBox.getItems().addAll(pokemonTypes);
        type2ComboBox.getItems().add("None");
        type2ComboBox.getItems().addAll(pokemonTypes);
        type2ComboBox.setValue("None");
    }

    /**
     * Handles the "Add Pokemon" button click event.
     * Validates input, confirms action, checks for duplicates, creates Pokemon, and adds to the Pokedex.
     *
     * @param event The button click event
     */
    @FXML
    private void handleAddPokemon(ActionEvent event) {
        try {
            //read and validate all text inputs
            String name = nameField.getText().trim();
            String pokedexText = pokedexNumberField.getText().trim();
            String levelText = levelField.getText().trim();
            String hpText = hpField.getText().trim();
            String attackText = attackField.getText().trim();
            String defenseText = defenseField.getText().trim();
            String speedText = speedField.getText().trim();

            if (name.isEmpty() || pokedexText.isEmpty() || levelText.isEmpty() || hpText.isEmpty()
                    || attackText.isEmpty() || defenseText.isEmpty() || speedText.isEmpty()
                    || type1ComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all required fields.");
                return;
            }

            //parse numeric input
            int number = Integer.parseInt(pokedexText);
            int level = Integer.parseInt(levelText);
            int hp = Integer.parseInt(hpText);
            int attack = Integer.parseInt(attackText);
            int defense = Integer.parseInt(defenseText);
            int speed = Integer.parseInt(speedText);

            //parse evolution related fields
            String evolvesFromText = evolvesFromField.getText().trim();
            String evolvesToText = evolvesToField.getText().trim();
            String evolutionLevelText = evolutionLevelField.getText().trim();

            Integer evolvesFrom = evolvesFromText.isEmpty() || evolvesFromText.equals("-1") ? null : Integer.parseInt(evolvesFromText);
            Integer evolvesTo = evolvesToText.isEmpty() || evolvesToText.equals("-1") ? null : Integer.parseInt(evolvesToText);
            Integer evolutionLevel = evolutionLevelText.isEmpty() ? null : Integer.parseInt(evolutionLevelText);

            //parse type input
            String type1 = type1ComboBox.getValue();
            String type2 = type2ComboBox.getValue();
            if (type2.equals("None")) type2 = null;

            //check for duplicates
            if (pokedexManager.hasPokemonWithNumber(number)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Pokedex Number", "A Pokemon with this Pokedex number already exists.");
                return;
            }

            if (pokedexManager.hasPokemonWithName(name)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Name", "A Pokemon with this name already exists.");
                return;
            }

            // Confirm add
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Pokemon Add");
            confirmAlert.setHeaderText("Are you sure you want to add this Pokemon?");
            confirmAlert.setContentText("Name: " + name + "\nType: " + type1 + (type2 != null ? "/" + type2 : ""));
            confirmAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirmAlert.showAndWait();

            if (confirmAlert.getResult() == ButtonType.NO) {
                return;
            }

            //add pokemon
            Pokemon newPokemon = new Pokemon(
                    number, name, type1, type2, level,
                    evolvesFrom, evolvesTo, evolutionLevel,
                    hp, attack, defense, speed,
                    List.of(), null
            );

            pokedexManager.addPokemon(newPokemon);
            clearFields();

            //optional cry
            Alert cryAlert = new Alert(Alert.AlertType.CONFIRMATION);
            cryAlert.setTitle("Cry Out?");
            cryAlert.setHeaderText("Let " + name + " cry out?");
            cryAlert.setContentText("Would you like to hear it shout its name?");
            cryAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            cryAlert.showAndWait();

            if (cryAlert.getResult() == ButtonType.YES) {
                showAlert(Alert.AlertType.INFORMATION, "Pokemon Cries", name.toUpperCase() + "!");
            }

            goBackToMenu(event);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Numbers", "All numeric fields must contain valid numbers.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the user to the Pokemon Menu screen.
     *
     * @param event The action event triggering the navigation
     * @throws IOException If the FXML cannot be loaded
     */
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

    /**
     * Clears all fields in the Add Pokemon form.
     */
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

    /**
     * Shows an alert popup with the given information.
     *
     * @param type    Type of the alert
     * @param title   Title of the alert window
     * @param message Message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}