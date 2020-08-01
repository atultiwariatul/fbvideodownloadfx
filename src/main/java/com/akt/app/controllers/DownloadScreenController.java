package com.akt.app.controllers;

import com.akt.app.model.DownloadDetails;
import com.akt.app.services.DownloadLinkProvider;
import com.akt.app.services.DownloadService;
import com.akt.app.tasks.CalculateDownloadSizeTask;
import com.akt.app.tasks.VideoDownloadTask;
import com.akt.app.utils.Utils;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    public JFXSnackbar snackBar ;
    public AnchorPane downloadAnchorPane;
    public AnchorPane anchorPaneRef;

    private DownloadDetails downloadDetails = new DownloadDetails();

    private DownloadService downloadService;
    public void initializeManager(final DownloadService service) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        this.downloadService = service;
        System.out.println("Videos Directory Path:"+ VIDEOS_DIRECTORY);
        anchorPaneRef = (AnchorPane) downloadButton.getParent();
        downloadButton.setOnAction(downloadButtonClickedEvent());
        fbDownloadLink.focusedProperty().addListener (event -> {
            statusLabel.setVisible(true);
            System.out.println("Event Text:"+fbDownloadLink.getText());
            if (!fbDownloadLink.getText().contains("https")){
                fbDownloadLink.requestFocus();
                statusLabel.setText("Please enter valid Facebook video URL");
                statusLabel.setTranslateY(2);
            }else{
                statusLabel.setText("Download Link validated");
            }
        });
        snackBar = new JFXSnackbar(anchorPaneRef);

//        snackBar.set
        JFXSnackbarLayout snackbarLayout = new JFXSnackbarLayout("Application is Ready to use", "Hide", (event)->{
            System.out.println("Going to close the snackbar");
            if (event != null) {
                snackBar.close();
            }
        });
        JFXSnackbar.SnackbarEvent event = new JFXSnackbar.SnackbarEvent(snackbarLayout,new Duration(5000));
        snackBar.enqueue(event);
    }

    private void calculateDownloadSize(ProgressBar progressBar, Label statusLabel){
        statusLabel.setVisible(true);
        statusLabel.setText("Calculating Size...");
        System.out.println("In Calculate Download Size:"+this.downloadDetails.getDownloadDir());
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
            }else {
                statusLabel.setText("YAY! You have already downloaded this file.");
                statusLabel.setTextFill(Color.RED);
            }
        });
    }

    private void downloadVideo(ProgressBar progressBar, Label statusLabel){
        statusLabel.setVisible(true);
        statusLabel.setText("Starting Download...");
        VideoDownloadTask videoDownloadTask = new VideoDownloadTask(downloadDetails);
        statusLabel.setText("Download Started...");
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
            if (this.downloadDetails.isExists()){
                statusLabel.setText("YAY! You have already downloaded this file.");
                statusLabel.setTextFill(Color.RED);

            }else {
                statusLabel.setText(this.downloadDetails.getMessage());
                statusLabel.setTextFill(Color.web("#268515"));
            }
        });
        videoDownloadTask.setOnCancelled(event -> {
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


    private EventHandler<ActionEvent> cancelDownloadEvent(VideoDownloadTask task){
        EventHandler<ActionEvent> event = e -> {
            System.out.println("Cancelling Download");
            task.cancel(true);
            statusLabel.setVisible(true);
            statusLabel.setText("Cancelled");
        };
        return event;
    }
    private EventHandler<MouseEvent> directoryChooserClickedEvent(){
        EventHandler<MouseEvent> event = e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(downloadService.getStage());
            if(selectedDirectory == null){
                //Put an Error Label;
            }else{
//                downloadDir = selectedDirectory.getAbsolutePath()+"/";
                System.out.println("Dir:"+selectedDirectory.getName());
                downloadDir.setText(selectedDirectory.getName());
                System.out.println("After directory selector:"+this.downloadDetails.getDownloadDir());
                downloadButton.setDisable(false);
            }
        };
        return event;
    }

    private EventHandler<ActionEvent> downloadButtonClickedEvent(){
        EventHandler<ActionEvent> event = e -> {
            statusLabel.setVisible(true);
            String text  = fbDownloadLink.getText();
            if (!Utils.isValidURL(text)){
                System.out.println("URL is not valid");
                statusLabel.setText("URL Entered is not valid");
                statusLabel.setTextFill(Color.RED);
                return ;
            }

            DownloadLinkProvider downloadLinkProvider = null;
            try {
                downloadLinkProvider = new DownloadLinkProvider(text);
            }catch (Exception e1){
                e1.printStackTrace();
                statusLabel.setWrapText(true);
                statusLabel.setText("We got some serious error while processing:"+e1.getMessage());
                statusLabel.setTextFill(Color.RED);
                return;
            }
            this.downloadDetails = downloadLinkProvider.getDownloadDetails().get(0);
            if (this.downloadDetails.getMessage()!=null){
                System.out.println("There is some error.");
                statusLabel.setTranslateY(0.5);
                statusLabel.setWrapText(true);
                statusLabel.setText(this.downloadDetails.getMessage());
                statusLabel.setTextFill(Color.RED);
            }else {
//                this.downloadDetails.setDownloadDir(downloadDir);
                progressBar.setVisible(true);
                System.out.println("Starting thread");
                calculateDownloadSize(progressBar, statusLabel);
            }
        };
        return event;
    }

    public void selectDirectory() {
        System.out.println("Click count");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        System.out.println("Going to choose dialogue");
        File selectedDirectory = directoryChooser.showDialog(downloadDir.getParent().getScene().getWindow());
        if(selectedDirectory == null){
            //Put an Error Label;
        }else{
//                downloadDir = selectedDirectory.getAbsolutePath()+"/";
            System.out.println("Dir:"+selectedDirectory.getName());
            downloadDir.setText(selectedDirectory.getName());
            System.out.println("After directory selector:"+this.downloadDetails.getDownloadDir());
            downloadButton.setDisable(false);
        }
    }
}
