package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Trainer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddTrainerController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField sexField;
    @FXML private TextField birthdateField;
    @FXML private TextField hometownField;
    @FXML private TextArea descriptionArea;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    public AddTrainerController(PokedexManager pokedexManager, MoveManager moveManager,
                                ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String sex = sexField.getText().trim().toUpperCase();
        String birthdateStr = birthdateField.getText().trim();
        String hometown = hometownField.getText().trim();
        String description = descriptionArea.getText().trim();

        // Basic validation
        if (id.isEmpty() || name.isEmpty() || sex.isEmpty() || birthdateStr.isEmpty() || hometown.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all required fields.");
            return;
        }

        if (!sex.equals("M") && !sex.equals("F")) {
            showAlert(Alert.AlertType.ERROR, "Sex must be 'M' or 'F'.");
            return;
        }

        LocalDate birthdate;
        try {
            birthdate = LocalDate.parse(birthdateStr);
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Birthdate must be in format YYYY-MM-DD.");
            return;
        }

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

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Add Trainer");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
