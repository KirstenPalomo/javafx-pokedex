package pokedex.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pokedex.managers.ItemManager;
import pokedex.models.Item;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;


import java.net.URL;
import java.util.ResourceBundle;

public class ViewItemController implements Initializable {

    @FXML private TableView<Item> itemTableView;
    @FXML private TableColumn<Item, String> colName;
    @FXML private TableColumn<Item, String> colCategory;
    @FXML private TableColumn<Item, String> colDescription;
    @FXML private TableColumn<Item, String> colEffects;
    @FXML private TableColumn<Item, String> colBuy;
    @FXML private TableColumn<Item, String> colSell;

    private final ItemManager itemManager = new ItemManager();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set cell value factories
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colEffects.setCellValueFactory(new PropertyValueFactory<>("effects"));
        colBuy.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        colSell.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        // Add tooltip for Description column
        colDescription.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setTooltip(item == null || empty ? null : new Tooltip(item));
            }
        });

        // Add tooltip for Buying Price column
        colBuy.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setTooltip(item == null || empty ? null : new Tooltip(item));
            }
        });

        // Load data into the table
        ObservableList<Item> data = FXCollections.observableArrayList(itemManager.getAllItems());
        itemTableView.setItems(data);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ItemMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // OK for now
        }
    }

}