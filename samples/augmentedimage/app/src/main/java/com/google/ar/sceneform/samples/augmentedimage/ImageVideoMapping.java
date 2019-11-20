package com.google.ar.sceneform.samples.augmentedimage;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ImageVideoMapping {
    private static ImageVideoMapping instance = null;
    private static Map<String, String> imageVideoMap;

    private ImageVideoMapping() {
        imageVideoMap = new HashMap<>();
    }

    public static ImageVideoMapping getInstance() {
        if (instance == null) {
            instance = new ImageVideoMapping();
        }
        return instance;
    }

    public void addImageVideo(@NonNull String imageURI, @NonNull String videoURI) {
        imageVideoMap.put(imageURI, videoURI);
    }

    public void printMap() {
        for (Map.Entry<String, String> entry : imageVideoMap.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
    }

    public Map<String, String> getImageVideoMap() {
        return imageVideoMap;
    }
}
