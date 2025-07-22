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
import pokedex.models.Move;
import pokedex.models.Pokemon;
import pokedex.models.Trainer;
import pokedex.controllers.StartScreenController;

import java.util.List;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(Objects.requireNonNull(getClass().getResourceAsStream("/assets/PressStart2P-Regular.ttf")), 12);

        // Instantiate managers
        PokedexManager pokedexManager = PokedexManager.getInstance();
        MoveManager moveManager = MoveManager.getInstance();
        ItemManager itemManager = new ItemManager();
        TrainerManager trainerManager = new TrainerManager();

        // Load from JSON
        List<Trainer> savedTrainers = JsonManager.loadTrainers();
        if (savedTrainers != null) trainerManager.setTrainers(savedTrainers);

        List<Pokemon> savedPokemons = JsonManager.loadPokemons();
        if (savedPokemons != null) pokedexManager.setAllPokemon(savedPokemons);

        List<Move> savedMoves = JsonManager.loadMoves();
        if (savedMoves != null) moveManager.setAllMoves(savedMoves);

        // 💡 Use FXMLLoader with controller factory
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StartScreen.fxml"));
        loader.setControllerFactory(param -> new StartScreenController(
                pokedexManager, moveManager, itemManager, trainerManager
        ));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        stage.setTitle("Pokedex App");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}