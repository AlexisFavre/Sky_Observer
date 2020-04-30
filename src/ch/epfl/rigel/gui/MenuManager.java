package ch.epfl.rigel.gui;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

public class MenuManager {

    private GridPane menuPane;
    private TextField starSearchBar;

    public MenuManager(ViewingParametersBean vpb, SkyCanvasManager manager) {
        menuPane = new GridPane();
        starSearchBar = new TextField();

        starSearchBar.setPromptText("Enter a star");
        GridPane.setColumnSpan(starSearchBar, 5);
        GridPane.setConstraints(starSearchBar, 0, 0);
        menuPane.getChildren().add(starSearchBar);


        menuPane.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                    String destination = starSearchBar.getText();
                    System.out.println(destination);
                    manager.goToDestinationWithName(destination);
            }
            event.consume();
        });
    }

    public GridPane menuPane() {
        return menuPane;
    }
}
