/**
 * This manages the Search Item screen in the Enhanced Pokédex system.
 * Allows users to enter keywords, view matching items with full details,
 * and optionally return to the Main Menu.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.controllers;

// JavaFX UI components and layout
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
// Managers
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
// Model
import pokedex.models.Item;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for searching items in the Pokédex.
 * Allows users to input keywords and view matching items with full details.
 * Displays results in a scrollable dialog or shows appropriate alerts.
 */
public class SearchItemController {

    @FXML private TextField keywordField;
    @FXML private Button submitBtn;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs a SearchItemController with the necessary manager dependencies.
     *
     * @param pokedexManager  manages Pokémon data
     * @param moveManager     manages TM/HM data
     * @param itemManager     manages item data and search
     * @param trainerManager  manages trainer data
     */
    public SearchItemController(PokedexManager pokedexManager,
                                MoveManager moveManager,
                                ItemManager itemManager,
                                TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Handles the search button action.
     * Retrieves the keyword, filters matching items, and displays results or warnings accordingly.
     *
     * @param event the action event from the UI
     */
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
            for (Item item : found) {
                sb.append("Name: ").append(item.getName()).append("\n");
                sb.append("Category: ").append(item.getCategory()).append("\n");
                sb.append("Description: ").append(item.getDescription()).append("\n");
                sb.append("Effect: ").append(item.getEffects()).append("\n");

                String buy = item.getMinBuyingPrice() == null ? "Not sold" : "₱" + String.format("%,d", item.getMinBuyingPrice());
                String sell = String.format("%,d", item.getSellingPrice());

                sb.append("Buy Price: ").append(buy).append("\n");
                sb.append("Sell Price: ").append(sell).append("\n");

                sb.append("-------------------------------\n");
            }
            showScrollableDialog("Search Results", sb.toString());
        }
    }

    /**
     * Shows a basic alert with a message and OK button.
     *
     * @param type the type of alert (e.g., INFORMATION, WARNING, ERROR)
     * @param msg  the message to display in the alert
     */
    private void showSimpleAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.initOwner(submitBtn.getScene().getWindow());
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    /**
     * Displays a scrollable dialog containing item search results.
     * Offers option to return to the main menu.
     *
     * @param title   the title of the dialog window
     * @param content the full text content to display
     */
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

    /**
     * Loads the main menu interface after clicking "Back to Main Menu."
     * Displays an error alert if loading fails.
     */
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