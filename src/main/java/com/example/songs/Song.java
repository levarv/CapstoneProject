package com.example.songs;

import javafx.scene.media.Media;
import java.io.File;

public class Song {

    private final String filePath;
    private String title;
    private String artist;

    public Song(String filePath) {
        this.filePath = filePath;
        loadSongInfo();
    }

    private void loadSongInfo() {
        File file = new File(filePath);

        title = file.getName()
                .replace(".mp3", "")
                .replace(".MP3", "");

        artist = "Unknown Artist";

        if (title.equalsIgnoreCase("Beat It") ||
                title.equalsIgnoreCase("Billie Jean")) {
            artist = "Michael Jackson";
        }
    }

    public Media getSong() {
        return new Media(filePathToURI());
    }

    private String filePathToURI() {
        return new File(filePath).toURI().toString();
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilePath() {
        return filePath;
    }

    // Keeps old controller code working
    public String getPath() {
        return filePath;
    }

    //make song display in listview
    @Override
    public String toString(){

        return title;

    }

}