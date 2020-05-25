package com.akt.app.fxml;

import com.akt.app.fxml.controllers.DownloadLinkProvider;
import com.akt.app.fxml.model.DownloadDetails;
import com.akt.app.fxml.tasks.CalculateDownloadSizeTask;
import com.akt.app.fxml.tasks.VideoDownloadTask;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main extends Application {
    private final String INPUT_FILE="input.txt";
    List<DownloadDetails> downloadDetailsList = null;
    String downloadDir = null;
    DownloadDetails downloadDetails = new DownloadDetails(null,null,null);
    @Override
    public void start(Stage s) throws Exception{
        s.setTitle("Facebook video Downloader");
        TextField textField = new TextField();

        ProgressBar pBar = new ProgressBar();
        pBar.setVisible(false);
        Label statusLabel = new Label();
        statusLabel.setText("Enter fb Video Link below");
        VBox root = new VBox();
        root.setFillWidth(true);
        root.setAlignment(Pos.CENTER);


        EventHandler<ActionEvent> event = e -> {
            String text  = textField.getText();
            DownloadLinkProvider downloadLinkProvider = new DownloadLinkProvider(text);
            this.downloadDetails = downloadLinkProvider.getDownloadDetails().get(0);
            this.downloadDetails.setDownloadDir(downloadDir);
            pBar.setVisible(true);
            System.out.println("Starting thread");
            calculateDownloadSize(pBar,statusLabel);
        };

        Button b = new Button("Download");
        Button directorySelect = new Button("Choose Download Directory");
        directorySelect.setOnAction(event1 -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(s);
            if(selectedDirectory == null){

            }else{
                downloadDir = selectedDirectory.getAbsolutePath()+"/";
                System.out.println("After directory selector:"+this.downloadDetails.getDownloadDir());
            }
        });
        b.setOnAction(event);
        if (downloadDetails.getMessage()!=null){
            b.setText(downloadDetails.getMessage());
            b.setDisable(true);
        }

        root.getChildren().add(directorySelect);
        root.getChildren().add(statusLabel);
        root.getChildren().add(textField);
        root.getChildren().add(b);
        root.getChildren().add(pBar);
        Scene sc = new Scene(root, 600, 200);
        s.setScene(sc);
        s.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping Download....");
        super.stop();
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

    private List<String> processFBLinks() throws FileNotFoundException {
        List<String> toProcess = new ArrayList<>();
        Scanner scanner = new Scanner(new File(INPUT_FILE));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.trim().isEmpty()) {
                toProcess.add(line);
            }
        }
        return toProcess;
    }
}
