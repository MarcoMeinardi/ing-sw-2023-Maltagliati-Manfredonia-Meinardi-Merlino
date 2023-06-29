package view.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Class that manages the initialization of the GUI
 */
public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * Get the resources
     */
    private URL getResource(String name){
        URL resource = getClass().getResource(name);
        if(resource == null){
            throw new IllegalArgumentException("Resource not found");
        }
        return resource;
    }

    private boolean btnPlay1;

    /**
     * JavaFX method override to start the GUI
     */
    @Override
    public void start(Stage stage){
        try {
            Parent root= FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            int width = 1140;
            int height = 760;
            Scene sceneLogin = new Scene(root, width, height);
            stage.setTitle("MyShelfie!");
            stage.setResizable(false);
            stage.setScene(sceneLogin);
            stage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * GUI entry point
     */
    public static void main(String[] args) {
        launch();
    }

}
