package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import pokedex.JsonManager;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;

import java.io.IOException;

public class MainMenuController {

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    public MainMenuController(PokedexManager pokedexManager, MoveManager moveManager,
                              ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML private Button pokemonBtn;
    @FXML private Button movesBtn;
    @FXML private Button itemsBtn;
    @FXML private Button trainerBtn;
    @FXML private Button exitBtn;

    private void saveAllData() {
        JsonManager.savePokemons(pokedexManager.getAllPokemon());
        JsonManager.saveMoves(moveManager.getAllMoves());
        JsonManager.saveTrainers(trainerManager.getAllTrainers());
    }


    @FXML
    private void goToPokemon(ActionEvent event) {
        saveAllData(); // 🔁 Save before switching
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PokemonMenu.fxml"));
            loader.setControllerFactory(param -> new PokemonMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            System.out.println("Loading FXML from: " + getClass().getResource("/fxml/PokemonMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goToMoves(ActionEvent event) {
        saveAllData();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MoveMenu.fxml"));
            loader.setControllerFactory(param -> new MoveMenuController(
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
    private void goToItems(ActionEvent event) {
        saveAllData();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ItemMenu.fxml"));
            loader.setControllerFactory(param -> new ItemMenuController(
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
    private void goToTrainer(ActionEvent event) {
        System.out.println("Navigate to Trainer screen");
        // TODO: Add TrainerMenuController and scene switch logic here when ready
    }

    @FXML
    private void exitApp(ActionEvent event) {
        saveAllData(); // Save before quitting
        System.exit(0);
    }
}