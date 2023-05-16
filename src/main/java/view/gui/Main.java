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

    @Override
    public void start(Stage stage){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getResource("/fxml/login.fxml"));
            Scene sceneLogin = new Scene(fxmlLoader.load());
            stage.setTitle("MyShelfie!");
            //stage.setWidth(1280d);
            //stage.setHeight(700d);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setFullScreen(true);
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