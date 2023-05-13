package com.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene sceneLogin = new Scene(fxmlLoader.load());
        stage.setTitle("MyShelfie!");
        stage.setFullScreen(true);
        stage.setScene(sceneLogin);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}