package ch.epfl.rigel.gui;

import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.script.Bindings;

public class MenuManager {

    private GridPane menuPane;

    public MenuManager(ViewingParametersBean vpb, SkyCanvasManager skyManager) {

        menuPane = new GridPane();
        menuPane.setPadding(new Insets(10, 10, 10, 10));
        menuPane.setVgap(10);
        menuPane.setHgap(10);

        Text title = new Text(" M   E   N   U");
        title.setFont(new Font("cambria", 17));
        title.setFill(Color.LIGHTGOLDENRODYELLOW);

        TextField starSearchBar = new TextField();
        starSearchBar.setMinWidth(92);
        starSearchBar.setPromptText("search a star");
        starSearchBar.setOnAction(event -> {
            String destination = starSearchBar.getText();
            starSearchBar.deleteText(0, starSearchBar.getLength());
            skyManager.goToDestinationWithName(destination);
            skyManager.focusOnCanvas();
        });

        Button rigel = new Button("Rigel");
        rigel.setOnAction(event -> skyManager.goToDestinationWithName("Rigel"));

        Button soleil = new Button("Soleil");
        soleil.setOnAction(event -> skyManager.goToDestinationWithName("Soleil"));

        GridPane.setConstraints(title, 0, 0);
        menuPane.getChildren().add(title);
        GridPane.setConstraints(starSearchBar, 0, 1);
        menuPane.getChildren().add(starSearchBar);
        GridPane.setConstraints(rigel, 0, 2);
        menuPane.getChildren().add(rigel);
        GridPane.setConstraints(soleil, 0, 3);
        menuPane.getChildren().add(soleil);

        menuPane.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.COMMAND)) {
                skyManager.focusOnCanvas();
            }
            event.consume();
        });
    }

    public GridPane menuPane() {
        return menuPane;
    }
}
