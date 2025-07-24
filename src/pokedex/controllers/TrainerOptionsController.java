package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Item;
import pokedex.models.Move;
import pokedex.models.Pokemon;
import pokedex.models.Trainer;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

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
    @FXML
    private void handleSell(ActionEvent event) {
        Map<String, Trainer.BagItem> bag = selectedTrainer.getItemBag();

        if (bag == null || bag.isEmpty()) {
            showAlert("Sell Item", "You have no items to sell.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> ownedItemNames = new ArrayList<>(bag.keySet());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(ownedItemNames.get(0), ownedItemNames);
        dialog.setTitle("Sell Item");
        dialog.setHeaderText("Select an item to sell");
        dialog.setContentText("Choose item:");

        dialog.showAndWait().ifPresent(itemName -> {
            // Check again in case the quantity is 0 or was removed during another action
            Trainer.BagItem item = bag.get(itemName);
            if (item == null || item.getQuantity() == 0) {
                showAlert("Sell Item", "You no longer have that item.", Alert.AlertType.ERROR);
                return;
            }

            // Pre-sale money for refund calculation (since Trainer.sellItem doesn't return anything)
            int originalMoney = selectedTrainer.getMoney();
            selectedTrainer.sellItem(itemName);
            int updatedMoney = selectedTrainer.getMoney();

            int earned = updatedMoney - originalMoney;

            if (earned > 0) {
                showAlert("Sell Item", "Sold 1 " + itemName + " for ₱" + earned + ".", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Sell Item", "⚠️ This item cannot be sold.", Alert.AlertType.WARNING);
            }
        });
    }
    @FXML
    private void handleUse(ActionEvent event) {
        Map<String, Trainer.BagItem> bag = selectedTrainer.getItemBag();

        if (bag.isEmpty()) {
            showAlert("Use Item", "You have no items to use.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> ownedItemNames = new ArrayList<>(bag.keySet());

        ChoiceDialog<String> itemDialog = new ChoiceDialog<>(ownedItemNames.get(0), ownedItemNames);
        itemDialog.setTitle("Use Item");
        itemDialog.setHeaderText("Select an item to use");
        itemDialog.setContentText("Choose item:");

        itemDialog.showAndWait().ifPresent(itemName -> {
            Trainer.BagItem bagItem = bag.get(itemName);

            if (bagItem == null || bagItem.getQuantity() == 0) {
                showAlert("Use Item", "You no longer have that item.", Alert.AlertType.ERROR);
                return;
            }

            List<Pokemon> lineup = selectedTrainer.getLineup();
            if (lineup.isEmpty()) {
                showAlert("Use Item", "You have no Pokémon in your lineup.", Alert.AlertType.INFORMATION);
                return;
            }

            List<String> pokemonNames = lineup.stream().map(Pokemon::getName).toList();

            ChoiceDialog<String> pokeDialog = new ChoiceDialog<>(pokemonNames.get(0), pokemonNames);
            pokeDialog.setTitle("Choose Pokémon");
            pokeDialog.setHeaderText("Select a Pokémon to use the item on");
            pokeDialog.setContentText("Choose Pokémon:");

            pokeDialog.showAndWait().ifPresent(pokemonName -> {
                Pokemon target = lineup.stream()
                        .filter(p -> p.getName().equals(pokemonName))
                        .findFirst()
                        .orElse(null);

                if (target == null) {
                    showAlert("Use Item", "Selected Pokémon not found.", Alert.AlertType.ERROR);
                    return;
                }

                // 👇 This will work as long as the item doesn't require actual scanner input
                selectedTrainer.useItem(bagItem.getItem(), target, pokedexManager, new Scanner(System.in));

                bagItem.decrement();
                if (bagItem.getQuantity() == 0) {
                    bag.remove(itemName);
                }


                showAlert("Use Item", "Used " + itemName + " on " + target.getName() + ".", Alert.AlertType.INFORMATION);
            });
        });
    }
    @FXML
    private void handleAdd(ActionEvent event) {
        List<Pokemon> allPokemon = pokedexManager.getAllPokemon();

        if (allPokemon.isEmpty()) {
            showAlert("Add Pokémon", "No Pokémon available in the Pokédex.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> pokemonNames = allPokemon.stream()
                .map(Pokemon::getName)
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(pokemonNames.get(0), pokemonNames);
        dialog.setTitle("Add Pokémon");
        dialog.setHeaderText("Select a Pokémon to add to your team");
        dialog.setContentText("Choose Pokémon:");

        dialog.showAndWait().ifPresent(pokemonName -> {
            Pokemon p = pokedexManager.getPokemonByName(pokemonName);
            if (p == null) {
                showAlert("Add Pokémon", "Pokémon not found in Pokédex.", Alert.AlertType.ERROR);
                return;
            }

            boolean addedToLineup = selectedTrainer.addPokemon(p);
            if (addedToLineup) {
                showAlert("Add Pokémon", p.getName() + " was added to your lineup.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Add Pokémon", p.getName() + " was added to storage (lineup is full).", Alert.AlertType.INFORMATION);
            }
        });
    }
    @FXML
    private void handleGive(ActionEvent event) {
        List<Pokemon> lineup = selectedTrainer.getLineup();
        if (lineup.isEmpty()) {
            showAlert("Give Item", "You have no Pokémon in your lineup.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> pokemonNames = lineup.stream().map(Pokemon::getName).toList();
        ChoiceDialog<String> pokeDialog = new ChoiceDialog<>(pokemonNames.get(0), pokemonNames);
        pokeDialog.setTitle("Give Item");
        pokeDialog.setHeaderText("Select a Pokémon");
        pokeDialog.setContentText("Choose Pokémon:");

        pokeDialog.showAndWait().ifPresent(pokemonName -> {
            Pokemon selectedPokemon = lineup.stream()
                    .filter(p -> p.getName().equals(pokemonName))
                    .findFirst()
                    .orElse(null);

            if (selectedPokemon == null) {
                showAlert("Give Item", "Selected Pokémon not found.", Alert.AlertType.ERROR);
                return;
            }

            Map<String, Trainer.BagItem> bag = selectedTrainer.getItemBag();
            if (bag == null || bag.isEmpty()) {
                showAlert("Give Item", "You have no items in your bag.", Alert.AlertType.INFORMATION);
                return;
            }

            List<String> itemNames = new ArrayList<>(bag.keySet());
            ChoiceDialog<String> itemDialog = new ChoiceDialog<>(itemNames.get(0), itemNames);
            itemDialog.setTitle("Give Item");
            itemDialog.setHeaderText("Select an item to give to " + selectedPokemon.getName());
            itemDialog.setContentText("Choose item:");

            itemDialog.showAndWait().ifPresent(itemName -> {
                Item item = itemManager.getItemByName(itemName);
                if (item == null) {
                    showAlert("Give Item", "Item not found.", Alert.AlertType.ERROR);
                    return;
                }

                // If already holding an item
                if (selectedPokemon.getHeldItem() != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Replace Held Item");
                    confirm.setHeaderText(selectedPokemon.getName() + " is already holding: " + selectedPokemon.getHeldItem().getName());
                    confirm.setContentText("Giving a new item will discard the current one. Continue?");
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        showAlert("Give Item", "Action cancelled.", Alert.AlertType.INFORMATION);
                        return;
                    }
                }

                selectedPokemon.setHeldItem(item);
                showAlert("Give Item", selectedPokemon.getName() + " is now holding " + item.getName() + ".", Alert.AlertType.INFORMATION);
            });
        });
    }
    @FXML
    private void handleRemove(ActionEvent event) {
        List<Pokemon> lineup = selectedTrainer.getLineup();
        if (lineup.isEmpty()) {
            showAlert("Remove Held Item", "You have no Pokémon in your lineup.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> pokemonNames = lineup.stream().map(Pokemon::getName).toList();
        ChoiceDialog<String> pokeDialog = new ChoiceDialog<>(pokemonNames.get(0), pokemonNames);
        pokeDialog.setTitle("Remove Held Item");
        pokeDialog.setHeaderText("Select a Pokémon");
        pokeDialog.setContentText("Choose Pokémon:");

        pokeDialog.showAndWait().ifPresent(pokemonName -> {
            Pokemon selectedPokemon = lineup.stream()
                    .filter(p -> p.getName().equals(pokemonName))
                    .findFirst()
                    .orElse(null);

            if (selectedPokemon == null) {
                showAlert("Remove Held Item", "Selected Pokémon not found.", Alert.AlertType.ERROR);
                return;
            }

            if (selectedPokemon.getHeldItem() == null) {
                showAlert("Remove Held Item", selectedPokemon.getName() + " is not holding any item.", Alert.AlertType.INFORMATION);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Removal");
            confirm.setHeaderText(selectedPokemon.getName() + " is currently holding: " + selectedPokemon.getHeldItem().getName());
            confirm.setContentText("Are you sure you want to remove and discard it?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                showAlert("Remove Held Item", "Action cancelled.", Alert.AlertType.INFORMATION);
                return;
            }

            String removedItem = selectedPokemon.getHeldItem().getName();
            selectedPokemon.setHeldItem(null);
            showAlert("Remove Held Item", removedItem + " was removed and discarded.", Alert.AlertType.INFORMATION);
        });
    }
    @FXML
    private void handleSwitch(ActionEvent event) {
        List<Pokemon> lineup = selectedTrainer.getLineup();
        List<Pokemon> storage = selectedTrainer.getStorage();

        if (lineup.isEmpty() || storage.isEmpty()) {
            showAlert("Switch Pokémon", "You need at least one Pokémon in both lineup and storage to switch.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> lineupNames = lineup.stream().map(Pokemon::getName).toList();
        List<String> storageNames = storage.stream().map(Pokemon::getName).toList();

        ChoiceDialog<String> lineupDialog = new ChoiceDialog<>(lineupNames.get(0), lineupNames);
        lineupDialog.setTitle("Switch Pokémon");
        lineupDialog.setHeaderText("Select a Pokémon from the LINEUP");
        lineupDialog.setContentText("Choose Pokémon:");

        lineupDialog.showAndWait().ifPresent(lineupName -> {
            int lineupIndex = IntStream.range(0, lineup.size())
                    .filter(i -> lineup.get(i).getName().equals(lineupName))
                    .findFirst()
                    .orElse(-1);

            if (lineupIndex == -1) {
                showAlert("Switch Pokémon", "Lineup Pokémon not found.", Alert.AlertType.ERROR);
                return;
            }

            // Now prompt for storage Pokémon
            ChoiceDialog<String> storageDialog = new ChoiceDialog<>(storageNames.get(0), storageNames);
            storageDialog.setTitle("Switch Pokémon");
            storageDialog.setHeaderText("Select a Pokémon from the STORAGE");
            storageDialog.setContentText("Choose Pokémon:");

            storageDialog.showAndWait().ifPresent(storageName -> {
                int storageIndex = IntStream.range(0, storage.size())
                        .filter(i -> storage.get(i).getName().equals(storageName))
                        .findFirst()
                        .orElse(-1);

                if (storageIndex == -1) {
                    showAlert("Switch Pokémon", "Storage Pokémon not found.", Alert.AlertType.ERROR);
                    return;
                }

                selectedTrainer.switchPokemon(lineupIndex, storageIndex);
                showAlert("Switch Successful", "Switched " + lineupName + " with " + storageName + ".", Alert.AlertType.INFORMATION);
            });
        });
    }
    @FXML
    private void handleRelease(ActionEvent event) {
        List<Pokemon> allPokemon = new ArrayList<>();
        allPokemon.addAll(selectedTrainer.getLineup());
        allPokemon.addAll(selectedTrainer.getStorage());

        if (allPokemon.isEmpty()) {
            showAlert("Release Pokémon", "You have no Pokémon to release.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> pokemonNames = allPokemon.stream()
                .map(Pokemon::getName)
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(pokemonNames.get(0), pokemonNames);
        dialog.setTitle("Release Pokémon");
        dialog.setHeaderText("Select a Pokémon to release");
        dialog.setContentText("Choose Pokémon:");

        dialog.showAndWait().ifPresent(nameToRelease -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Release");
            confirmAlert.setHeaderText("Are you sure you want to release " + nameToRelease + "?");
            confirmAlert.setContentText("This cannot be undone.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedTrainer.releasePokemon(nameToRelease);
                showAlert("Released", nameToRelease + " has been released.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Canceled", "Release canceled.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleTeach(ActionEvent event) {
        List<Pokemon> lineup = selectedTrainer.getLineup();
        if (lineup.isEmpty()) {
            showAlert("Teach Move", "You have no Pokémon in your lineup.", Alert.AlertType.INFORMATION);
            return;
        }

        // Step 1: Select Pokémon
        List<String> pokemonNames = lineup.stream().map(Pokemon::getName).toList();
        ChoiceDialog<String> pokeDialog = new ChoiceDialog<>(pokemonNames.get(0), pokemonNames);
        pokeDialog.setTitle("Select Pokémon");
        pokeDialog.setHeaderText("Choose which Pokémon to teach a move to:");
        pokeDialog.setContentText("Pokémon:");

        Optional<String> selectedPoke = pokeDialog.showAndWait();
        if (selectedPoke.isEmpty()) return;

        Pokemon selectedPokemon = lineup.stream()
                .filter(p -> p.getName().equalsIgnoreCase(selectedPoke.get()))
                .findFirst()
                .orElse(null);
        if (selectedPokemon == null) return;

        // Step 2: Select Move
        List<String> moveNames = moveManager.getAllMoves().stream().map(Move::getName).toList();
        ChoiceDialog<String> moveDialog = new ChoiceDialog<>(moveNames.get(0), moveNames);
        moveDialog.setTitle("Select Move");
        moveDialog.setHeaderText("Choose a move to teach to " + selectedPokemon.getName());
        moveDialog.setContentText("Move:");

        Optional<String> selectedMoveName = moveDialog.showAndWait();
        if (selectedMoveName.isEmpty()) return;

        Move move = moveManager.getMoveByName(selectedMoveName.get().trim());
        if (move == null) {
            showAlert("Error", "Move not found.", Alert.AlertType.ERROR);
            return;
        }

        // Step 3: Attempt to teach move
        String result = selectedTrainer.teachMove(selectedPokemon, move, moveManager);
        if (result.equals("Needs to forget a move first")) {
            List<String> currentMoves = selectedPokemon.getMoveSet();
            ChoiceDialog<String> forgetDialog = new ChoiceDialog<>(currentMoves.get(0), currentMoves);
            forgetDialog.setTitle("Forget a Move");
            forgetDialog.setHeaderText(selectedPokemon.getName() + " already knows 4 moves.");
            forgetDialog.setContentText("Choose a move to forget:");

            Optional<String> moveToForget = forgetDialog.showAndWait();
            if (moveToForget.isEmpty()) {
                showAlert("Canceled", "Move teaching canceled.", Alert.AlertType.INFORMATION);
                return;
            }

            // Confirm
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Move");
            confirm.setHeaderText("Replace " + moveToForget.get() + " with " + move.getName() + "?");
            confirm.setContentText("Are you sure?");

            Optional<ButtonType> confirmation = confirm.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                boolean success = selectedTrainer.forgetAndLearnMove(selectedPokemon, moveToForget.get(), move, moveManager);
                if (success) {
                    showAlert("Move Taught", selectedPokemon.getName() + " learned " + move.getName() + "!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Failed", "Move teaching failed. HM moves cannot be forgotten.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Canceled", "Move teaching canceled.", Alert.AlertType.INFORMATION);
            }
        } else {
            // Success or failure directly from teachMove
            showAlert("Result", result, Alert.AlertType.INFORMATION);
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


