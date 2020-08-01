package com.akt.app.utils;

import com.akt.app.model.DownloadedItem;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Utils {
    private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static final String VIDEO_DIR = "/home/atul/Videos/fb/finished/";
    public static final String VIDEOS_DIRECTORY = System.getProperty("user.home")+"/Videos/";
    public static boolean isValidURL(String url) {
        return Pattern.compile(URL_REGEX).matcher(url).matches();
    }

    public static String getFileName(String url) {
        String[] tokens = url.split("=");
        return tokens[tokens.length - 1];
    }

    public static String validateVideoLink(String videoURL) {
        if (videoURL.contains("ref")) {

            String[] tokens = videoURL.split("&");
            String video = tokens[tokens.length - 1];
            return "https://www.facebook.com/watch/?" + video.substring(0, video.indexOf(".mp4"));
        } else {
            return videoURL;
        }
    }

    public static String[] getHeightWidth(String filePath) throws IOException {
        String[] cmd = {"bash", "-c", "ffmpeg -i " + filePath + " 2>&1 | grep Video: | grep -Po '\\d{3,5}x\\d{3,5}'"};
        Process p = null;
        p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = null;
        String[] heightWidth = null;
        while ((s = reader.readLine()) != null) {
            heightWidth = s.split("x");
        }
        return heightWidth;
    }

    public static List<DownloadedItem> getDownloadList() throws IOException {
        List<DownloadedItem> items = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(VIDEO_DIR))) {
            paths.forEach(path -> {
                if (path.getFileName().toString().endsWith(".mp4")) {
                    String name = path.getFileName().toString();
                    DownloadedItem item = new DownloadedItem(name,getMediaView(name));
                    items.add(item);
                    System.out.println(path.getFileName());
                }
            });
        }
        return items;
    }

    private static MediaView getMediaView(String name){
        Media media = new Media(new File(VIDEO_DIR+name).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            int w = media.getWidth();
            int h = media.getHeight();
            Duration d =media.getDuration();
            System.out.println("DownloadedItem:Media Details: Name:"+name+", width:"+w+", Height:"+h+", Duration in minutes:"+d.toMinutes());
        });
        mediaPlayer.setAutoPlay(false);
        MediaView mediaView = new MediaView(mediaPlayer);
        return mediaView;
    }

    public static void main(String... args) {
        String s = "?ref=saved&v=270418464144537.mp4";
        String[] tokens = null;

        if (s.contains("ref")) {
            tokens = s.split("&");
        } else {

        }
        System.out.println(Arrays.toString(tokens));
    }
}
