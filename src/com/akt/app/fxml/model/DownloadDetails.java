package com.akt.app.fxml.model;

public class DownloadDetails {
    private String fbUrl;
    private String downloadLink;
    private String title;
    private long size;
    private long processStartMillis;
    private long processEndMillis;
    private String fileName;
    private String message;
    private long totalSize;
    //TODO: Need to see how to get Default Video Directory of System.
    private String downloadDir="";
    private boolean exists;


    public DownloadDetails(String fbUrl, String downloadLink, String fileName) {
        this.fbUrl = fbUrl;
        this.downloadLink = downloadLink;
        this.fileName = fileName;
        this.processStartMillis = System.currentTimeMillis();
    }

    public DownloadDetails(String fbUrl, String downloadLink, String title, String fileName) {
        this.fbUrl = fbUrl;
        this.downloadLink = downloadLink;
        this.title = title;
        this.fileName = fileName;
        this.processStartMillis = System.currentTimeMillis();
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getFbUrl() {
        return fbUrl;
    }

    public void setFbUrl(String fbUrl) {
        this.fbUrl = fbUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getProcessStartMillis() {
        return processStartMillis;
    }

    public void setProcessStartMillis(long processStartMillis) {
        this.processStartMillis = processStartMillis;
    }

    public long getProcessEndMillis() {
        return processEndMillis;
    }

    public void setProcessEndMillis(long processEndMillis) {
        this.processEndMillis = processEndMillis;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
