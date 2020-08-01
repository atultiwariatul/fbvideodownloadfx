package com.akt.app.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaView;

public class DownloadedItem extends RecursiveTreeObject<DownloadedItem> {
    private StringProperty name;
    private ImageView image;
    private ObjectProperty<MediaView> mediaView;

    public DownloadedItem(String name,MediaView mediaView1) {
        this.name = new SimpleStringProperty(name);
        mediaView = new SimpleObjectProperty<>(mediaView1);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public MediaView getMediaView() {
        return mediaView.get();
    }

    public ObjectProperty<MediaView> mediaViewProperty() {
        return mediaView;
    }

    public void setMediaView(MediaView mediaView) {
        this.mediaView.set(mediaView);
    }
}
