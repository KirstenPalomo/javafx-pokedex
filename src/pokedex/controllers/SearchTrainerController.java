package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Pokemon;
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
            showScrollableDialog("Trainer(s) Found", resultBuilder.toString());
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
    private void showScrollableDialog(String title, String content) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(idField.getScene().getWindow());

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK);

        TextArea textArea = new TextArea(content);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefSize(500, 400);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox container = new VBox(scrollPane);
        container.setPrefSize(500, 400);
        dialogPane.setContent(container);

        dialog.showAndWait();
    }


    private String formatTrainer(Trainer t) {
        StringBuilder sb = new StringBuilder();

        // Basic Info
        sb.append("ID: ").append(t.getTrainerID())
                .append("\nName: ").append(t.getName())
                .append("\nSex: ").append(t.getSex())
                .append("\nBirthdate: ").append(t.getBirthdate())
                .append("\nHometown: ").append(t.getHometown())
                .append("\nDescription: ").append(t.getDescription())
                .append("\nMoney: ₱").append(String.format("%,d", t.getMoney()));

        // Lineup
        sb.append("\n\nLineup (").append(t.getLineup().size()).append("/6):");
        if (t.getLineup().isEmpty()) {
            sb.append("\n  (No Pokémon in lineup)");
        } else {
            for (Pokemon p : t.getLineup()) {
                sb.append("\n- ").append(p.briefProfile());
            }
        }

        // Item Inventory
        sb.append("\n\nInventory:");
        if (t.getItemBag().isEmpty()) {
            sb.append("\n  (No items)");
        } else {
            for (Trainer.BagItem bagItem : t.getItemBag().values()) {
                sb.append("\n- ").append(bagItem.getItem().getName())
                        .append(" × ").append(bagItem.getQuantity());
            }
        }

        return sb.toString();
    }

}