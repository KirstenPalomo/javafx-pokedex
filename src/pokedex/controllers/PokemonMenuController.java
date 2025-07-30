/**
 * Controller for the Pokemon Menu screen in the Pokedex GUI.
 * Handles navigation to Add Pokemon, View All Pokemon, Search Pokemon, and back to the Main Menu.
 * Uses FXML loaders with custom controller injection to maintain manager references across scenes.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;

import java.io.IOException;
import java.util.Objects;

public class PokemonMenuController {

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller with references to core managers.
     *
     * @param pokedexManager Pokedex manager
     * @param moveManager Move manager
     * @param itemManager Item manager
     * @param trainerManager Trainer manager
     */
    public PokemonMenuController(PokedexManager pokedexManager, MoveManager moveManager,
                                 ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Navigates to the Add Pokemon screen.
     *
     * @param event Action event triggered by button click
     */
    @FXML
    private void handleAddPokemon(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddPokemon.fxml"));
            loader.setControllerFactory(param -> new AddPokemonController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the View All Pokemon screen.
     *
     * @param event Action event triggered by button click
     */
    @FXML
    private void handleViewAll(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewPokemon.fxml"));
            loader.setControllerFactory(param -> new ViewPokemonController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the Search Pokemon screen.
     *
     * @param event Action event triggered by button click
     */
    @FXML
    private void handleSearchPokemon(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchPokemon.fxml"));
            loader.setControllerFactory(param -> new SearchPokemonController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the Main Menu screen.
     *
     * @param event Action event triggered by button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            loader.setControllerFactory(param -> new MainMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}