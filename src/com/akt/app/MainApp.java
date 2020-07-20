package com.akt.app;

import com.akt.app.services.DownloadService;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application
{
    /**
     * The main method is only called as a last resort in JavaFX applications.
     *
     * @param args Java program arguments.
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    /**
     * The start method is inherited from Application and is called on startup in JavaFX applications.
     *
     * @param primaryStage The first stage to be displayed.
     */
    @Override
    public void start(Stage primaryStage)
    {
        DownloadService downloadService = new DownloadService(primaryStage);
        downloadService.setScene_DownloadPage();
        primaryStage.show();
    }
}
