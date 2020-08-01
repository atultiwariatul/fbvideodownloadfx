package com.akt.app.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class SlidingWindow extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Main");
        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        double screenRightEdge = primScreenBounds.getMaxX() ;
        stage.setX(screenRightEdge);
        System.out.println(primScreenBounds.getWidth());
        stage.setY(primScreenBounds.getMinY());
        stage.setWidth(0);
        stage.setHeight(primScreenBounds.getHeight());

        Timeline timeline = new Timeline();
        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return stage.getWidth();
            }
            @Override
            public void setValue(Double value) {
                stage.setX(screenRightEdge - value);
                stage.setWidth(value);
            }
        };

        KeyValue kv = new KeyValue(writableWidth, 600d);
        KeyFrame kf = new KeyFrame(Duration.millis(3000), kv);
        timeline.getKeyFrames().addAll(kf);
        timeline.play();
        stage.show();
        stage.setOnCloseRequest(event -> {
            Timeline timeline1 = new Timeline();
            KeyFrame endFrame = new KeyFrame(Duration.millis(9000), new KeyValue(writableWidth, 0.0));
            timeline1.getKeyFrames().add(endFrame);
            timeline1.setOnFinished(e -> Platform.runLater(() -> stage.hide()));
            timeline1.play();
            event.consume();
        });

    }
}
