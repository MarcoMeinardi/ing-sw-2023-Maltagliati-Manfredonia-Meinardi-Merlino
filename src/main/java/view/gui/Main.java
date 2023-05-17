package view.gui;

import javafx.application.Application;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    private URL getResource(String name){
        URL resource = getClass().getResource(name);
        if(resource == null){
            throw new IllegalArgumentException("Resource not found");
        }
        return resource;
    }

    private boolean btnPlay1;

    @Override
    public void start(Stage stage){
        try {
            Parent root= FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene sceneLogin = new Scene(root);
            stage.setTitle("MyShelfie!");
            stage.setResizable(true);
            stage.setFullScreen(true);
            stage.setMaximized(true);
            stage.setScene(sceneLogin);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}