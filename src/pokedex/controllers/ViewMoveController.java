/**
 * Controller for the "View Move" screen in the Pokédex GUI.
 * Displays a list of all available moves and shows detailed information about the selected move.
 * Integrates with MoveManager to fetch and display data using JavaFX ListView and labels.
 * Also handles navigation back to the Move Menu screen.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.ItemManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Move;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewMoveController implements Initializable {

    @FXML private ListView<Move> listViewMove;
    @FXML private Label labelName;
    @FXML private Label labelClassification;
    @FXML private Label labelType;
    @FXML private Label labelDescription;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller with manager dependencies injected.
     *
     * @param pokedexManager Pokédex manager
     * @param moveManager Move manager
     * @param itemManager Item manager
     * @param trainerManager Trainer manager
     */
    public ViewMoveController(PokedexManager pokedexManager, MoveManager moveManager,
                              ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Initializes the view after FXML elements are loaded.
     * Populates the ListView with all moves and sets up a listener to display details when selected.
     *
     * @param location  The location used to resolve relative paths for the root object, or null
     * @param resources The resources used to localize the root object, or null
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Clear all labels initially
        labelName.setText("");
        labelClassification.setText("");
        labelType.setText("");
        labelDescription.setText("");

        //Populate ListView with all moves
        ObservableList<Move> allMoves = FXCollections.observableArrayList(moveManager.getAllMoves());
        listViewMove.setItems(allMoves);

        //Set how each move is displayed in the list (by name)
        listViewMove.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Move move, boolean empty) {
                super.updateItem(move, empty);
                setText((empty || move == null) ? null : move.getName());
            }
        });

        //Display selected move details in labels
        listViewMove.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                labelName.setText("Name: " + selected.getName());
                labelClassification.setText("Classification: " + selected.getClassification());

                String type = selected.getType1();
                if (selected.getType2() != null && !selected.getType2().isBlank()) {
                    type += ", " + selected.getType2();
                }
                labelType.setText("Type: " + type);

                labelDescription.setText("Description: " + selected.getDescription());
            }
        });
    }

    /**
     * Handles the "Back" button action to return to the Move Menu screen.
     *
     * @param event The button click event
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MoveMenu.fxml"));
            loader.setControllerFactory(param -> new MoveMenuController(
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