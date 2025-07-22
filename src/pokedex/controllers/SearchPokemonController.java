package pokedex.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pokedex.managers.*;
import pokedex.models.Pokemon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SearchPokemonController {

    @FXML private TextField nameField;
    @FXML private TextField numberField;
    @FXML private ChoiceBox<String> typeChoiceBox;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    // ✅ Constructor for injecting managers
    public SearchPokemonController(PokedexManager pokedexManager, MoveManager moveManager,
                                   ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    public void initialize() {
        List<String> types = List.of(
                "Fire", "Water", "Grass", "Electric", "Psychic", "Normal", "Fighting",
                "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel", "Ice",
                "Dragon", "Dark", "Fairy"
        );
        typeChoiceBox.getItems().addAll(types);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String name = nameField.getText().trim();
        String numberStr = numberField.getText().trim();
        String type = typeChoiceBox.getValue();

        List<Pokemon> results = new ArrayList<>();

        if (!name.isEmpty()) {
            results = pokedexManager.searchByName(name);
        } else if (type != null && !type.isEmpty()) {
            results = pokedexManager.searchByType(type);
        } else if (!numberStr.isEmpty()) {
            try {
                int number = Integer.parseInt(numberStr);
                Pokemon found = pokedexManager.getByPokedexNumber(number);
                if (found != null) {
                    results.add(found);
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Pokedex number.", Alert.AlertType.WARNING, event);
                return;
            }
        } else {
            showAlert("Please fill at least one search field.", Alert.AlertType.WARNING, event);
            return;
        }

        if (results.isEmpty()) {
            showAlert("No Pokémon found.", Alert.AlertType.INFORMATION, event);
        } else {
            StringBuilder resultMessage = new StringBuilder();
            for (Pokemon p : results) {
                resultMessage.append(p).append("\n\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search Results");
            alert.setHeaderText("Pokémon Found:");
            alert.setContentText(resultMessage.toString());

            ButtonType okButton = new ButtonType("OK");
            ButtonType backButton = new ButtonType("Back to Menu");

            alert.getButtonTypes().setAll(okButton, backButton);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == backButton) {
                returnToMenu(event);
            }
        }
    }

    private void showAlert(String message, Alert.AlertType type, ActionEvent event) {
        Alert alert = new Alert(type);
        alert.setTitle("Search");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType("OK");
        ButtonType backButton = new ButtonType("Back to Menu");
        alert.getButtonTypes().setAll(okButton, backButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == backButton) {
            returnToMenu(event);
        }
    }

    private void returnToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PokemonMenu.fxml"));
            loader.setControllerFactory(param -> new PokemonMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}