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

    public ViewMoveController(PokedexManager pokedexManager, MoveManager moveManager,
                              ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelName.setText("");
        labelClassification.setText("");
        labelType.setText("");
        labelDescription.setText("");

        ObservableList<Move> allMoves = FXCollections.observableArrayList(moveManager.getAllMoves());
        listViewMove.setItems(allMoves);

        listViewMove.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Move move, boolean empty) {
                super.updateItem(move, empty);
                setText((empty || move == null) ? null : move.getName());
            }
        });

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