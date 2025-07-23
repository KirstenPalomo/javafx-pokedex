package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
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

        Trainer found = null;

        if (!idInput.isEmpty()) {
            found = trainerManager.getTrainerWithID(idInput);
        } else if (!nameInput.isEmpty()) {
            found = trainerManager.getTrainerWithName(nameInput);
        } else if (sexInput != null && !sexInput.isEmpty()) {
            for (Trainer t : trainerManager.getAllTrainers()) {
                if (t.getSex().equalsIgnoreCase(sexInput)) {
                    found = t;
                    break;
                }
            }
        } else if (!hometownInput.isEmpty()) {
            for (Trainer t : trainerManager.getAllTrainers()) {
                if (t.getHometown().equalsIgnoreCase(hometownInput)) {
                    found = t;
                    break;
                }
            }
        } else if (birthdateInput != null) {
            for (Trainer t : trainerManager.getAllTrainers()) {
                if (t.getBirthdate().equals(birthdateInput)) {
                    found = t;
                    break;
                }
            }
        }

        if (found != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Trainer Found");
            alert.setHeaderText(null);
            alert.setContentText(formatTrainer(found));
            alert.showAndWait(); // Wait until OK is pressed

            // Navigate back to Trainer Menu
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

        } else {
            showAlert(AlertType.WARNING, "No Match", "No trainer matched the given input.");
        }
    }


    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
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
