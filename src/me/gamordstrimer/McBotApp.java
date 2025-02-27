package me.gamordstrimer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class McBotApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("app/ui/McBotAppUI.fxml"));
            Scene scene = new Scene(root);
            // scene.getStylesheets().add(getClass().getResource("app/ui/McBotAppUI.css").toExternalForm());
            String css = this.getClass().getResource("app/ui/McBotAppUI.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle("MINECRAFT BOT APPLICATION");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        Platform.exit();
        System.exit(0);
        super.stop();
    }

    public static void main(String[] args) {

        // new McBotAppUI();
        launch(args);
    }
}
