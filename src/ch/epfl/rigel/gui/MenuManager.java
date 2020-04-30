package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.scene.control.TextField;
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
            switch (event.getCode()) {
                case ENTER:
                    String destination = starSearchBar.getText();
                    System.out.println(destination);
                    manager.goToDestinationWithName(destination);
                    break;
                default:
                    break;
            }
            event.consume();
        });
    }

    public GridPane menuPane() {
        return menuPane;
    }
}
