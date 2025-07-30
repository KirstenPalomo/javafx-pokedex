/**
 * Entry point for the Pokedex JavaFX application.
 * This class initializes the application by loading saved data,
 * preparing the necessary managers, and displaying the Start Screen.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */

package pokedex.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pokedex.JsonManager;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Item;
import pokedex.models.Move;
import pokedex.models.Pokemon;
import pokedex.models.Trainer;
import pokedex.controllers.StartScreenController;

import java.util.List;
import java.util.Objects;

/**
 * Main class that launches the Pokedex JavaFX application.
 * It sets up the scene, loads data from JSON, and displays the Start Screen.
 */
public class Main extends Application {
    /**
     * Starts the JavaFX application by initializing managers,
     * loading saved data, and displaying the Start Screen.
     *
     * @param stage the primary stage for this application
     * @throws Exception if FXML or resource files are not found
     */
    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(Objects.requireNonNull(getClass().getResourceAsStream("/assets/PressStart2P-Regular.ttf")), 12);

        // Instantiate managers
        PokedexManager pokedexManager = PokedexManager.getInstance();
        MoveManager moveManager = MoveManager.getInstance();
        ItemManager itemManager = ItemManager.getInstance();
        TrainerManager trainerManager = new TrainerManager();

        // Load from JSON
        List<Trainer> savedTrainers = JsonManager.loadTrainers();
        if (savedTrainers != null) trainerManager.setTrainers(savedTrainers);

        List<Pokemon> savedPokemons = JsonManager.loadPokemons();
        if (savedPokemons != null) pokedexManager.setAllPokemon(savedPokemons);

        List<Move> savedMoves = JsonManager.loadMoves();
        if (savedMoves != null) moveManager.setAllMoves(savedMoves);

        List<Item> savedItems = JsonManager.loadItems();
        if (savedItems != null && !savedItems.isEmpty()) {
            itemManager.setAllItems(savedItems);
        }


        // Load the Start Screen with controller injection
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StartScreen.fxml"));
        loader.setControllerFactory(param -> new StartScreenController(
                pokedexManager, moveManager, itemManager, trainerManager
        ));

        Parent root = loader.load();

        // Attach stylesheet and show the stage
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        stage.setTitle("Pokedex App");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}