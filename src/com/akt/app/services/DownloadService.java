package com.akt.app.services;

import com.akt.app.controllers.DownloadScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class DownloadService {
    private final Stage stage;


    public DownloadService(Stage stage)
    {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setScene_DownloadPage() {
        URL url =  this.getClass().getResource("../ui/DownloadScreen.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Image loginicon = new Image("/resources/img/loginicon.png");
        try
        {
            Parent root = fxmlLoader.load();
            root.getStylesheets().add("/resources/css/global.css");
            stage.getIcons().setAll(loginicon);
            stage.setTitle("Facebook Video Downloader");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            DownloadScreenController loginController = fxmlLoader.<DownloadScreenController>getController();
            loginController.initializeManager(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
