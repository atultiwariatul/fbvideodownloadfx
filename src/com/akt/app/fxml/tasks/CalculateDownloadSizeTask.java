package com.akt.app.fxml.tasks;

import com.akt.app.fxml.model.DownloadDetails;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CalculateDownloadSizeTask extends Task<DownloadDetails> {
         private final DownloadDetails downloadDetails;

         public CalculateDownloadSizeTask(DownloadDetails downloadDetails) {
             this.downloadDetails = downloadDetails;
         }

         @Override protected DownloadDetails call() {
             URLConnection conn = null;
             if (this.downloadDetails.getDownloadDir()==null){
                 this.downloadDetails.setDownloadDir("/home/atul/Videos/fb");
             }
             System.out.println("Download Directory and File:"+this.downloadDetails.getDownloadDir()+this.downloadDetails.getFileName());
             File f = new File(this.downloadDetails.getDownloadDir()+this.downloadDetails.getFileName());
             if (f.exists()){
                 this.downloadDetails.setMessage("File "+ this.downloadDetails.getFileName() +" Already Exists");
                 this.downloadDetails.setExists(true);
                 updateProgress(1,1);

                 return this.downloadDetails;
             }
             try {
                 conn = new URL(this.downloadDetails.getDownloadLink()).openConnection();
                 if(conn instanceof HttpURLConnection) {
                     ((HttpURLConnection)conn).setRequestMethod("HEAD");
                 }
                 conn.getInputStream();
                 this.downloadDetails.setTotalSize(conn.getContentLength());
                 return this.downloadDetails;
             } catch (IOException e) {
                 this.downloadDetails.setMessage(e.getMessage());
             } finally {
                 if(conn instanceof HttpURLConnection) {
                     ((HttpURLConnection)conn).disconnect();
                 }
             }
             return this.downloadDetails;
         }
     }
