package com.akt.app.controllers;

import com.akt.app.model.DownloadedItem;
import com.akt.app.utils.Utils;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import static com.akt.app.utils.Utils.VIDEO_DIR;

public class DownloadedItemsController implements Initializable {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private JFXTreeTableView<DownloadedItem> treeTableView;
    private static int currentIndex = 0;
    private int PLAYED_VIDEO_HEIGHT=480;
    private int PLAYED_VIDEO_WIDTH=480;
    @FXML
    void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<DownloadedItem> items = prepareTreeViewItems();
        final TreeItem<DownloadedItem> root = new RecursiveTreeItem<>(items, RecursiveTreeObject::getChildren);

        treeTableView.setOnMouseClicked((MouseEvent event) -> {
            if(event.getClickCount()>1){
                String fileName=treeTableView.getSelectionModel().getSelectedItem().getValue().getName();
                System.out.println("Mouse click Captured: File Clicked:"+fileName);
                Media media1 = new Media(new File(VIDEO_DIR +fileName).toURI().toString());

                String heightWidth[] = null;
                try {
                    heightWidth = Utils.getHeightWidth(VIDEO_DIR +fileName);
                    PLAYED_VIDEO_HEIGHT = Integer.parseInt(heightWidth[0]);
                    PLAYED_VIDEO_WIDTH = Integer.parseInt(heightWidth[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.format("Height: %s X Width: %s ",PLAYED_VIDEO_HEIGHT,PLAYED_VIDEO_WIDTH);


                Slider slider = new Slider();
                Button nextButton = new Button("Next");

                final VBox playerFeatures = addMediaPlayerFeatures(slider, nextButton);


                configureNextButtonAction(nextButton);
                MediaPlayer mediaPlayer = new MediaPlayer(media1);
                MediaView mediaView = new MediaView(mediaPlayer);

                StackPane mediaPlayerLayout = new StackPane();
                mediaPlayerLayout.getChildren().add(mediaView);
                mediaPlayerLayout.getChildren().add(playerFeatures);

                startPlayer(fileName, mediaPlayerLayout);

                configureMediaPlayer(mediaPlayer, slider);
            }
        });
//        treeTableView.resize(1200,800);
        setTreeTableViewProperties(root);
    }

    private void setTreeTableViewProperties(TreeItem<DownloadedItem> root) {
//        treeTableView.setPrefHeight(400);
//        treeTableView.setPrefWidth(200);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    private ObservableList<DownloadedItem> prepareTreeViewItems() {
        JFXTreeTableColumn<DownloadedItem,String> nameColumn = new JFXTreeTableColumn<>("Name");
        nameColumn.setPrefWidth(50);
        nameColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());

        JFXTreeTableColumn<DownloadedItem, MediaView> video = new JFXTreeTableColumn<>("Video");
        video.setPrefWidth(400);
        video.setCellValueFactory((TreeTableColumn.CellDataFeatures<DownloadedItem, MediaView> param) -> {
            ObjectProperty<MediaView> mediaView = param.getValue().getValue().mediaViewProperty();
            mediaView.getValue().setFitHeight(150);
            mediaView.getValue().setFitWidth(400);
            return mediaView;
        });
        ObservableList<DownloadedItem> items = FXCollections.observableArrayList();
        try {
            items.addAll(Utils.getDownloadList());
            System.out.println("Total videos found in that directory:"+items.size());
            treeTableView.getColumns().setAll(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void configureNextButtonAction(Button nextButton) {
        nextButton.setOnAction(actionEvent -> {
            System.out.println("Next button clicked:Total Items:"+treeTableView.getCurrentItemsCount());
            int currentIndex = treeTableView.getSelectionModel().getSelectedIndex();
            System.out.println("Current Index:"+currentIndex);
            if (currentIndex<treeTableView.getCurrentItemsCount()) {
                treeTableView.getSelectionModel().select(currentIndex+1);
            }else {
                System.out.println("We have reached end");
            }
        });
    }

    private VBox addMediaPlayerFeatures(Slider slider, Button nextButton) {
        final VBox vBox = new VBox();
        vBox.getChildren().add(slider);
        vBox.getChildren().add(nextButton);
        System.out.println("\nSetting Height of Slider:"+(PLAYED_VIDEO_HEIGHT-50));
        System.out.println("Setting Width of Slider:"+PLAYED_VIDEO_WIDTH);
        vBox.setMinSize(PLAYED_VIDEO_WIDTH,100);
        vBox.setTranslateY(PLAYED_VIDEO_HEIGHT-50);
        return vBox;
    }

    private void configureMediaPlayer(MediaPlayer mediaPlayer, Slider slider) {
        mediaPlayer.currentTimeProperty().addListener((observableValue, duration, current) -> {
            slider.setValue(current.toSeconds());
        });
        slider.setOnMouseClicked(mouseEvent -> {
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        });

        mediaPlayer.setOnReady(() -> {
            System.out.println("Media Player is ready now");
            System.out.println("AFer media:W"+mediaPlayer.getMedia().getWidth());
            System.out.println("AFer media:H"+mediaPlayer.getMedia().getHeight());
            PLAYED_VIDEO_WIDTH = mediaPlayer.getMedia().getWidth();
            PLAYED_VIDEO_HEIGHT = mediaPlayer.getMedia().getHeight();
            slider.setMin(0.0);
            slider.setValue(0.0);
            slider.setMax(mediaPlayer.getTotalDuration().toSeconds());

        });
        mediaPlayer.setAutoPlay(true);
    }

    private void startPlayer(String fileName, StackPane secondaryLayout) {
        Window treeTableViewWindow = treeTableView.getParent().getScene().getWindow();
        Scene playerScene = new Scene(secondaryLayout, PLAYED_VIDEO_HEIGHT, PLAYED_VIDEO_WIDTH);
        Stage playerWindow = new Stage();
        playerWindow.setTitle(fileName);
        playerWindow.setScene(playerScene);
        playerWindow.initModality(Modality.WINDOW_MODAL);
        playerWindow.initOwner(treeTableViewWindow);
        playerWindow.setX(treeTableViewWindow.getX());
        playerWindow.setY(treeTableViewWindow.getY());
        playerWindow.show();
    }
}

