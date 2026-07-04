package com.example.songs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;

public class MusicPlayerController {

    private MediaPlayer mediaPlayer;
    private Song currentSong;

    @FXML
    private Label songLabel;

    @FXML
    public void initialize() {

        // Test the Song object
        Song test = new Song("1", "C:/Music/Beat It.mp3");
        System.out.println(test.getSong());

        // Load the song into the player
        currentSong = test;
        mediaPlayer = new MediaPlayer(currentSong.getSong());

        songLabel.setText("Loaded: Beat It");
    }

    @FXML
    private void playSong() {
        mediaPlayer.play();
    }

    @FXML
    private void pauseSong() {
        mediaPlayer.pause();
    }

    @FXML
    private void stopSong() {
        mediaPlayer.stop();
    }

    @FXML
    private void loadBillieJean() {

        mediaPlayer.stop();

        currentSong = new Song("2", "C:/Music/Billie Jean.mp3");
        mediaPlayer = new MediaPlayer(currentSong.getSong());

        songLabel.setText("Loaded: Billie Jean");
    }
}