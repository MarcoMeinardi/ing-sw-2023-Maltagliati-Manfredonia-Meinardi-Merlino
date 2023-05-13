package com.example.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class LobbyController {


    @FXML
    private Text nameUser;

    @FXML
    private TableView<?> ListLobby;

    @FXML
    private TableColumn<?, ?> nameLobby;

    @FXML
    private TableColumn<?, ?> numPlayers;

    @FXML
    private Button btnCreateLobby;

    @FXML
    private Button btnSelectLobby;

    @FXML
    void createNewLobby(ActionEvent event) {

    }

    @FXML
    void selectedLobby(ActionEvent event) {

    }

    public void setNameUser(String nameUser) {
        this.nameUser.setText(nameUser);
    }
}
