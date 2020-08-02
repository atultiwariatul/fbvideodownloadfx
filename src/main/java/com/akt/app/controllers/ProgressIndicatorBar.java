package com.akt.app.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
//https://gist.github.com/jewelsea/4391775
//@DefaultProperty(value = "extension")
public class ProgressIndicatorBar extends StackPane {
         private ReadOnlyDoubleProperty workDone;
         private double totalWork;

         private ProgressBar bar  = new ProgressBar();
         private Text text = new Text();
         private String labelFormatSpecifier;
         private static int DEFAULT_LABEL_PADDING = 5;

    public ProgressIndicatorBar() {
        this.createResettableProgressIndicatorBar();
    }

    public ProgressIndicatorBar(final ReadOnlyDoubleProperty workDone, final double totalWork, final String labelFormatSpecifier) {
            this.workDone  = workDone;
            this.totalWork = totalWork;
            this.labelFormatSpecifier = labelFormatSpecifier;
            syncProgress();
            workDone.addListener((observableValue, number, number2) -> syncProgress());
            bar.setMaxWidth(Double.MAX_VALUE); // allows the progress bar to expand to fill available horizontal space.
            getChildren().setAll(bar, text);
        }

        // synchronizes the progress indicated with the work done.
        private void syncProgress() {
            if (workDone == null || totalWork == 0) {
                text.setText("");
                bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            } else {
                text.setText(String.format(labelFormatSpecifier, Math.ceil(workDone.get())));
                bar.setProgress(workDone.get() / totalWork);
            }

            bar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
            bar.setMinWidth (text.getBoundsInLocal().getWidth()  + DEFAULT_LABEL_PADDING * 2);
        }
        public VBox createResettableProgressIndicatorBar() {
            final int TOTAL_WORK = 18;
            final String WORK_DONE_LABEL_FORMAT = "%.0f";
            final ReadOnlyDoubleWrapper workDone  = new ReadOnlyDoubleWrapper();

            final ProgressIndicatorBar bar = new ProgressIndicatorBar(
                    workDone.getReadOnlyProperty(),
                    TOTAL_WORK,
                    WORK_DONE_LABEL_FORMAT
            );

            final Timeline countDown = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(workDone, TOTAL_WORK)),
                    new KeyFrame(Duration.seconds(10), new KeyValue(workDone, 0))
            );
            countDown.play();
            final Button resetButton = new Button("Reset");
            resetButton.setOnAction(actionEvent -> countDown.playFromStart());
//            stage.getScene().
            final VBox layout = new VBox(20);
            layout.setAlignment(Pos.CENTER);
            layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 20px;");
            layout.getChildren().addAll(bar, resetButton);
            return layout;
        }
    }
