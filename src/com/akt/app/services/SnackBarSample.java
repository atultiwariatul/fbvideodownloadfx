package com.akt.app.services;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;

public class SnackBarSample {
    public void someRandomMethod() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass() .getResource("fxml/splash.fxml"));
        Parent root = fxmlLoader.load();
//        controller = fxmlLoader.getController().in;
//        controller.init();
//        primaryStage.setTitle(NAME);
        Scene scene = new Scene(root, 800, 600);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/main.css").toExternalForm());
//        primaryStage.setScene(scene);
//        primaryStage.show();

        JFXSnackbar noti = new JFXSnackbar();
        noti.registerSnackbarContainer((Pane) root);
        noti.getStylesheets().add(getClass().getResource("snackbar.css").toExternalForm());
        noti.toFront();
        noti.enqueue(new JFXSnackbar.SnackbarEvent(
                new JFXSnackbarLayout("Message",
                        "CLOSE", action -> {
                    Timeline closeAnimation = new Timeline(
                            new KeyFrame(
                                    Duration.ZERO,
                                    e -> noti.toFront(),
                                    new KeyValue(noti.opacityProperty(), 1, Interpolator.EASE_IN),
                                    new KeyValue(noti.translateYProperty(), 0, Interpolator.EASE_OUT)
                            ),
                            new KeyFrame(
                                    Duration.millis(290),
                                    new KeyValue(noti.visibleProperty(), true, Interpolator.EASE_BOTH)
                            ),
                            new KeyFrame(Duration.millis(300),
                                    e -> noti.toBack(),
                                    new KeyValue(noti.visibleProperty(), false, Interpolator.EASE_BOTH),
                                    new KeyValue(noti.translateYProperty(),
                                            noti.getLayoutBounds().getHeight(),
                                            Interpolator.EASE_IN),
                                    new KeyValue(noti.opacityProperty(), 0, Interpolator.EASE_OUT)
                            )
                    );
                    closeAnimation.setCycleCount(1);
                    closeAnimation.play();
                }),
                Duration.seconds(3), null));
    }
}
