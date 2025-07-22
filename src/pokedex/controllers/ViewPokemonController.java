package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pokedex.models.Pokemon;
import pokedex.managers.PokedexManager;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        ObservableList<Pokemon> allPokemon = FXCollections.observableArrayList(
                PokedexManager.getInstance().getAllPokemon()
        );
        listViewPokemon.setItems(allPokemon);

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

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PokemonMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}