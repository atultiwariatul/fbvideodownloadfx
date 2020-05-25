package com.akt.app.fxml.tasks;

import com.akt.app.fxml.model.DownloadDetails;
import javafx.concurrent.Task;

public class GetDownloadingLinkTask extends Task<DownloadDetails> {
    private final DownloadDetails downloadDetails;

    public GetDownloadingLinkTask(DownloadDetails downloadDetails) {
        this.downloadDetails = downloadDetails;
    }

    @Override
    protected DownloadDetails call() throws Exception {
        return null;
    }
}
