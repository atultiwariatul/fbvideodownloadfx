package com.akt.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Group;  
import javafx.scene.Scene;  
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;  
import javafx.stage.Stage;  
public class JavaFX_Media extends Application
{  
  
    @Override  
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub  
        //Initialising path of the media file, replace this with your file path   
        String path = "/home/atul/Projects/JavaFX/fbdownloaderfx/598061674444566.mp4";
        String[] cmd={"bash","-c","ffmpeg -i /home/atul/Projects/JavaFX/fbdownloaderfx/598061674444566.mp4 2>&1 | grep Video: | grep -Po '\\d{3,5}x\\d{3,5}'"};
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s;
        int height = 0;
        int width = 0;
        while ((s = reader.readLine()) != null) {
            String[] a = s.split("x");
            height = Integer.parseInt(a[0]);
            width = Integer.parseInt(a[1]);
            System.out.println("Script ---output: " + s);
        }
//        System.out.println("Read output:"+ Arrays.toString(output));
        //Instantiating Media class  
        Media media = new Media(new File(path).toURI().toString());
        System.out.println("Media Height:"+media.getHeight());
        System.out.println("Media Width:"+media.getWidth());
        //Instantiating MediaPlayer class
        MediaPlayer mediaPlayer = new MediaPlayer(media);  
        //Instantiating MediaView class
        MediaView mediaView = new MediaView(mediaPlayer);  
//        mediaView.setOn
        //by setting this property to true, the Video will be played   
        mediaPlayer.setAutoPlay(true);  
          
        //setting group and scene   
        Group root = new Group();  
        root.getChildren().add(mediaView);  
        Scene scene = new Scene(root,height,width);
        primaryStage.setScene(scene);  
        primaryStage.setTitle("Playing video");
//        mediaView.setFitHeight(1080);
//        mediaView.setFitWidth(1080);

        primaryStage.show();
    }  
    public static void main(String[] args) {
        launch(args);  
    }  
      
}  
