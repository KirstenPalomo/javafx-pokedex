/**
 * This controller handles the main menu navigation of the Pokédex application.
 * Users can navigate to submodules (Pokémon, Moves, Items, Trainers) or exit the app.
 * Each button handler saves data before transitioning to the next screen.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */

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

/**
 * Controller for the main menu screen.
 * Provides navigation to other parts of the application:
 * Pokémon, Moves, Items, Trainer Menu, and Exit.
 */
public class MainMenuController {

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller with shared manager references.
     *
     * @param pokedexManager the main Pokedex manager
     * @param moveManager the move manager
     * @param itemManager the item manager
     * @param trainerManager the trainer manager
     */
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

    /**
     * Saves all game data using JsonManager.
     * Called before navigating away or exiting the application.
     */
    private void saveAllData() {
        JsonManager.savePokemons(pokedexManager.getAllPokemon());
        JsonManager.saveMoves(moveManager.getAllMoves());
        JsonManager.saveTrainers(trainerManager.getAllTrainers());
    }

    /**
     * Navigates to the Pokémon menu screen.
     * Saves data first and uses a controller factory to inject dependencies.
     *
     * @param event the button click event
     */
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

    /**
     * Navigates to the Move menu screen.
     * Saves data first and uses a controller factory to inject dependencies.
     *
     * @param event the button click event
     */
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

    /**
     * Navigates to the Item menu screen.
     * Saves data first and uses a controller factory to inject dependencies.
     *
     * @param event the button click event
     */
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

    /**
     * Navigates to the Trainer menu screen.
     * Dependencies are passed via controller factory.
     *
     * @param event the button click event
     * @throws IOException if loading the FXML fails
     */
    @FXML
    private void goToTrainerMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TrainerMenu.fxml"));
        loader.setControllerFactory(param -> new TrainerMenuController(pokedexManager, moveManager, itemManager, trainerManager));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Exits the application after saving all game data.
     *
     * @param event the button click event
     */
    @FXML
    private void exitApp(ActionEvent event) {
        saveAllData(); // Save before quitting
        System.exit(0);
    }
}