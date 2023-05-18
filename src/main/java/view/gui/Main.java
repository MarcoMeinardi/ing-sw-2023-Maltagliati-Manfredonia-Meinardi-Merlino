package view.gui;

import javafx.application.Application;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
            int width = 1140;
            int height = 760;
            Scene sceneLogin = new Scene(root, width, height);
            stage.setTitle("MyShelfie!");
            stage.setResizable(false);
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