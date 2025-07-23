package pokedex.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Item;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SearchItemController {

    @FXML private TextField keywordField;
    @FXML private Button submitBtn;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    public SearchItemController(PokedexManager pokedexManager,
                                MoveManager moveManager,
                                ItemManager itemManager,
                                TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String kw = keywordField.getText().trim();
        if (kw.isEmpty()) {
            showSimpleAlert(Alert.AlertType.WARNING, "Please enter an item name or keyword.");
            return;
        }

        List<Item> found = itemManager.getAllItems().stream()
                .filter(i -> i.getName().toLowerCase().contains(kw.toLowerCase()))
                .toList();

        if (found.isEmpty()) {
            showSimpleAlert(Alert.AlertType.INFORMATION, "No items found matching: " + kw);
        } else {
            StringBuilder sb = new StringBuilder();
            found.forEach(i -> sb.append(i).append("\n"));
            showScrollableDialog("Search Results", sb.toString());
        }
    }

    private void showSimpleAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.initOwner(submitBtn.getScene().getWindow());
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private void showScrollableDialog(String title, String content) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(submitBtn.getScene().getWindow());

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, new ButtonType("Back to Main Menu", ButtonBar.ButtonData.CANCEL_CLOSE));

        TextArea textArea = new TextArea(content);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox container = new VBox(scrollPane);
        container.setPrefSize(500, 400);
        dialogPane.setContent(container);

        Optional<ButtonType> choice = dialog.showAndWait();
        if (choice.isPresent() && choice.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
            goToMainMenu();
        }
    }

    private void goToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            loader.setControllerFactory(param -> new MainMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();

            Stage stage = (Stage) submitBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showSimpleAlert(Alert.AlertType.ERROR, "Failed to load Main Menu.");
        }
    }
}