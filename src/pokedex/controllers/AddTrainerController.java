/**
 * This handles form validation, trainer object creation, and redirection back to the menu
 * upon successful submission. Data is managed via the TrainerManager.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.controllers;

// JavaFX components for UI handling
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

// Managers for various parts of the application
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;

// Data model
import pokedex.models.Trainer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Controller for the Add Trainer form.
 * Handles input validation, trainer creation, and saving to the TrainerManager.
 * On successful submission, the app navigates back to the Trainer Menu.
 */
public class AddTrainerController {

    // FXML input fields
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField sexField;
    @FXML private DatePicker birthdateField;
    @FXML private TextField hometownField;
    @FXML private TextArea descriptionArea;

    // Manager references
    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the AddTrainerController with the necessary manager dependencies.
     *
     * @param pokedexManager  the Pokédex manager for Pokémon reference
     * @param moveManager     the Move manager for managing TMs/HMs
     * @param itemManager     the Item manager for inventory logic
     * @param trainerManager  the Trainer manager for trainer storage and retrieval
     */
    public AddTrainerController(PokedexManager pokedexManager, MoveManager moveManager,
                                ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Handles the Submit button click.
     * Validates input, constructs a new Trainer object, and adds it to the TrainerManager.
     * Redirects the user back to the Trainer Menu on success.
     *
     * @param event the triggered ActionEvent from the Submit button
     */
    @FXML
    private void handleSubmit(ActionEvent event) {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String sex = sexField.getText().trim().toUpperCase();
        LocalDate birthdateStr = birthdateField.getValue();
        String hometown = hometownField.getText().trim();
        String description = descriptionArea.getText().trim();

        // Basic validation
        if (id.isEmpty() || name.isEmpty() || sex.isEmpty() || birthdateStr == null || hometown.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all required fields.");
            return;
        }

        if (!sex.equals("M") && !sex.equals("F")) {
            showAlert(Alert.AlertType.ERROR, "Sex must be 'M' or 'F'.");
            return;
        }

        LocalDate birthdate = birthdateStr;

        if (trainerManager.hasTrainerWithID(id)) {
            showAlert(Alert.AlertType.ERROR, "A trainer with this ID already exists.");
            return;
        }

        Trainer newTrainer = new Trainer(id, name, birthdate, sex, hometown, description);

        trainerManager.addTrainer(newTrainer);

        showAlert(Alert.AlertType.INFORMATION, "Trainer added successfully!");

        // Go back to Trainer Menu
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TrainerMenu.fxml"));
            loader.setControllerFactory(param -> new TrainerMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to show an alert dialog.
     *
     * @param type    the alert type (INFORMATION, ERROR, etc.)
     * @param message the message to display
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Add Trainer");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}