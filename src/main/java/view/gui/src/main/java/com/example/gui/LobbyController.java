package com.example.gui;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class LobbyController {


    @FXML
    private Text nameUser;


    public void setNameUser(String nameUser) {
        this.nameUser.setText(nameUser);
    }
}
