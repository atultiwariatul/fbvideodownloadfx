package com.akt.app.services;

import com.akt.app.controllers.NavigationController;
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
        URL url =  this.getClass().getResource("../ui/container.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Image logoIcon = new Image("/img/white_logo.png");
        try
        {
            Parent root = fxmlLoader.load();
            root.getStylesheets().add("/css/register.css");
            stage.getIcons().setAll(logoIcon);
            stage.setTitle("Facebook Video Downloader");
            stage.setScene(new Scene(root,600,430));
            stage.setResizable(true);
            NavigationController loginController = fxmlLoader.getController();
//            loginController.initializeManager(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
