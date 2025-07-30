/**
 * Controller for the "Search Move" screen in the Pokédex GUI.
 * Allows users to search for moves by keyword (name, description, type, or classification).
 * Displays results in an alert dialog and provides an option to return to the Move Menu.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */

package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.ItemManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Move;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SearchMoveController {

    @FXML private TextField keywordField;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller with manager dependencies injected.
     *
     * @param pokedexManager Pokédex manager
     * @param moveManager Move manager
     * @param itemManager Item manager
     * @param trainerManager Trainer manager
     */
    public SearchMoveController(PokedexManager pokedexManager, MoveManager moveManager,
                                ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Handles the search action when the user clicks the search button.
     * Searches all move fields and displays matches in an alert.
     * If no keyword is entered, prompts the user to enter one.
     *
     * @param event The search button click event
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = keywordField.getText().trim();
        List<Move> results = new ArrayList<>();

        if (!keyword.isEmpty()) {
            for (Move move : MoveManager.getInstance().getAllMoves()) {
                if ((move.getName() != null && move.getName().toLowerCase().contains(keyword.toLowerCase())) ||
                        (move.getDescription() != null && move.getDescription().toLowerCase().contains(keyword.toLowerCase())) ||
                        (move.getType1() != null && move.getType1().toLowerCase().contains(keyword.toLowerCase())) ||
                        (move.getType2() != null && move.getType2().toLowerCase().contains(keyword.toLowerCase())) ||
                        (move.getClassification() != null && move.getClassification().toLowerCase().contains(keyword.toLowerCase()))) {
                    results.add(move);
                }
            }

            if (results.isEmpty()) {
                showAlert("No move found.", event);
            } else {
                StringBuilder message = new StringBuilder();
                for (Move m : results) {
                    message.append("Move: ").append(m.getName()).append("\n")
                            .append("Description: ").append(m.getDescription()).append("\n")
                            .append("Type: ").append(m.getType1());
                    if (m.getType2() != null && !m.getType2().isBlank()) {
                        message.append("/").append(m.getType2());
                    }
                    message.append("\n\n");
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Search Results");
                alert.setHeaderText("Moves Found:");
                alert.setContentText(message.toString());

                ButtonType okButton = new ButtonType("OK");
                ButtonType backButton = new ButtonType("Back to Menu");
                alert.getButtonTypes().setAll(okButton, backButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == backButton) {
                    goToMoveMenu(event);
                }
            }
        } else {
            showAlert("Please enter a keyword to search.", event);
        }
    }

    /**
     * Displays an information alert with a message and optional navigation back to the Move Menu.
     *
     * @param message Message to show in the alert
     * @param event Event used for navigation if the user selects "Back to Menu"
     */
    private void showAlert(String message, ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType("OK");
        ButtonType backButton = new ButtonType("Back to Menu");
        alert.getButtonTypes().setAll(okButton, backButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == backButton) {
            goToMoveMenu(event);
        }
    }

    /**
     * Navigates back to the Move Menu screen.
     *
     * @param event The button click event that triggered the navigation
     */
    private void goToMoveMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MoveMenu.fxml"));
            loader.setControllerFactory(param -> new MoveMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}