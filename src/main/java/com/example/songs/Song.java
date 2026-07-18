package com.example.songs;

import javafx.scene.media.Media;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class Song {

    private String filePath;
    private String title;
    private String artist;


    public Song(String filePath) {

        this.filePath = filePath;

        readMetadata();
    }


    private void readMetadata() {

        try {

            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            Tag tag = audioFile.getTag();

            if (tag != null) {

                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.ARTIST);

            }


        } catch (Exception e) {

            title = "Unknown Song";
            artist = "Unknown Artist";

        }
    }


    public Media getSong() {

        return new Media(
                new File(filePath).toURI().toString()
        );

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
}