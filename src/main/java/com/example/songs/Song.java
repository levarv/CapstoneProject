package com.example.songs;

import javafx.scene.media.Media;
import java.io.File;

public class Song {

    private String id;
    private String filePath;

    public Song() {
    }

    public Song(String id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    public Media getSong() {
        return new Media(new File(filePath).toURI().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}