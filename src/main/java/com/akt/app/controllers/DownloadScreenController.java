package com.akt.app.controllers;

import com.akt.app.model.DownloadDetails;
import com.akt.app.services.DownloadLinkProvider;
import com.akt.app.services.DownloadService;
import com.akt.app.tasks.CalculateDownloadSizeTask;
import com.akt.app.tasks.VideoDownloadTask;
import com.akt.app.utils.Utils;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import static com.akt.app.utils.Utils.VIDEOS_DIRECTORY;

public class DownloadScreenController implements Initializable {
    public TextField downloadDir;
    public TextField fbDownloadLink;
    public Button downloadButton;
    public Label directoryChosen;
    public ProgressBar progressBar;
    public Label statusLabel;
    public Button cancelButton;
    public Button playButton;
    public JFXSnackbar snackBar;
    public AnchorPane downloadAnchorPane;
    public AnchorPane anchorPaneRef;
    public JFXCheckBox directLink;
    private boolean[] direct={false};

    private DownloadDetails downloadDetails = new DownloadDetails();

    private DownloadService downloadService;

    public void initializeManager(final DownloadService service) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("User's Videos Directory Default Path:" + VIDEOS_DIRECTORY);
        anchorPaneRef = (AnchorPane) downloadButton.getParent();
        snackBar = new JFXSnackbar(anchorPaneRef);
        downloadButton.setOnAction(downloadButtonClickedEvent());
        directLink.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("New value:"+newValue);
            direct[0] = newValue;
            System.out.println("isChecked:"+direct[0]);
//            directLink.setSelected(!newValue);
        });

        fbDownloadLink.focusedProperty().addListener(event -> {
            statusLabel.setVisible(true);
            System.out.println("Event Text:" + fbDownloadLink.getText());
            if (!fbDownloadLink.getText().contains("https") && !fbDownloadLink.getText().contains("facebook")) {
                fbDownloadLink.requestFocus();
                statusLabel.setText("Please enter valid Facebook video URL");
//                fireSnackBarEvent("Please enter valid Facebook video URL","Hide");
                statusLabel.setTranslateY(2);
            } else {
                statusLabel.setText("Download Link validated");
            }
        });
        fireSnackBarEvent("Application is ready to use","Hide");
    }

    private void fireSnackBarEvent(String message, String actionText){
        System.out.println("Going to fire a Snackbar Event");
        JFXSnackbarLayout snackbarLayout = new JFXSnackbarLayout(message, actionText, (event) -> {
            if (event != null) {
                snackBar.close();
            }
        });
        JFXSnackbar.SnackbarEvent event = new JFXSnackbar.SnackbarEvent(snackbarLayout, new Duration(5000));
        snackBar.enqueue(event);
    }

    private void calculateDownloadSize(ProgressBar progressBar, Label statusLabel) {
        statusLabel.setVisible(true);
        statusLabel.setText("Calculating Size...");
        System.out.println("In Calculate Download Size:" + this.downloadDetails.getDownloadDir());
        CalculateDownloadSizeTask calculateDownloadSizeTask = new CalculateDownloadSizeTask(downloadDetails);
        progressBar.progressProperty().bind(calculateDownloadSizeTask.progressProperty());
        Thread thread = new Thread(calculateDownloadSizeTask);
        thread.setDaemon(true);
        thread.start();
        calculateDownloadSizeTask.setOnSucceeded(event -> {
            try {
                this.downloadDetails = calculateDownloadSizeTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (!this.downloadDetails.isExists()) {
                downloadVideo(progressBar, statusLabel);
            } else {
                fireSnackBarEvent("YAY! You have already downloaded this file","Hide");
                statusLabel.setText("YAY! You have already downloaded this file.");
                statusLabel.setTextFill(Color.RED);
            }
        });
    }

    private void downloadVideo(ProgressBar progressBar, Label statusLabel) {
        statusLabel.setVisible(true);
        statusLabel.setText("Starting Download...");
        VideoDownloadTask videoDownloadTask = new VideoDownloadTask(downloadDetails);
        statusLabel.setText("Download Started...");
        fireSnackBarEvent("Your Download has started","Hide");
        progressBar.progressProperty().bind(videoDownloadTask.progressProperty());
        Thread thread = new Thread(videoDownloadTask);
        thread.setDaemon(true);
        thread.start();
        cancelButton.setOnAction(cancelDownloadEvent(videoDownloadTask));
        videoDownloadTask.setOnSucceeded(event -> {
            try {
                this.downloadDetails = videoDownloadTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (this.downloadDetails.isExists()) {
                statusLabel.setText("YAY! You have already downloaded this file.");
                statusLabel.setTextFill(Color.RED);

            } else {
                statusLabel.setText(this.downloadDetails.getMessage());
                statusLabel.setTextFill(Color.web("#268515"));
            }
        });
        videoDownloadTask.setOnCancelled(event -> {
                    fireSnackBarEvent("Video Download Cancelled","Hide");
                    statusLabel.setText("Why you cancelled?");
//                    try {
//                        System.out.println("Message from thread:"+videoDownloadTask.get().getMessage());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
                }
        );
    }


    private EventHandler<ActionEvent> cancelDownloadEvent(VideoDownloadTask task) {
        EventHandler<ActionEvent> event = e -> {
            System.out.println("Cancelling Download");
            task.cancel(true);
            statusLabel.setVisible(true);
            statusLabel.setText("Cancelled");
        };
        return event;
    }

    private EventHandler<ActionEvent> downloadButtonClickedEvent() {
        EventHandler<ActionEvent> event = e -> {
            statusLabel.setVisible(true);
            String text = fbDownloadLink.getText();
            if (!Utils.isValidURL(text)) {
                System.out.println("URL is not valid");
                statusLabel.setText("URL Entered is not valid");
//                fireSnackBarEvent("Please enter a valid facebook url","Hide");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            DownloadLinkProvider downloadLinkProvider = null;
            try {
                downloadLinkProvider = new DownloadLinkProvider(text,directLink.isSelected());
            } catch (Exception e1) {
                e1.printStackTrace();
                statusLabel.setWrapText(true);
                statusLabel.setText("We got some serious error while processing:" + e1.getMessage());
                fireSnackBarEvent("System error","Hide");
                statusLabel.setTextFill(Color.RED);
                return;
            }
            this.downloadDetails = downloadLinkProvider.getDownloadDetails().get(0);
            if (this.downloadDetails.getMessage() != null) {
                System.out.println("There is some error.");
                statusLabel.setTranslateY(0.5);
                statusLabel.setWrapText(true);
                statusLabel.setText(this.downloadDetails.getMessage());
                statusLabel.setTextFill(Color.RED);
            } else {
                this.downloadDetails.setDownloadDir(downloadDir.getText()+"/");
                progressBar.setVisible(true);
                System.out.println("Starting thread");
                calculateDownloadSize(progressBar, statusLabel);
            }
        };
        return event;
    }

    public void selectDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        System.out.println("Going to choose dialogue");
        File selectedDirectory = directoryChooser.showDialog(downloadDir.getParent().getScene().getWindow());
        if (selectedDirectory == null) {
            //Put an Error Label;
        } else {
            System.out.println("Selected Directory:" + selectedDirectory.getAbsolutePath());
            fireSnackBarEvent("Selected Directory:" + selectedDirectory.getAbsolutePath(),"Hide");
            downloadDir.setText(selectedDirectory.getAbsolutePath());
            this.downloadDetails.setDownloadDir(selectedDirectory.getAbsolutePath()+"/");
            downloadButton.setDisable(false);
        }
    }
}
