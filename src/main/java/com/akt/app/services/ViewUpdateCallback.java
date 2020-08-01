package com.akt.app.services;

import java.io.IOException;

public interface ViewUpdateCallback {
    void updateColor(String newColor);
    void updateView() throws IOException;
}
