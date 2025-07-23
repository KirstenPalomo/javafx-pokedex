package pokedex.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import pokedex.managers.ItemManager;
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Trainer;

import java.io.IOException;
import java.util.List;

public class ViewTrainerController {

    @FXML private ListView<String> trainerListView;
    @FXML private Label birthdateLabel;
    @FXML private Label sexLabel;
    @FXML private Label hometownLabel;
    @FXML private TextArea descriptionArea;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    public ViewTrainerController(PokedexManager pokedexManager, MoveManager moveManager,
                                 ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    @FXML
    private void initialize() {
        List<Trainer> allTrainers = trainerManager.getAllTrainers();
        ObservableList<String> trainerNames = FXCollections.observableArrayList();

        for (Trainer t : allTrainers) {
            trainerNames.add(t.getName()); // or t.getTrainerID() if preferred
        }

        trainerListView.setItems(trainerNames);

        trainerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Trainer selected = trainerManager.getTrainerWithName(newVal); // or use ID
                if (selected != null) {
                    birthdateLabel.setText("Birthdate: " + selected.getBirthdate());
                    sexLabel.setText("Sex: " + selected.getSex());
                    hometownLabel.setText("Hometown: " + selected.getHometown());
                    descriptionArea.setText(selected.getDescription());

                }
            }
        });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TrainerMenu.fxml"));
            loader.setControllerFactory(param -> new TrainerMenuController(
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
