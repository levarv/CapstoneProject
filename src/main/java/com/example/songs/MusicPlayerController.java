package com.example.songs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class MusicPlayerController {

    private Song[] songs;
    private int currentIndex = 0;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private Song nextQueuedSong;


    @FXML
    private Label songLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private Slider progressSlider;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private Label totalTimeLabel;


    @FXML
    public void initialize() {

        songs = new Song[] {
                new Song("C:/Music/Beat It.mp3"),
                new Song("C:/Music/Billie Jean.mp3")
        };


        currentSong = songs[currentIndex];

        updateQueue();

        mediaPlayer = new MediaPlayer(currentSong.getSong());

        setNextSongListener();

        updateSongInfo();

        setupSlider();
        setupVisualizer();
    }



    private void updateSongInfo() {

        songLabel.setText(currentSong.getTitle());

        artistLabel.setText(currentSong.getArtist());
    }



    private void setupSlider() {

        mediaPlayer.setOnReady(() -> {

            progressSlider.setMax(
                    mediaPlayer.getTotalDuration().toSeconds()
            );

            totalTimeLabel.setText(
                    formatTime(mediaPlayer.getTotalDuration())
            );
        });


        mediaPlayer.currentTimeProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!progressSlider.isValueChanging()) {
                        progressSlider.setValue(newValue.toSeconds());
                    }

                    currentTimeLabel.setText(
                            formatTime(newValue)
                    );
                });


        progressSlider.valueChangingProperty().addListener(
                (obs, wasChanging, isChanging) -> {

                    if (!isChanging) {

                        mediaPlayer.seek(
                                Duration.seconds(progressSlider.getValue())
                        );
                    }
                });
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
    private Label nextSongLabel;


    @FXML
    private void nextSong() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }


        currentIndex++;

        if (currentIndex >= songs.length) {
            currentIndex = 0;
        }


        currentSong = songs[currentIndex];


        mediaPlayer = new MediaPlayer(currentSong.getSong());


        updateSongInfo();


        setupSlider();


        setupVisualizer();   // <-- ADD THIS HERE


        mediaPlayer.play();
    }

    @FXML
    private Canvas visualizerCanvas;


    @FXML
    private void previousSong() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }


        currentIndex--;

        if (currentIndex < 0) {
            currentIndex = songs.length - 1;
        }


        currentSong = songs[currentIndex];

        mediaPlayer = new MediaPlayer(currentSong.getSong());


        updateQueue();

        setNextSongListener();

        updateSongInfo();

        setupSlider();

        mediaPlayer.play();
    }



    // Finds the next song in queue
    private void updateQueue() {

        int nextIndex = currentIndex + 1;

        if (nextIndex >= songs.length) {
            nextIndex = 0;
        }

        nextQueuedSong = songs[nextIndex];

        nextSongLabel.setText(
                "Up Next: " + nextQueuedSong.getTitle()
        );
    }



    // Automatically play next song when current finishes
    private void setNextSongListener() {

        mediaPlayer.setOnEndOfMedia(() -> {

            nextSong();

        });
    }



    private String formatTime(Duration duration) {

        int minutes = (int) duration.toMinutes();

        int seconds = (int) duration.toSeconds() % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    //Visualizer setup()
    private void setupVisualizer() {

        GraphicsContext gc = visualizerCanvas.getGraphicsContext2D();

        mediaPlayer.setAudioSpectrumNumBands(20);
        mediaPlayer.setAudioSpectrumInterval(0.04);


        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {


            double width = visualizerCanvas.getWidth();
            double height = visualizerCanvas.getHeight();


            gc.clearRect(0, 0, width, height);


            double barWidth = width / magnitudes.length;


            for (int i = 0; i < magnitudes.length; i++) {


                // Convert JavaFX negative values into usable height
                double level = magnitudes[i] + 60;

                double barHeight = (level / 60) * height;


                if(barHeight < 5) {
                    barHeight = 5;
                }


                double x = i * barWidth;

                double y = (height - barHeight) / 2;


                gc.setFill(
                        javafx.scene.paint.Color.web("#ff0093")
                );


                gc.fillRoundRect(
                        x,
                        y,
                        barWidth - 4,
                        barHeight,
                        10,
                        10
                );

            }

        });
    }
}