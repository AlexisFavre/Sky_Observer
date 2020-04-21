package ch.epfl.rigel;

import ch.epfl.rigel.gui.DateTimeBean;
import ch.epfl.rigel.gui.NamedTimeAccelerator;
import ch.epfl.rigel.gui.TimeAccelerator;
import ch.epfl.rigel.gui.TimeAnimator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.time.ZonedDateTime;

public final class UseTimeAnimator extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        ZonedDateTime simulatedStart =
                ZonedDateTime.parse("2020-06-01T23:55:00+01:00");
        TimeAccelerator accelerator =
                NamedTimeAccelerator.SIDEREAL_DAY.getAccelerator();

        DateTimeBean dateTimeB = new DateTimeBean();
        dateTimeB.setZonedDateTime(simulatedStart);

        TimeAnimator timeAnimator = new TimeAnimator(dateTimeB);
        timeAnimator.setAccelerator(accelerator);

        dateTimeB.dateProperty().addListener((p, o, n) -> {
            System.out.printf(" Nouvelle date : %s%n", n);
            Platform.exit();
            System.out.println(timeAnimator.runningProperty().getValue());
            //timeAnimator.stop();
        });
        dateTimeB.timeProperty().addListener((p, o, n) -> {
            System.out.printf("Nouvelle heure : %s%n", n);
        });
        timeAnimator.start();
    }
}
