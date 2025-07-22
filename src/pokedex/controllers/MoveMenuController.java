package pokedex.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MoveMenuController {

    @FXML
    private void handleAddMove(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddMove.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAll(ActionEvent event) {
        System.out.println("View All Moves clicked");
        // Example navigation:
        // loadFXML(event, "/fxml/ViewAllMoves.fxml");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        System.out.println("Search Move clicked");
        // Example navigation:
        // loadFXML(event, "/fxml/SearchMove.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        loadFXML(event, "/fxml/MainMenu.fxml");
    }

    private void loadFXML(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}