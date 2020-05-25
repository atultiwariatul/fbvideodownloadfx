package com.akt.app.fxml.controllers;

import com.akt.app.fxml.model.DownloadDetails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class DownloadLinkProvider {
    private List<String> fbLinks;
    private List<DownloadDetails> downloadDetails = new ArrayList<>();

    public DownloadLinkProvider(List<String> fbLinks) {
        this.fbLinks = fbLinks;
        prepareLinks();
    }

    public DownloadLinkProvider(String fbURL){
        this.fbLinks = new ArrayList<>(1);
        this.fbLinks.add(fbURL);
        prepareLinks();
    }
    public List<DownloadDetails> getDownloadDetails() {
        return downloadDetails;
    }

    private void prepareLinks(){
        fbLinks.parallelStream().forEach(downloadLinkObj -> {
            downloadDetails.add(getDownloadLink(downloadLinkObj));
        });
    }
    private String getFileNameFromLink(String fbVideoUrl) {
        String[] splits = fbVideoUrl.split("/");
        return splits[splits.length - 1] + ".mp4";
    }

    private Document getHTMLDocument(String fbVideoUrl) throws IOException {
        return Jsoup.connect("https://www.getfvid.com/downloader").data("url", fbVideoUrl).post();
    }
    private DownloadDetails getDownloadLink(String fbVideoUrl)  {
        String fileName = getFileNameFromLink(fbVideoUrl);
        System.out.println("File Name:" + fileName);
        DownloadDetails downloadLinkObj = new DownloadDetails(fbVideoUrl,null,fileName);
        Element link = null;
        try {
            link = getHTMLDocument(fbVideoUrl).select("div.col-md-4").first();
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof UnknownHostException){
                downloadLinkObj.setMessage("Please Check your internet Connection or Service is down");
            }else {
                downloadLinkObj.setMessage(e.getLocalizedMessage());
            }
            return downloadLinkObj;
        }
        String downloadLink = link.select("a").first().attr("href");
        System.out.println("\n\nDownload Link:" + downloadLink);
        downloadLinkObj.setDownloadLink(downloadLink);
        return downloadLinkObj;
    }
}
