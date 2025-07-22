package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pokedex.models.Pokemon;
import pokedex.managers.PokedexManager;

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

            Integer evolvesFrom = evolvesFromText.isEmpty() ? null : Integer.parseInt(evolvesFromText);
            Integer evolvesTo = evolvesToText.isEmpty() ? null : Integer.parseInt(evolvesToText);
            Integer evolutionLevel = evolutionLevelText.isEmpty() ? null : Integer.parseInt(evolutionLevelText);

            String type1 = type1ComboBox.getValue();
            String type2 = type2ComboBox.getValue();
            if (type2.equals("None")) type2 = null;

            // ✅ Duplicate number check
            if (PokedexManager.getInstance().hasPokemonWithNumber(number)) {
                Alert numberAlert = new Alert(Alert.AlertType.ERROR);
                numberAlert.setTitle("Duplicate Number");
                numberAlert.setHeaderText(null);
                numberAlert.setContentText("A Pokémon with number #" + number + " already exists.");
                numberAlert.showAndWait();
                return;
            }

// ✅ Duplicate name check
            if (PokedexManager.getInstance().hasPokemonWithName(name)) {
                Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                nameAlert.setTitle("Duplicate Name");
                nameAlert.setHeaderText(null);
                nameAlert.setContentText("The name \"" + name + "\" already exists in the Pokédex.");
                nameAlert.showAndWait();
                return;
            }

            // Confirm add
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Pokémon Add");
            confirmAlert.setHeaderText("Are you sure you want to add this Pokémon?");
            confirmAlert.setContentText("Name: " + name + "\nType: " + type1 + (type2 != null ? "/" + type2 : ""));
            confirmAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirmAlert.showAndWait();

            if (confirmAlert.getResult() != ButtonType.YES) return;

            // Add the Pokémon
            Pokemon newPokemon = new Pokemon(
                    number, name, type1, type2, level,
                    evolvesFrom, evolvesTo, evolutionLevel,
                    hp, attack, defense, speed,
                    List.of(), null
            );

            PokedexManager.getInstance().addPokemon(newPokemon);
            clearFields();

            // Ask if Pokémon should cry out
            Alert cryAlert = new Alert(Alert.AlertType.CONFIRMATION);
            cryAlert.setTitle("Cry Out?");
            cryAlert.setHeaderText("Let " + name + " cry out?");
            cryAlert.setContentText("Would you like to hear it shout its name?");
            cryAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            cryAlert.showAndWait();

            if (cryAlert.getResult() == ButtonType.YES) {
                Alert soundAlert = new Alert(Alert.AlertType.INFORMATION);
                soundAlert.setTitle("Pokémon Cries");
                soundAlert.setHeaderText(null);
                soundAlert.setContentText(name.toUpperCase() + "!");
                soundAlert.showAndWait();
            }

            // Go back to Pokémon main menu (PokemonMenu.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PokemonMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Invalid inputs");
        }
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
}