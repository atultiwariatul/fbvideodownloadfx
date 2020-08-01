package com.akt.app.tasks;

import com.akt.app.model.DownloadDetails;
import javafx.concurrent.Task;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class VideoDownloadTask extends Task<DownloadDetails> {
    private final DownloadDetails downloadDetails;
    private final int BUFFER_SIZE = 1024;

    public VideoDownloadTask(DownloadDetails downloadDetails) {
        this.downloadDetails = downloadDetails;
    }

    @Override
    protected DownloadDetails call() throws Exception {
        System.out.println("Starting Download");
        long totalSize = 0;

        OutputStream outStream = null;
        URLConnection uCon = null;
        InputStream is = null;
        URL downloadFileUrl = null;
        try {
            downloadFileUrl = new URL(downloadDetails.getDownloadLink());
            totalSize = downloadDetails.getTotalSize();
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        long startTime = 0;
        try {
            byte[] buf;
            int byteRead, byteWritten = 0;
            outStream = new BufferedOutputStream(new FileOutputStream(this.downloadDetails.getDownloadDir() + this.downloadDetails.getFileName()));

            assert downloadFileUrl != null;
            uCon = downloadFileUrl.openConnection();
            is = uCon.getInputStream();
            buf = new byte[BUFFER_SIZE];
            startTime = System.currentTimeMillis();
            while ((byteRead = is.read(buf)) != -1 ) {
                if (this.isCancelled()){
                    System.out.println("Task is cancelled so stopping it");
                    this.downloadDetails.setMessage("Download Cancelled After downloading:"+(byteWritten/1024)+" KB of Data/"+(totalSize/1024));
                    updateMessage("Cancelled By user");
                    updateTitle("Cancelled By User!");
                    updateProgress(1,1);
                    return this.downloadDetails;
                }else{
                    outStream.write(buf, 0, byteRead);
                    byteWritten += byteRead;
//                    if (byteWritten>(1024*5)) {
//                        float bytesPerSecond = byteWritten / ((System.currentTimeMillis() - startTime) / 1000.0f);
//                        System.out.println("Download Speed =>" + bytesPerSecond + " BPS");
//                        float kbPerSecond = bytesPerSecond/1024.0f;
//                        System.out.println("Download Speed =>" + kbPerSecond + " KBPS");
//                    }
                    updateProgress(byteWritten, totalSize);
                    downloadDetails.setSize(byteWritten);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.downloadDetails.setMessage(e.getMessage());
        } finally {
            try {
                assert is != null;
                is.close();
                outStream.close();
                long kbps = (this.downloadDetails.getTotalSize()/((System.currentTimeMillis() - startTime) / 1000))/1024;
                System.out.println("Average Download speed in KBPS :"+kbps);
                this.downloadDetails.setMessage("Download Successful, Avg speed :"+kbps+" KBPS");
            } catch (IOException e) {
                e.printStackTrace();
                this.downloadDetails.setMessage(e.getMessage());
            }
        }
        return this.downloadDetails;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }
}
