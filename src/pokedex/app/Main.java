package pokedex.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 🔹 Load custom font BEFORE loading the FXML
        Font.loadFont(Objects.requireNonNull(getClass().getResourceAsStream("/assets/PressStart2P-Regular.ttf")), 12);

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/StartScreen.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        stage.setTitle("Pokedex App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}