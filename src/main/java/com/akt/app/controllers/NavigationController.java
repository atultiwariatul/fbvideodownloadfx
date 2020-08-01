package com.akt.app.controllers;

import com.akt.app.Launcher;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NavigationController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (!Launcher.isSplashLoaded) {
            loadSplashScreen();
        }
        loadMainContainer();
    }

    private void loadMainContainer(){
        try {
            VBox box = FXMLLoader.load(getClass().getResource("/fxml/drawer.fxml"));
            contentAnchorPane = FXMLLoader.load(getClass().getResource("/fxml/download_link.fxml"));

            drawer.setSidePane(box);
            drawer.setContent(contentAnchorPane);
            for (Node node : box.getChildren()) {
                if (node.getAccessibleText() != null) {
                    node.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                        switch (node.getAccessibleText()) {
                            case "download_video":
                                try {
                                    contentAnchorPane = FXMLLoader.load(getClass().getResource("/fxml/download_link.fxml"));
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                drawer.setContent(contentAnchorPane);

                                break;
                            case "download_history":
                                try {
                                    FlowPane pane = FXMLLoader.load(getClass().getResource("/fxml/downloaded_items.fxml"));
                                    drawer.setContent(pane);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                break;
                        }
                    });
                }
            }
            HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
            transition.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
                transition.setRate(transition.getRate() * -1);
                transition.play();
                if (drawer.isOpened()) {
                    drawer.close();
                } else {
                    drawer.open();
                }
            });
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    private void loadSplashScreen() {
        try {
            Launcher.isSplashLoaded = true;

            StackPane pane = FXMLLoader.load(getClass().getResource(("/fxml/splash.fxml")));
            anchorPane.getChildren().setAll(pane);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), pane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), pane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            fadeIn.play();

            fadeIn.setOnFinished((e) -> {
                fadeOut.play();
            });

            fadeOut.setOnFinished((e) -> {
                try {
                    AnchorPane parentContent = FXMLLoader.load(getClass().getResource("/fxml/container.fxml"));
                    anchorPane.getChildren().setAll(parentContent);
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
