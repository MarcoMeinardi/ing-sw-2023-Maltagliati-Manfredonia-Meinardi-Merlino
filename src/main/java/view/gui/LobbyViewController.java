package view.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class LobbyViewController implements Initializable {

    @FXML
    private Text nameUser;

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        if(SceneController.username != null){
            nameUser.setText(SceneController.username);
        }
    }
}
