package pokedex.controllers;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

import java.util.Objects;

public class PokemonMenuController {

    @FXML
    private void handleAddPokemon(ActionEvent event) {
        System.out.println("[DEBUG] handleAddPokemon() triggered");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddPokemon.fxml"));
            System.out.println("[DEBUG] FXMLLoader created");

            Parent addPokemonRoot = loader.load();
            System.out.println("[DEBUG] FXML loaded successfully");

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(addPokemonRoot));
            stage.show();
            System.out.println("[DEBUG] Scene set and shown");
        } catch (IOException e) {
            System.out.println("[ERROR] IOException occurred");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[ERROR] Unexpected exception occurred");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAll(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewPokemon.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchPokemon(ActionEvent event) {
        System.out.println("Search Pokémon button clicked");
        // TODO: Load search screen
    }

    @FXML
    private void handleBack(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/MainMenu.fxml")));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}