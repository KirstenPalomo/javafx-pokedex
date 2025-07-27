package pokedex.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pokedex.JsonManager;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Item;

import java.io.IOException;

public class ItemMenuController {

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    public ItemMenuController(PokedexManager pokedexManager, MoveManager moveManager,
                              ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    private Button viewBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private Button backBtn;

    @FXML
    private void handleView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewItem.fxml"));
            loader.setControllerFactory(param -> new ViewItemController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
private void handleSearch(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchItem.fxml"));
        loader.setControllerFactory(param -> new SearchItemController(
            pokedexManager, moveManager, itemManager, trainerManager
        ));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddItem(ActionEvent event) {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("Enter item details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField categoryField = new TextField();
        TextField descriptionField = new TextField();
        TextField effectsField = new TextField();
        TextField minPriceField = new TextField();
        TextField maxPriceField = new TextField();
        TextField sellingPriceField = new TextField();

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1); grid.add(categoryField, 1, 1);
        grid.add(new Label("Description:"), 0, 2); grid.add(descriptionField, 1, 2);
        grid.add(new Label("Effects:"), 0, 3); grid.add(effectsField, 1, 3);
        grid.add(new Label("Min Price:"), 0, 4); grid.add(minPriceField, 1, 4);
        grid.add(new Label("Max Price:"), 0, 5); grid.add(maxPriceField, 1, 5);
        grid.add(new Label("Selling Price:"), 0, 6); grid.add(sellingPriceField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Item when "Add" is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String category = categoryField.getText().trim();
                    String description = descriptionField.getText().trim();
                    String effects = effectsField.getText().trim();
                    String minText = minPriceField.getText().trim();
                    String maxText = maxPriceField.getText().trim();
                    String sellText = sellingPriceField.getText().trim();

                    if (name.isEmpty() || category.isEmpty() || description.isEmpty() || effects.isEmpty() || sellText.isEmpty()) {
                        showAlert("All required fields must be filled.");
                        return null;
                    }

                    Integer min = minText.isEmpty() ? null : Integer.parseInt(minText);
                    Integer max = maxText.isEmpty() ? null : Integer.parseInt(maxText);
                    int sell = Integer.parseInt(sellText);

                    return new Item(name, category, description, effects, min, max, sell);

                } catch (NumberFormatException e) {
                    showAlert("Invalid number input. Please check price fields.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newItem -> {
            if (!itemManager.hasItemWithName(newItem.getName())) {
                itemManager.addItem(newItem);
                JsonManager.saveItems(itemManager.getAllItems()); // <-- Save immediately
                showAlert("Item added successfully!");
            } else {
                showAlert("Item with this name already exists.");
            }
        });

    }



    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            loader.setControllerFactory(param -> new MainMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
