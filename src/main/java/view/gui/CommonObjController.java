package view.gui;

import javafx.fxml.Initializable;
import model.Shelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CommonObjController implements Initializable {
    private ArrayList<Shelf> shelves;
    public CommonObjController() {
        this.shelves = GameViewController.getShelves();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO Create the scene showing all the shelves
    }


}
