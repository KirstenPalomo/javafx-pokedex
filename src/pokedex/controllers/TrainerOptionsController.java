package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Item;
import pokedex.models.Trainer;

import java.io.IOException;
import java.util.Optional;

public class TrainerOptionsController {

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;
    private final Trainer selectedTrainer;

    public TrainerOptionsController(PokedexManager pokedexManager,
                                    MoveManager moveManager,
                                    ItemManager itemManager,
                                    TrainerManager trainerManager,
                                    Trainer selectedTrainer){
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
        this.selectedTrainer = selectedTrainer;// <- Add this
    }

    @FXML
    private void handleBuyItem(ActionEvent event) {
        // Filter only items that can be bought (have a minBuyingPrice)
        var buyableItems = itemManager.getAllItems().stream()
                .filter(item -> item.getMinBuyingPrice() != null)
                .toList();

        if (buyableItems.isEmpty()) {
            showAlert("No Items", "⚠️ No items available for purchase.", Alert.AlertType.WARNING);
            return;
        }

        ChoiceDialog<Item> dialog = new ChoiceDialog<>(buyableItems.get(0), buyableItems);
        dialog.setTitle("Buy Item");
        dialog.setHeaderText("Select an item to buy");
        dialog.setContentText("Choose item:");

        dialog.showAndWait().ifPresent(selectedItem -> {
            String feedback = selectedTrainer.buyItem(selectedItem);
            Alert.AlertType type = feedback.startsWith("SUCCESS:")
                    ? Alert.AlertType.INFORMATION
                    : Alert.AlertType.ERROR;

            String displayMessage = feedback
                    .replace("SUCCESS: ", "")
                    .replace("ERROR: ", "");

            showAlert("Buy Result", displayMessage, type);
        });
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        StringBuilder profile = new StringBuilder();

        profile.append("Trainer: ").append(selectedTrainer.getName()).append("\n")
                .append("Sex     : ").append(selectedTrainer.getSex()).append("\n")
                .append("Hometown: ").append(selectedTrainer.getHometown()).append("\n")
                .append("About   : ").append(selectedTrainer.getDescription()).append("\n")
                .append(String.format("Money   : ₱%,d%n", selectedTrainer.getMoney()));

        // Lineup
        profile.append("\nLineup (").append(selectedTrainer.getLineup().size()).append("/6):\n");
        if (selectedTrainer.getLineup().isEmpty()) {
            profile.append("  None\n");
        } else {
            for (var p : selectedTrainer.getLineup()) {
                profile.append("  • ").append(p.getName())
                        .append(" (#").append(String.format("%03d", p.getPokedexNumber())).append(")\n");
            }
        }

        // Storage
        profile.append("\nStorage (").append(selectedTrainer.getStorage().size()).append("):\n");
        if (selectedTrainer.getStorage().isEmpty()) {
            profile.append("  None\n");
        } else {
            for (var p : selectedTrainer.getStorage()) {
                profile.append("  • ").append(p.getName())
                        .append(" (#").append(String.format("%03d", p.getPokedexNumber())).append(")\n");
            }
        }

        // Bag
        profile.append("\nInventory:\n");
        var bag = selectedTrainer.getItemBag();
        if (bag == null || bag.isEmpty()) {
            profile.append("  None\n");
        } else {
            bag.forEach((itemName, bagItem) ->
                    profile.append("  • ").append(itemName)
                            .append(" ×").append(bagItem.getQuantity()).append("\n")
            );
        }

        showAlert("Trainer Profile", profile.toString(), Alert.AlertType.INFORMATION);
    }


    @FXML
    private void handleBack(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewTrainer.fxml"));
            loader.setControllerFactory(param -> new ViewTrainerController(
                    pokedexManager, moveManager, itemManager, trainerManager));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to Trainer Menu screen.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


