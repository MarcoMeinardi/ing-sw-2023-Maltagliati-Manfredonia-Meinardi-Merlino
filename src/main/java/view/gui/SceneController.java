package view.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class SceneController {

    public static void changeScene(Scene scene, String fxml) {

        try {
            FXMLLoader loader = new FXMLLoader(SceneController.class.getResource(fxml));
            Parent newSceneRoot = loader.load();
            Scene currentScene = scene.getRoot().getScene();
            currentScene.setRoot(newSceneRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
