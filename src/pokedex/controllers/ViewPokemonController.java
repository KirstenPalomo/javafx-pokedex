
package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pokedex.models.Pokemon;
import pokedex.managers.PokedexManager;
import pokedex.managers.MoveManager;
import pokedex.managers.ItemManager;
import pokedex.managers.TrainerManager;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * Controller for the "View Pokemon" screen in the Pokedex GUI.
 * Displays a list of all registered Pokemon and shows detailed information
 * (e.g., types, stats, evolution, moves, held item) for the selected Pokemon.
 * Also includes a back button to return to the Pokemon Menu screen.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
public class ViewPokemonController implements Initializable {

    @FXML private ListView<Pokemon> listViewPokemon;

    @FXML private Label labelPokedexNumber;
    @FXML private Label labelType;
    @FXML private Label labelLevel;
    @FXML private Label labelEvolvesFrom;
    @FXML private Label labelEvolvesTo;
    @FXML private Label labelHp;
    @FXML private Label labelAtk;
    @FXML private Label labelDef;
    @FXML private Label labelSpd;
    @FXML private Label labelMoves;
    @FXML private Label labelHeldItem;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller and stores references to manager instances.
     */
    public ViewPokemonController(PokedexManager pokedexManager, MoveManager moveManager,
                                 ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Initializes the ListView of Pokemon and sets up a listener to display
     * details when a Pokemon is selected.
     *
     * @param location  Location of the FXML file (not used)
     * @param resources Resource bundle (not used)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Clear initial text
        labelPokedexNumber.setText("");
        labelType.setText("");
        labelLevel.setText("");
        labelEvolvesFrom.setText("");
        labelEvolvesTo.setText("");
        labelHp.setText("");
        labelAtk.setText("");
        labelDef.setText("");
        labelSpd.setText("");
        labelMoves.setText("");
        labelHeldItem.setText("");

        // Load Pokemon list
        ObservableList<Pokemon> allPokemon = FXCollections.observableArrayList(
                pokedexManager.getAllPokemon() // ← now uses the instance from constructor
        );
        listViewPokemon.setItems(allPokemon);

        // Display Pokemon details on selection
        listViewPokemon.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                labelPokedexNumber.setText(String.format("#%03d – %s", selected.getPokedexNumber(), selected.getName()));

                String type = selected.getType1();
                if (selected.getType2() != null && !selected.getType2().isBlank()) {
                    type += ", " + selected.getType2();
                }
                labelType.setText("Type: " + type);
                labelLevel.setText("Level: " + selected.getBaseLevel());

                labelEvolvesFrom.setText("Evolves From: " +
                        (selected.getEvolvesFrom() != null
                                ? "#" + String.format("%03d", selected.getEvolvesFrom())
                                : "None")
                );

                labelEvolvesTo.setText("Evolves To: " +
                        (selected.getEvolvesTo() != null
                                ? "#" + String.format("%03d", selected.getEvolvesTo()) + " (Level " + selected.getEvolutionLevel() + ")"
                                : "None")
                );

                labelHp.setText("HP: " + selected.getHp());
                labelAtk.setText("ATK: " + selected.getAttack());
                labelDef.setText("DEF: " + selected.getDefense());
                labelSpd.setText("SPD: " + selected.getSpeed());

                labelMoves.setText("Moves: " +
                        (selected.getMoveSet().isEmpty()
                                ? "None"
                                : String.join(", ", selected.getMoveSet()))
                );

                labelHeldItem.setText("Held Item: " +
                        (selected.getHeldItem() != null
                                ? selected.getHeldItem().getName()
                                : "None")
                );
            }
        });
    }

    /**
     * Navigates back to the Pokemon Menu screen.
     *
     * @param event Action event triggered by the "Back" button
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PokemonMenu.fxml"));
            loader.setControllerFactory(param -> new PokemonMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}