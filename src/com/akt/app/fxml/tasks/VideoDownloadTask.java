package com.akt.app.fxml.tasks;

import com.akt.app.fxml.model.DownloadDetails;
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
        try {
            byte[] buf;
            int byteRead, byteWritten = 0;
            outStream = new BufferedOutputStream(new FileOutputStream(this.downloadDetails.getDownloadDir() + this.downloadDetails.getFileName()));

            assert downloadFileUrl != null;
            uCon = downloadFileUrl.openConnection();
            is = uCon.getInputStream();
            buf = new byte[BUFFER_SIZE];
            while ((byteRead = is.read(buf)) != -1) {
                outStream.write(buf, 0, byteRead);
                byteWritten += byteRead;
                updateProgress(byteWritten, totalSize);
                downloadDetails.setSize(byteWritten);
                //System.out.println(byteWritten+" bytes of "+totalSize+" is written to Disk");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.downloadDetails.setMessage(e.getMessage());
        } finally {
            try {
                assert is != null;
                is.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                this.downloadDetails.setMessage(e.getMessage());
            }
        }
        return this.downloadDetails;
    }
}
