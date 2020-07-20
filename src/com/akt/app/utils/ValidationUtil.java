package com.akt.app.utils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static boolean isValidURL(String url){
        return Pattern.compile(URL_REGEX).matcher(url).matches();
    }
    public static String getFileName(String url){
        String[] tokens = url.split("=");
        return tokens[tokens.length-1];
    }
    public static String validateVideoLink(String videoURL){
        if (videoURL.contains("ref")){

            String[] tokens = videoURL.split("&");
            String video = tokens[tokens.length-1];
            return "https://www.facebook.com/watch/?"+video.substring(0,video.indexOf(".mp4"));
        }else {
            return videoURL;
        }
    }
    public static void main(String... args){
        String s = "?ref=saved&v=270418464144537.mp4";
        String[] tokens = null;

        if(s.contains("ref")){
            tokens = s.split("&");
        }else{

        }
        System.out.println(Arrays.toString(tokens));
    }
}
