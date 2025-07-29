package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import pokedex.JsonManager;
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
        // Filter buyable items
        var buyableItems = itemManager.getAllItems().stream()
                .filter(item -> item.getMinBuyingPrice() != null)
                .toList();

        if (buyableItems.isEmpty()) {
            showAlert("No Items", "⚠️ No items available for purchase.", Alert.AlertType.WARNING);
            return;
        }

        // Let user choose item
        ChoiceDialog<Item> dialog = new ChoiceDialog<>(buyableItems.get(0), buyableItems);
        dialog.setTitle("Buy Item");
        dialog.setHeaderText("Select an item to buy");
        dialog.setContentText("Choose item:");

        dialog.showAndWait().ifPresent(selectedItem -> {
            TextInputDialog quantityDialog = new TextInputDialog("1");
            quantityDialog.setTitle("Quantity");
            quantityDialog.setHeaderText("How many \"" + selectedItem.getName() + "\" do you want to buy?");
            quantityDialog.setContentText("Enter quantity:");

            Optional<String> quantityInput = quantityDialog.showAndWait();
            if (quantityInput.isEmpty()) return;

            int quantity;
            try {
                quantity = Integer.parseInt(quantityInput.get().trim());
                if (quantity <= 0) {
                    showAlert("Invalid Input", "Quantity must be a positive number.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.", Alert.AlertType.ERROR);
                return;
            }

            int unitPrice = selectedItem.getMinBuyingPrice();
            int totalBought = 0;
            int rareCandiesReceived = 0;
            int totalSpent = 0;

            for (int i = 0; i < quantity; i++) {
                String result = selectedTrainer.buyItem(selectedItem);
                if (result.startsWith("SUCCESS:")) {
                    totalBought++;
                    totalSpent += unitPrice;
                    if (result.contains("Bonus:")) {
                        rareCandiesReceived++;
                    }
                } else {
                    break; // Stop buying when funds or item limits are hit
                }
            }

            if (totalBought == 0) {
                showAlert("Buy Failed", "No items were bought.\nPossible reasons: insufficient funds or item limits.", Alert.AlertType.WARNING);
                return;
            }

            StringBuilder resultMsg = new StringBuilder();
            resultMsg.append("Bought ").append(selectedItem.getName()).append(" ×").append(totalBought)
                    .append(" for ₱").append(String.format("%,d", totalSpent)).append(".");

            if (rareCandiesReceived > 0) {
                resultMsg.append("\n🎁 Bonus: Received ").append(rareCandiesReceived).append(" Rare Cand").append(rareCandiesReceived == 1 ? "y!" : "ies!");
            }

            showAlert("Buy Result", resultMsg.toString(), Alert.AlertType.INFORMATION);
        });
    }


    @FXML
    private void handleViewProfile(ActionEvent event) {
        StringBuilder profile = new StringBuilder();

        // Basic Info
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
                profile.append("  • ").append(p.getName()).append(" (#").append(String.format("%03d", p.getPokedexNumber())).append(")\n")
                        .append("    Level     : ").append(p.getBaseLevel()).append("\n")
                        .append("    Type      : ").append(p.getType1());
                if (p.getType2() != null && !p.getType2().isEmpty()) {
                    profile.append("/").append(p.getType2());
                }
                profile.append("\n")
                        .append("    Stats     : HP ").append(p.getHp())
                        .append(" | ATK ").append(p.getAttack())
                        .append(" | DEF ").append(p.getDefense())
                        .append(" | SPD ").append(p.getSpeed()).append("\n")
                        .append("    Moves     : ").append(p.getMoveSet().isEmpty() ? "(None)" : String.join(", ", p.getMoveSet())).append("\n")
                        .append("    Held Item : ").append(p.getHeldItem() != null ? p.getHeldItem().getName() : "None").append("\n\n");
            }
        }

        // Storage
        profile.append("Storage (").append(selectedTrainer.getStorage().size()).append("):\n");
        if (selectedTrainer.getStorage().isEmpty()) {
            profile.append("  None\n");
        } else {
            for (var p : selectedTrainer.getStorage()) {
                profile.append("  • ").append(p.getName()).append(" (#").append(String.format("%03d", p.getPokedexNumber())).append(")\n")
                        .append("    Level     : ").append(p.getBaseLevel()).append("\n")
                        .append("    Type      : ").append(p.getType1());
                if (p.getType2() != null && !p.getType2().isEmpty()) {
                    profile.append("/").append(p.getType2());
                }
                profile.append("\n")
                        .append("    Stats     : HP ").append(p.getHp())
                        .append(" | ATK ").append(p.getAttack())
                        .append(" | DEF ").append(p.getDefense())
                        .append(" | SPD ").append(p.getSpeed()).append("\n")
                        .append("    Moves     : ").append(p.getMoveSet().isEmpty() ? "(None)" : String.join(", ", p.getMoveSet())).append("\n")
                        .append("    Held Item : ").append(p.getHeldItem() != null ? p.getHeldItem().getName() : "None").append("\n\n");
            }
        }

        // Bag
        profile.append("Inventory:\n");
        var bag = selectedTrainer.getItemBag();
        if (bag == null || bag.isEmpty()) {
            profile.append("  None\n");
        } else {
            bag.forEach((itemName, bagItem) ->
                    profile.append("  • ").append(itemName)
                            .append(" ×").append(bagItem.getQuantity()).append("\n")
            );
        }

        showScrollableAlert("Trainer Profile", profile.toString(), Alert.AlertType.INFORMATION);
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
            Trainer.BagItem bagItem = bag.get(itemName);
            if (bagItem == null || bagItem.getQuantity() == 0) {
                showAlert("Sell Item", "You no longer have that item.", Alert.AlertType.ERROR);
                return;
            }

            int maxQuantity = bagItem.getQuantity();

            // Ask how many to sell
            TextInputDialog quantityDialog = new TextInputDialog("1");
            quantityDialog.setTitle("Sell Quantity");
            quantityDialog.setHeaderText("How many \"" + itemName + "\" do you want to sell?");
            quantityDialog.setContentText("You currently own: " + maxQuantity);

            Optional<String> quantityInput = quantityDialog.showAndWait();
            if (quantityInput.isEmpty()) return;

            int quantity;
            try {
                quantity = Integer.parseInt(quantityInput.get().trim());
                if (quantity <= 0 || quantity > maxQuantity) {
                    showAlert("Invalid Quantity", "Please enter a number from 1 to " + maxQuantity + ".", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.", Alert.AlertType.ERROR);
                return;
            }

            int originalMoney = selectedTrainer.getMoney();
            int unitPrice = bagItem.getItem().getSellingPrice();
            int totalEarned = 0;
            int totalSold = 0;

            for (int i = 0; i < quantity; i++) {
                int before = selectedTrainer.getMoney();
                selectedTrainer.sellItem(itemName);
                int after = selectedTrainer.getMoney();

                if (after > before) {
                    totalEarned += (after - before);
                    totalSold++;
                } else {
                    break; // stop if item can no longer be sold (e.g., not sellable)
                }
            }

            if (totalSold == 0) {
                showAlert("Sell Failed", "⚠️ This item cannot be sold.", Alert.AlertType.WARNING);
            } else {
                showAlert("Sell Success", "Sold " + totalSold + " × " + itemName +
                        "\nTotal earned: ₱" + String.format("%,d", totalEarned), Alert.AlertType.INFORMATION);
            }
        });
    }


    @FXML
    private void handleUse(ActionEvent event) {
        List<Pokemon> lineup = selectedTrainer.getLineup();
        if (lineup.isEmpty()) {
            showAlert("Use Item", "You have no Pokémon in your lineup.", Alert.AlertType.INFORMATION);
            return;
        }

        // Step 1: Choose Pokémon
        List<String> pokemonNames = lineup.stream().map(Pokemon::getName).toList();
        ChoiceDialog<String> pokeDialog = new ChoiceDialog<>(pokemonNames.get(0), pokemonNames);
        pokeDialog.setTitle("Use Item");
        pokeDialog.setHeaderText("Select a Pokémon");
        pokeDialog.setContentText("Which Pokémon?");

        pokeDialog.showAndWait().ifPresent(pokemonName -> {
            Pokemon target = lineup.stream()
                    .filter(p -> p.getName().equals(pokemonName))
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                showAlert("Use Item", "Selected Pokémon not found.", Alert.AlertType.ERROR);
                return;
            }

            // Step 2: Choose source
            List<String> sourceOptions = List.of("Held Item", "Inventory");
            ChoiceDialog<String> sourceDialog = new ChoiceDialog<>(sourceOptions.get(0), sourceOptions);
            sourceDialog.setTitle("Use Item");
            sourceDialog.setHeaderText("Select Source");
            sourceDialog.setContentText("Which item do you want to use?");

            sourceDialog.showAndWait().ifPresent(source -> {
                StringBuilder log = new StringBuilder();

                if (source.equals("Held Item")) {
                    Item held = target.getHeldItem();
                    if (held == null) {
                        showAlert("Use Item", target.getName() + " is not holding any item.", Alert.AlertType.WARNING);
                        return;
                    }

                    String result = selectedTrainer.useHeldItem(target, pokedexManager);

                    if (result.contains("[EVOLUTION_PROMPT]")) {
                        result = result.replace("[EVOLUTION_PROMPT]", "");

                        Alert evoPrompt = new Alert(Alert.AlertType.CONFIRMATION);
                        evoPrompt.setTitle("Evolution");
                        evoPrompt.setHeaderText(target.getName() + " can now evolve!");
                        evoPrompt.setContentText("Do you want to evolve this Pokémon now?");
                        Optional<ButtonType> confirm = evoPrompt.showAndWait();

                        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                            Pokemon evolvedForm = pokedexManager.getPokemonByNumber(target.getEvolvesTo());
                            if (evolvedForm != null) {
                                target.setName(evolvedForm.getName());
                                target.setPokedexNumber(evolvedForm.getPokedexNumber());
                                target.setEvolvesTo(evolvedForm.getEvolvesTo());
                                target.setEvolutionLevel(evolvedForm.getEvolutionLevel());
                                target.setType1(evolvedForm.getType1());
                                target.setType2(evolvedForm.getType2());
                                result += "\n" + target.getName() + " evolved successfully!";
                            } else {
                                result += "\nEvolution data not found.";
                            }
                        } else {
                            result += "\nEvolution cancelled.";
                        }
                    }

                    showAlert("Use Item", result, Alert.AlertType.INFORMATION);
                    return;
                }


                    // Inventory flow
                Map<String, Trainer.BagItem> bag = selectedTrainer.getItemBag();
                if (bag.isEmpty()) {
                    showAlert("Use Item", "You have no items in your inventory.", Alert.AlertType.INFORMATION);
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

                    String itemNameLower = itemName.toLowerCase();

                    if (itemNameLower.equals("rare candy")) {
                        int oldLevel = target.getBaseLevel();
                        int newLevel = oldLevel + 1;
                        target.setBaseLevel(newLevel);

                        target.setHp((int) Math.round(target.getHp() * 1.1));
                        target.setAttack((int) Math.round(target.getAttack() * 1.1));
                        target.setDefense((int) Math.round(target.getDefense() * 1.1));
                        target.setSpeed((int) Math.round(target.getSpeed() * 1.1));

                        log.append(target.getName())
                                .append(" leveled up from ").append(oldLevel).append(" to ").append(newLevel)
                                .append(". Base stats increased by 10%.\n");

                        Integer evoLevel = target.getEvolutionLevel();
                        Integer evolvesTo = target.getEvolvesTo();
                        List<String> allowedEvos = List.of("pikachu", "vulpix", "growlithe", "togetic", "eevee");

                        if (evoLevel != null &&
                                newLevel >= evoLevel &&
                                evolvesTo != null &&
                                allowedEvos.contains(target.getName().toLowerCase())) {
                            log.append(target.getName())
                                    .append(" can now evolve (Evolution Level: ").append(evoLevel).append(").\n");

                            Alert evoPrompt = new Alert(Alert.AlertType.CONFIRMATION);
                            evoPrompt.setTitle("Evolution");
                            evoPrompt.setHeaderText(target.getName() + " reached level " + newLevel + "!");
                            evoPrompt.setContentText("Do you want to evolve this Pokémon now?");
                            Optional<ButtonType> result = evoPrompt.showAndWait();

                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                Pokemon evolvedForm = pokedexManager.getPokemonByNumber(evolvesTo);
                                if (evolvedForm != null) {
                                    log.append(target.getName())
                                            .append(" evolved into ").append(evolvedForm.getName()).append("!\n");

                                    target.setName(evolvedForm.getName());
                                    target.setPokedexNumber(evolvedForm.getPokedexNumber());
                                    target.setEvolvesTo(evolvedForm.getEvolvesTo());
                                    target.setEvolutionLevel(evolvedForm.getEvolutionLevel());
                                    target.setType1(evolvedForm.getType1());
                                    target.setType2(evolvedForm.getType2());
                                } else {
                                    log.append("Evolution failed. Evolved form not found in Pokédex.\n");
                                }
                            } else {
                                log.append("Pokémon was not evolved.\n");
                            }
                        }

                        log.insert(0, selectedTrainer.getName() + " used Rare Candy on " + target.getName() + ".\n");

                    } else if (bagItem.getItem().getCategory().equalsIgnoreCase("Evolution Stone")) {
                        String stoneUsed = bagItem.getItem().getName();
                        String targetName = target.getName().toLowerCase();
                        List<String> allowedEvos = List.of("pikachu", "vulpix", "growlithe", "togetic", "eevee");

                        if (!allowedEvos.contains(targetName)) {
                            log.append(target.getName()).append(" cannot evolve using an evolution stone.");
                        } else if (!pokedexManager.isCorrectStoneForEvolution(target.getName(), stoneUsed)) {
                            log.append(stoneUsed).append(" cannot be used to evolve ").append(target.getName()).append(".");
                        } else {
                            Integer evolvesTo = target.getEvolvesTo();
                            if (evolvesTo == null) {
                                log.append(target.getName()).append(" has no evolution target.");
                            } else {
                                Pokemon evolvedForm = pokedexManager.getPokemonByNumber(evolvesTo);
                                if (evolvedForm == null) {
                                    log.append("Evolution data not found for ").append(target.getName()).append(".");
                                } else {
                                    String originalName = target.getName();
                                    target.setName(evolvedForm.getName());
                                    target.setPokedexNumber(evolvedForm.getPokedexNumber());
                                    target.setType1(evolvedForm.getType1());
                                    target.setType2(evolvedForm.getType2());
                                    target.setHp(Math.max(target.getHp(), evolvedForm.getHp()));
                                    target.setAttack(Math.max(target.getAttack(), evolvedForm.getAttack()));
                                    target.setDefense(Math.max(target.getDefense(), evolvedForm.getDefense()));
                                    target.setSpeed(Math.max(target.getSpeed(), evolvedForm.getSpeed()));
                                    target.setEvolvesTo(evolvedForm.getEvolvesTo());
                                    target.setEvolutionLevel(evolvedForm.getEvolutionLevel());

                                    log.append(originalName).append(" evolved into ")
                                            .append(evolvedForm.getName()).append("!\n")
                                            .append("Evolution complete. Stats updated.");
                                }
                            }
                        }

                    } else if (bagItem.getItem().getCategory().equalsIgnoreCase("Vitamin") ||
                                bagItem.getItem().getCategory().equalsIgnoreCase("Feather")) {
                            selectedTrainer.applyVitaminEffect(bagItem.getItem(), target);
                            log.append(selectedTrainer.getName()).append(" used ")
                                    .append(itemName).append(" on ")
                                    .append(target.getName()).append(". The item took effect.");
                        } else {
                            log.append(selectedTrainer.getName()).append(" used ")
                                    .append(itemName).append(" on ")
                                    .append(target.getName()).append(".\n")
                                    .append("But it had no effect.");
                        }


                    // Decrement inventory
                    bagItem.decrement();
                    if (bagItem.getQuantity() == 0) {
                        bag.remove(itemName);
                    }

                    showAlert("Use Item", log.toString(), Alert.AlertType.INFORMATION);
                });
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
            Pokemon original = pokedexManager.getPokemonByName(pokemonName);
            if (original == null) {
                showAlert("Add Pokémon", "Pokémon not found in Pokédex.", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Use a cloned copy instead of the shared instance
            Pokemon p = original.clone();

            boolean alreadyInLineup = selectedTrainer.getLineup().stream()
                    .anyMatch(existing -> existing.getName().equalsIgnoreCase(p.getName()));

            if (alreadyInLineup) {
                showAlert("Add Pokémon", p.getName() + " is already in your lineup.", Alert.AlertType.WARNING);
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

                // ✅ Decrement quantity
                Trainer.BagItem bagItem = bag.get(itemName);
                if (bagItem != null) {
                    bagItem.decrement();
                    if (bagItem.getQuantity() <= 0) {
                        bag.remove(itemName);
                    }
                }

                showAlert("Give Item", selectedPokemon.getName() + " is now holding " + item.getName() + ".", Alert.AlertType.INFORMATION);

                JsonManager.saveTrainers(trainerManager.getAllTrainers());
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

            // ✅ SYNC AND SAVE
            Pokemon global = pokedexManager.getPokemonByNumber(selectedPokemon.getPokedexNumber());
            if (global != null) {
                global.setHeldItem(null);
            }
            JsonManager.saveTrainers(trainerManager.getAllTrainers());
            JsonManager.savePokemons(pokedexManager.getAllPokemon());
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
        List<String> options = List.of("Lineup", "Storage");

        ChoiceDialog<String> sourceDialog = new ChoiceDialog<>(options.get(0), options);
        sourceDialog.setTitle("Release Pokémon");
        sourceDialog.setHeaderText("Select a source");
        sourceDialog.setContentText("Release from:");

        Optional<String> sourceChoice = sourceDialog.showAndWait();
        if (sourceChoice.isEmpty()) return;

        String source = sourceChoice.get();
        List<Pokemon> targetList = source.equals("Lineup") ? selectedTrainer.getLineup() : selectedTrainer.getStorage();

        if (targetList.isEmpty()) {
            showAlert("Release Pokémon", "There are no Pokémon in your " + source.toLowerCase() + ".", Alert.AlertType.WARNING);
            return;
        }

        List<String> pokeNames = targetList.stream().map(Pokemon::getName).toList();

        ChoiceDialog<String> pokeDialog = new ChoiceDialog<>(pokeNames.get(0), pokeNames);
        pokeDialog.setTitle("Release Pokémon");
        pokeDialog.setHeaderText("Select a Pokémon to release from " + source);
        pokeDialog.setContentText("Choose Pokémon:");

        Optional<String> selectedPoke = pokeDialog.showAndWait();
        if (selectedPoke.isEmpty()) return;

        String nameToRelease = selectedPoke.get();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Release");
        confirmAlert.setHeaderText("Are you sure you want to release " + nameToRelease + "?");
        confirmAlert.setContentText("This cannot be undone.");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            int before = targetList.size();
            selectedTrainer.releasePokemon(nameToRelease);
            int after = targetList.size();

            if (after < before) {
                // ✅ Save trainer updates to file
                JsonManager.saveTrainers(trainerManager.getAllTrainers());
                showAlert("Released", nameToRelease + " has been released from your " + source.toLowerCase() + ".", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", nameToRelease + " was not found or could not be released.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Canceled", "Release canceled.", Alert.AlertType.INFORMATION);
        }
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
        if (result.equals("PROMPT_FORGET")) {
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
            showAlert("Result", result, Alert.AlertType.INFORMATION);
        }

        JsonManager.saveTrainers(trainerManager.getAllTrainers());
    }


    private void showScrollableAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(content);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(600);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportWidth(500);
        scrollPane.setPrefViewportHeight(600);

        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(550, 650);

        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}


