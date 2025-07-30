/**
 * This controller manages the start screen of the Pokedex application.
 * It initializes font assets and transitions to the main menu upon clicking Start.
 *
 * Uses FXMLLoader with a custom controller factory to inject shared singleton managers.
 * This allows state continuity throughout the application.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */

package pokedex.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Node;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the Pokedex start screen.
 * Handles UI font initialization and start button navigation logic.
 */
public class StartScreenController implements Initializable {

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller with required manager dependencies.
     *
     * @param pokedexManager the Pokedex manager
     * @param moveManager the move manager
     * @param itemManager the item manager
     * @param trainerManager the trainer manager
     */
    public StartScreenController(PokedexManager pokedexManager, MoveManager moveManager,
                                 ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Initializes the start screen by loading the custom pixel font.
     *
     * @param location unused
     * @param resources unused
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getResourceAsStream("/assets/PressStart2P-Regular.ttf"), 12);
    }

    /**
     * Event handler for the Start button.
     * Loads the main menu screen and injects the manager dependencies.
     *
     * @param event JavaFX ActionEvent triggered by button click
     * @throws IOException if FXML loading fails
     */
    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        // 👉 FXMLLoader with controller factory to pass dependencies
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        loader.setControllerFactory(param -> new MainMenuController(
                pokedexManager, moveManager, itemManager, trainerManager
        ));

        Parent mainMenuRoot = loader.load();
        Scene mainMenuScene = new Scene(mainMenuRoot);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(mainMenuScene);
        stage.show();
    }
}
