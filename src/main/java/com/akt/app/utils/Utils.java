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
        if (url.contains("facebook")) {
            String[] tokens = url.split("=");
            return tokens[tokens.length - 1];
        }else {
            return url.substring(url.lastIndexOf("/")+1, url.indexOf(".mp4"));
        }
//        return null;
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
        String s = "?https://video-ams4-1.xx.fbcdn.net/v/t39.24130-2/10000000_1642682365886891_6641411404153519299_n.mp4?_nc_cat=107&_nc_sid=985c63&efg=eyJ2ZW5jb2RlX3RhZyI6Im9lcF9oZCJ9&_nc_ohc=Wx1Jp8T1_9UAX_XMHDd&_nc_ht=video-ams4-1.xx&oh=fe3ae2541f154c01d6e20ab42305a1c2&oe=5F5F04F9";
        String[] tokens = null;

        if (s.contains("ref")) {
            tokens = s.split("&");
        } else {
            System.out.println(s.substring(s.lastIndexOf("/"),s.indexOf(".mp4")));
        }
        System.out.println(Arrays.toString(tokens));
    }
}
