/**
 * Controller for the "Search Pokemon" screen in the Pokedex GUI.
 * Allows users to search for Pokemon by name, number, or type.
 * Displays matching Pokemon details in a scrollable dialog or shows alerts for no results or invalid input.
 * Also allows users to return to the Pokemon Menu.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
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

/**
 * Constructs the controller and injects required manager dependencies.
 */
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

    /**
     * Initializes the type choice box with valid Pokemon types.
     */
    @FXML
    public void initialize() {
        List<String> types = List.of(
                "Fire", "Water", "Grass", "Electric", "Psychic", "Normal", "Fighting",
                "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel", "Ice",
                "Dragon", "Dark", "Shiny"
        );
        typeChoiceBox.getItems().addAll(types);
    }

    /**
     * Handles the search action when the user clicks the search button.
     * Searches by name, type, or Pokedex number and displays results or alerts.
     *
     * @param event The triggered ActionEvent
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String name = nameField.getText().trim();
        String numberStr = numberField.getText().trim();
        String type = typeChoiceBox.getValue();

        List<Pokemon> results = new ArrayList<>();

        //search by name
        if (!name.isEmpty()) {
            results = pokedexManager.searchByName(name);
        //search by type
        } else if (type != null && !type.isEmpty()) {
            results = pokedexManager.searchByType(type);
        //search by number
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

        //show results or not found
        if (results.isEmpty()) {
            showAlert("No Pokemon found.", Alert.AlertType.INFORMATION, event);
        } else {
            StringBuilder resultMessage = new StringBuilder();
            for (Pokemon p : results) {
                resultMessage.append("#").append(p.getPokedexNumber()).append(" – ").append(p.getName()).append("\n");
                resultMessage.append("Type: ").append(p.getType1());
                if (p.getType2() != null && !p.getType2().isEmpty()) {
                    resultMessage.append(" / ").append(p.getType2());
                }
                resultMessage.append("\nLevel: ").append(p.getBaseLevel());
                resultMessage.append("\nHP: ").append(p.getHp());
                resultMessage.append("\nAttack: ").append(p.getAttack());
                resultMessage.append("\nDefense: ").append(p.getDefense());
                resultMessage.append("\nSpeed: ").append(p.getSpeed());

                resultMessage.append("\nMoves:\n");
                if (p.getMoveSet() == null || p.getMoveSet().isEmpty()) {
                    resultMessage.append("None\n");
                } else {
                    for (String move : p.getMoveSet()) {
                        resultMessage.append("- ").append(move).append("\n");
                    }
                }
                resultMessage.append("\n\n");
            }

            showScrollablePokemonDialog(resultMessage.toString(), event);
        }
    }

    /**
     * Shows a basic alert dialog with optional return to the Pokemon menu.
     *
     * @param message The message to display
     * @param type The type of alert (e.g., WARNING, INFORMATION)
     * @param event The triggering event for optional navigation
     */
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

    /**
     * Displays a scrollable dialog with Pokemon search results.
     *
     * @param content Text content to show
     * @param event The triggering event for optional return
     */
    private void showScrollablePokemonDialog(String content, ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Search Results");
        dialog.setHeaderText("Pokemon Found:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, new ButtonType("Back to Menu", ButtonBar.ButtonData.CANCEL_CLOSE));

        TextArea textArea = new TextArea(content);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefSize(500, 400);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        dialogPane.setContent(scrollPane);

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(button -> {
            if (button.getText().equals("Back to Menu")) {
                returnToMenu(event);
            }
        });
    }


    /**
     * Navigates back to the Pokemon Menu screen.
     *
     * @param event The action event used to switch scenes
     */
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