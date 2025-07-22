package pokedex.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemMenuController {

    @FXML
    private Button viewBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private Button backBtn;

    @FXML
    private void handleView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewItem.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        System.out.println("🔍 Search Items clicked!");
        // TODO: Navigate to item search screen or open search dialog
    }

    @FXML
    private void handleBack() {
        System.out.println("🔙 Back button clicked!");
        // TODO: Navigate back to main menu or previous scene
    }
}
