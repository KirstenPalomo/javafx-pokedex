package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pokedex.models.Move;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.ItemManager;
import pokedex.managers.TrainerManager;

import java.util.List;

public class AddMoveController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> classificationCombo;
    @FXML private ComboBox<String> type1Combo;
    @FXML private ComboBox<String> type2Combo;

    private final MoveManager moveManager;
    private final PokedexManager pokedexManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    private final List<String> validTypes = List.of(
            "Normal", "Fire", "Water", "Grass", "Electric", "Ice", "Fighting", "Poison",
            "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark",
            "Steel", "Fairy"
    );

    public AddMoveController(PokedexManager pokedexManager, MoveManager moveManager,
                             ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    public void initialize() {
        classificationCombo.getItems().addAll("HM", "TM");
        type1Combo.getItems().addAll(validTypes);
        type2Combo.getItems().add("None");
        type2Combo.getItems().addAll(validTypes);
        type2Combo.setValue("None");
    }

    @FXML
    private void handleAddMove(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            String classification = classificationCombo.getValue();
            String type1 = type1Combo.getValue();
            String type2 = type2Combo.getValue();
            if ("None".equals(type2)) type2 = null;

            if (name.isEmpty() || description.isEmpty() || classification == null || type1 == null) {
                showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all required fields.");
                return;
            }

            if (moveManager.hasMoveWithName(name)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Move", "A move named \"" + name + "\" already exists.");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Move Add");
            confirmAlert.setHeaderText("Are you sure you want to add this move?");
            confirmAlert.setContentText("Name: " + name + "\nType: " + type1 + (type2 != null ? "/" + type2 : ""));
            confirmAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirmAlert.showAndWait();

            if (confirmAlert.getResult() == ButtonType.NO) {
                return;
            }

            Move move = new Move(name, description, classification, type1, type2);
            moveManager.addMove(move);
            clearFields();

            showAlert(Alert.AlertType.INFORMATION, "Move Added", "Move \"" + name + "\" was successfully added.");
            returnToMoveMenu(event);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        descriptionArea.clear();
        classificationCombo.getSelectionModel().clearSelection();
        type1Combo.getSelectionModel().clearSelection();
        type2Combo.setValue("None");
    }

    private void returnToMoveMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MoveMenu.fxml"));
            loader.setControllerFactory(param -> new MoveMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}