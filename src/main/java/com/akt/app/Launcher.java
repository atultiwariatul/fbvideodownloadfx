package com.akt.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Launcher extends Application {
    
    public static Boolean isSplashLoaded = false;
    
    @Override
    public void start(Stage stage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/container.fxml"));
            AnchorPane pane = (AnchorPane) root;
            Image logoIcon = new Image("/img/blue_logo.png");
            Scene scene = new Scene(root,600,400);
            stage.setScene(scene);
            stage.setTitle("Facebook Video Downloader");
            stage.getIcons().setAll(logoIcon);
            scene.getStylesheets().add("/css/register.css");
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
