/**
 * This is the JavaFX controller for the Trainer Menu screen in the Enhanced Pokedex system.
 * Handles navigation between Trainer-related interfaces: Add, View All, Search, and return to Main Menu.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.controllers;

// JavaFX components for handling UI actions and transitions
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Manager classes
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;

import java.io.IOException;

/**
 * Controller for the Trainer Menu screen.
 * Handles navigation to Add, View, Search trainers and return to Main Menu.
 */
public class TrainerMenuController {

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the TrainerMenuController with required manager dependencies.
     *
     * @param pokedexManager  manages Pokemon data
     * @param moveManager     manages move data
     * @param itemManager     manages item data
     * @param trainerManager  manages trainer data
     */
    public TrainerMenuController(PokedexManager pokedexManager, MoveManager moveManager,
                                 ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Navigates to the Add Trainer screen.
     *
     * @param event the action event triggered by clicking "Add"
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleAdd(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddTrainer.fxml"));
        loader.setControllerFactory(param -> new AddTrainerController(pokedexManager, moveManager, itemManager, trainerManager));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Navigates to the View All Trainers screen.
     *
     * @param event the action event triggered by clicking "View All"
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleViewAll(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewTrainer.fxml"));
        loader.setControllerFactory(param -> new ViewTrainerController(pokedexManager, moveManager, itemManager, trainerManager));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Navigates to the Search Trainer screen.
     *
     * @param event the action event triggered by clicking "Search"
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleSearch(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchTrainer.fxml"));
        loader.setControllerFactory(param -> new SearchTrainerController(pokedexManager, moveManager, itemManager, trainerManager));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Navigates back to the Main Menu screen.
     *
     * @param event the action event triggered by clicking "Back"
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        loader.setControllerFactory(param -> new MainMenuController(pokedexManager, moveManager, itemManager, trainerManager));
        Parent root = loader.load();
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}