package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Trainer;

import java.io.IOException;
import java.time.LocalDate;

public class SearchTrainerController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private TextField hometownField;
    @FXML private DatePicker birthdatePicker;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    public SearchTrainerController(PokedexManager pokedexManager, MoveManager moveManager,
                                   ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String idInput = idField.getText().trim();
        String nameInput = nameField.getText().trim();
        String sexInput = sexComboBox.getValue();
        String hometownInput = hometownField.getText().trim();
        LocalDate birthdateInput = birthdatePicker.getValue();

        if (idInput.isEmpty() && nameInput.isEmpty() && (sexInput == null || sexInput.isEmpty()) &&
                hometownInput.isEmpty() && birthdateInput == null) {
            showAlert(AlertType.WARNING, "Missing Input", "Please fill in at least one field to search.");
            return;
        }

        StringBuilder resultBuilder = new StringBuilder();

        for (Trainer t : trainerManager.getAllTrainers()) {
            boolean match = idInput.isEmpty() || t.getTrainerID().equalsIgnoreCase(idInput);

            if (!nameInput.isEmpty() && !t.getName().equalsIgnoreCase(nameInput)) {
                match = false;
            }
            if (sexInput != null && !sexInput.isEmpty() && !t.getSex().equalsIgnoreCase(sexInput)) {
                match = false;
            }
            if (!hometownInput.isEmpty() && !t.getHometown().equalsIgnoreCase(hometownInput)) {
                match = false;
            }
            if (birthdateInput != null && !t.getBirthdate().equals(birthdateInput)) {
                match = false;
            }

            if (match) {
                resultBuilder.append(formatTrainer(t)).append("\n\n");
            }
        }

        if (!resultBuilder.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Trainer(s) Found", resultBuilder.toString());
        } else {
            showAlert(AlertType.WARNING, "No Match", "No trainer matched the given input.");
        }

        // Navigate back to Trainer Menu after showing results
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

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // So it expands for long messages
        alert.showAndWait();
    }

    private String formatTrainer(Trainer t) {
        return "ID: " + t.getTrainerID() +
                "\nName: " + t.getName() +
                "\nSex: " + t.getSex() +
                "\nBirthdate: " + t.getBirthdate() +
                "\nHometown: " + t.getHometown() +
                "\nDescription: " + t.getDescription();
    }
}