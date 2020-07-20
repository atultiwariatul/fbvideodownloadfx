package com.akt.app.controllers;

import com.akt.app.model.DownloadDetails;
import com.akt.app.services.DownloadService;
import com.akt.app.task.CalculateDownloadSizeTask;
import com.akt.app.task.VideoDownloadTask;
import com.akt.app.utils.ValidationUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class DownloadScreenController {
    public Button directorySelect;
    public String downloadDir;
    public TextField downloadLink;
    public Button downloadButton;
    public Label directoryChosen;
    public ProgressBar progressBar;
    public Label statusLabel;
    private DownloadDetails downloadDetails = new DownloadDetails(null,null,null);

    private DownloadService downloadService;
    public void initializeManager(final DownloadService service) {
        this.downloadService = service;
        directorySelect.setOnAction(directoryChooserClickedEvent());
        downloadButton.setOnAction(downloadButtonClickedEvent());
        System.out.println("coming here");
    }

    private void calculateDownloadSize(ProgressBar progressBar, Label statusLabel){
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
        statusLabel.setText("Starting Download...");
        VideoDownloadTask videoDownloadTask = new VideoDownloadTask(downloadDetails);
        statusLabel.setText("Download Started...");
        progressBar.progressProperty().bind(videoDownloadTask.progressProperty());
        Thread thread = new Thread(videoDownloadTask);
        thread.setDaemon(true);
        thread.start();
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
                statusLabel.setText("Download Successful:");
                statusLabel.setTextFill(Color.web("#268515"));
            }
        });
    }

    private EventHandler<ActionEvent> directoryChooserClickedEvent(){
        EventHandler<ActionEvent> event = e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(downloadService.getStage());
            if(selectedDirectory == null){
                //Put an Error Label;
            }else{
                downloadDir = selectedDirectory.getAbsolutePath()+"/";
                directoryChosen.setText(downloadDir);
                System.out.println("After directory selector:"+this.downloadDetails.getDownloadDir());
                downloadButton.setDisable(false);
            }
        };
        return event;
    }
    private EventHandler<ActionEvent> downloadButtonClickedEvent(){
        EventHandler<ActionEvent> event = e -> {
            String text  = downloadLink.getText();
            if (!ValidationUtil.isValidURL(text)){
                System.out.println("URL is not valid");
                statusLabel.setText("URL Entered is not valid");
                statusLabel.setTextFill(Color.RED);
                return ;
            }

            DownloadLinkProvider downloadLinkProvider = null;
            try {
                downloadLinkProvider = new DownloadLinkProvider(text);
                this.downloadDetails = downloadLinkProvider.getDownloadDetails().get(0);
            }catch (Exception e1){
                e1.printStackTrace();
                statusLabel.setText("We got some serious error while processing:"+e1.getMessage());
                statusLabel.setTextFill(Color.RED);
                return;
            }

            if (this.downloadDetails.getMessage()!=null){
                System.out.println("There is some error.");
                statusLabel.setText(this.downloadDetails.getMessage());
                statusLabel.setTextFill(Color.RED);
            }else {
                this.downloadDetails.setDownloadDir(downloadDir);
                progressBar.setVisible(true);
                System.out.println("Starting thread");
                calculateDownloadSize(progressBar, statusLabel);
            }
        };
        return event;
    }
}