package com.example.javafx;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private Button login_button;
    @FXML
    private Button btnError;
    @FXML
    private TextField txtName;
    @FXML
    void login_action(ActionEvent event) {
        String name = txtName.getText();
    }

    @FXML
    void btnError(ActionEvent event) {
        Stage window = (Stage) btnError.getScene().getWindow();
        //window.setScene("login.fxml");
    }

}
