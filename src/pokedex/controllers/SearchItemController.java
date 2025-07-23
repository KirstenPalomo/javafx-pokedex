package pokedex.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
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

    // your DI constructor
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

        // do the search
        List<Item> found = itemManager.getAllItems().stream()
                .filter(i -> i.getName().toLowerCase().contains(kw.toLowerCase()))
                .toList();

        // build content text
        String content;
        if (found.isEmpty()) {
            content = "No items found matching: " + kw;
        } else {
            StringBuilder sb = new StringBuilder();
            found.forEach(i -> sb.append(i).append("\n"));
            content = sb.toString();
        }

        // show single Alert with OK + Back buttons
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(submitBtn.getScene().getWindow());
        alert.setTitle("Search Results");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        // replace buttons
        ButtonType okBtn   = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType backBtn = new ButtonType("Back to Main Menu", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okBtn, backBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == backBtn) {
            goToMainMenu();
        }
        // otherwise just close and stay
    }

    private void showSimpleAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.initOwner(submitBtn.getScene().getWindow());
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private void goToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/MainMenu.fxml")
            );
            // ensure your custom constructor is called:
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
