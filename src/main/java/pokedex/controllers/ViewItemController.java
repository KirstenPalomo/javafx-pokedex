/**
 * This displays all Pokemon items in a table format in the Enhanced Pokedex system.
 * Items are shown with tooltips for easier readability. Columns include name, category, description,
 * effects, buy price, and sell price. Allows users to return to the Item Menu.
 *
 * Authors: Kirsten Palomo, Erylle Galinato
 */
package pokedex.controllers;

// JavaFX UI and controls
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
import pokedex.managers.MoveManager;
import pokedex.managers.PokedexManager;
import pokedex.managers.TrainerManager;
import pokedex.models.Item;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for displaying all Pokemon items in a table.
 * Each item is shown with its name, category, description, effect, and prices.
 * Tooltips are applied to help users view full text on hover.
 * Provides a back button to return to the Item Menu screen.
 */
public class ViewItemController implements Initializable {

    @FXML private TableView<Item> itemTableView;
    @FXML private TableColumn<Item, String> colName;
    @FXML private TableColumn<Item, String> colCategory;
    @FXML private TableColumn<Item, String> colDescription;
    @FXML private TableColumn<Item, String> colEffects;
    @FXML private TableColumn<Item, String> colBuy;
    @FXML private TableColumn<Item, String> colSell;

    private final PokedexManager pokedexManager;
    private final MoveManager moveManager;
    private final ItemManager itemManager;
    private final TrainerManager trainerManager;

    /**
     * Constructs the controller with required manager dependencies.
     *
     * @param pokedexManager the Pokedex manager
     * @param moveManager the move manager
     * @param itemManager the item manager
     * @param trainerManager the trainer manager
     */
    public ViewItemController(PokedexManager pokedexManager, MoveManager moveManager,
                              ItemManager itemManager, TrainerManager trainerManager) {
        this.pokedexManager = pokedexManager;
        this.moveManager = moveManager;
        this.itemManager = itemManager;
        this.trainerManager = trainerManager;
    }

    /**
     * Initializes the table view with item data and applies tooltips to applicable columns.
     *
     * @param location  the location used to resolve relative paths
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set cell value factories
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colEffects.setCellValueFactory(new PropertyValueFactory<>("effects"));
        colBuy.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        colSell.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        // Apply tooltip on hover for all relevant columns
        applyTooltip(colName);
        applyTooltip(colCategory);
        applyTooltip(colDescription);
        applyTooltip(colEffects);
        applyTooltip(colBuy);

        // Load data
        ObservableList<Item> data = FXCollections.observableArrayList(itemManager.getAllItems());
        itemTableView.setItems(data);
    }

    /**
     * Applies tooltip functionality to a table column.
     * Tooltips appear on hover and show full text content of each cell.
     *
     * @param column the column to apply tooltips to
     */
    private void applyTooltip(TableColumn<Item, String> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setTooltip((item == null || empty) ? null : new Tooltip(item));
            }
        });
    }

    /**
     * Handles the Back button click and navigates the user back to the Item Menu screen.
     *
     * @param event the action event triggered by the Back button
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ItemMenu.fxml"));
            loader.setControllerFactory(param -> new ItemMenuController(
                    pokedexManager, moveManager, itemManager, trainerManager
            ));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}