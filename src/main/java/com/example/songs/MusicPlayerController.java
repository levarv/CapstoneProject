package com.example.songs;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;




public class MusicPlayerController {

    private Song[] songs;
    private int currentIndex = 0;

    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private Song nextQueuedSong;


    private boolean shuffle = false;
    private boolean repeat = false;
    private boolean favorite = false;


    @FXML
    private Label songLabel;


    @FXML
    private ListView<Song> queueList;


    @FXML
    private Slider volumeSlider;



    @FXML
    private Label artistLabel;

    @FXML
    private Label nextSongLabel;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private Label totalTimeLabel;

    @FXML
    private Slider progressSlider;

    @FXML
    private Canvas visualizerCanvas;


    @FXML
    private Canvas starCanvas;



    @FXML
    public void initialize() {

        songs = new Song[]{

                new Song("C:/Music/Beat It.mp3"),
                new Song("C:/Music/Billie Jean.mp3")

        };


        setupQueue();


        drawStars();


        loadSong();

        volumeSlider.valueProperty()
                .addListener((obs,oldValue,newValue)->{

                    if(mediaPlayer != null){

                        mediaPlayer.setVolume(
                                newValue.doubleValue()/100
                        );

                    }

                });

    }




    private void loadSong(){


        if(mediaPlayer != null){

            mediaPlayer.stop();

            mediaPlayer.dispose();

        }


        currentSong = songs[currentIndex];

        mediaPlayer = new MediaPlayer(
                currentSong.getSong()
        );


        updateSongInfo();

        updateQueue();

        setupSlider();

        setupVisualizer();

        setNextSongListener();

        queueList.getSelectionModel()
                .select(currentIndex);
    }





    private void updateSongInfo(){

        songLabel.setText(
                currentSong.getTitle()
        );


        artistLabel.setText(
                currentSong.getArtist()
        );

    }





    private void updateQueue(){

        int nextIndex = currentIndex + 1;


        if(nextIndex >= songs.length){

            nextIndex = 0;

        }


        nextQueuedSong = songs[nextIndex];


        if(nextSongLabel != null){

            nextSongLabel.setText(
                    "Up Next: " + nextQueuedSong.getTitle()
            );

        }

    }





    private void setupSlider(){


        mediaPlayer.setOnReady(() -> {


            progressSlider.setMax(
                    mediaPlayer.getTotalDuration().toSeconds()
            );


            totalTimeLabel.setText(
                    formatTime(mediaPlayer.getTotalDuration())
            );


        });



        mediaPlayer.currentTimeProperty()
                .addListener((obs,oldTime,newTime)->{


                    if(!progressSlider.isValueChanging()){

                        progressSlider.setValue(
                                newTime.toSeconds()
                        );

                    }


                    currentTimeLabel.setText(
                            formatTime(newTime)
                    );


                });



        progressSlider.valueChangingProperty()
                .addListener((obs,oldVal,newVal)->{


                    if(!newVal){

                        mediaPlayer.seek(
                                Duration.seconds(
                                        progressSlider.getValue()
                                )
                        );

                    }

                });


    }





    @FXML
    private void playSong(){

        mediaPlayer.play();

    }



    @FXML
    private void pauseSong(){

        mediaPlayer.pause();

    }

    @FXML
    private Button shuffleButton;

    @FXML
    private Button repeatButton;




    @FXML
    private void stopSong(){

        mediaPlayer.stop();

    }





    @FXML
    private void nextSong(){

        if(shuffle){

            int randomIndex;

            do {
                randomIndex = (int)(Math.random() * songs.length);
            }
            while(randomIndex == currentIndex && songs.length > 1);

            currentIndex = randomIndex;

        }
        else{

            currentIndex++;

            if(currentIndex >= songs.length){
                currentIndex = 0;
            }

        }

        loadSong();

        mediaPlayer.play();

    }





    @FXML
    private void previousSong(){


        currentIndex--;


        if(currentIndex < 0){

            currentIndex = songs.length-1;

        }


        loadSong();


        mediaPlayer.play();


    }





    private void setNextSongListener(){


        mediaPlayer.setOnEndOfMedia(() -> {

            if(repeat){

                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();

            }
            else{

                nextSong();

            }

        });


    }







    private void setupVisualizer(){


        GraphicsContext gc =
                visualizerCanvas.getGraphicsContext2D();



        mediaPlayer.setAudioSpectrumNumBands(40);

        mediaPlayer.setAudioSpectrumInterval(0.04);



        mediaPlayer.setAudioSpectrumListener(
                (timestamp,duration,magnitudes,phases)->{


                    double width =
                            visualizerCanvas.getWidth();


                    double height =
                            visualizerCanvas.getHeight();



                    gc.clearRect(
                            0,
                            0,
                            width,
                            height
                    );



                    double barWidth =
                            width / magnitudes.length;



                    for(int i=0;i<magnitudes.length;i++){



                        double level =
                                magnitudes[i]+60;



                        double barHeight =
                                (level/60)*height;



                        if(barHeight < 5){

                            barHeight = 5;

                        }



                        gc.setFill(
                                Color.web("#ff0093")
                        );



                        gc.fillRoundRect(

                                i * barWidth,

                                (height-barHeight)/2,

                                barWidth-3,

                                barHeight,

                                10,

                                10
                        );


                    }


                });


    }





    //FOrmat time method
    private String formatTime(Duration duration){


        int minutes =
                (int)duration.toMinutes();


        int seconds =
                (int)duration.toSeconds()%60;



        return String.format(
                "%d:%02d",
                minutes,
                seconds
        );


    }
    //draw stars method

    private void drawStars(){


        GraphicsContext gc =
                starCanvas.getGraphicsContext2D();



        gc.clearRect(
                0,
                0,
                starCanvas.getWidth(),
                starCanvas.getHeight()
        );



        double[][] stars = {

                {60,80},
                {120,180},
                {80,500},

                {800,90},
                {750,250},
                {820,520},

                {450,70}

        };



        for(double[] star : stars){


            drawFourPointStar(
                    gc,
                    star[0],
                    star[1],
                    12
            );


        }


    }



    //draw star method

    private void drawFourPointStar(GraphicsContext gc,
                                   double x,
                                   double y,
                                   double size){


        gc.setFill(Color.WHITE);


        gc.beginPath();


        gc.moveTo(x, y-size);


        gc.lineTo(x+size/3, y-size/3);


        gc.lineTo(x+size, y);


        gc.lineTo(x+size/3, y+size/3);


        gc.lineTo(x, y+size);


        gc.lineTo(x-size/3, y+size/3);


        gc.lineTo(x-size, y);


        gc.lineTo(x-size/3, y-size/3);


        gc.closePath();


        gc.fill();


    }

    //Setup Queue
    private void setupQueue(){

        queueList.getItems().clear();

        queueList.getItems().addAll(songs);

        queueList.setOnMouseClicked(event -> {

            int index = queueList.getSelectionModel().getSelectedIndex();

            if(index >= 0){

                currentIndex = index;

                loadSong();

                mediaPlayer.play();

            }

        });

    }
    //shuffle songs method
    @FXML
    private void shuffleSong(){

        shuffle = !shuffle;

        if(shuffle){

            shuffleButton.getStyleClass()
                    .add("active-button");

        }
        else{

            shuffleButton.getStyleClass()
                    .remove("active-button");

        }

    }
    //repeat song method
    @FXML
    private void repeatSong(){

        repeat = !repeat;

        if(repeat){

            repeatButton.getStyleClass()
                    .add("active-button");

        }
        else{

            repeatButton.getStyleClass()
                    .remove("active-button");

        }

    }
    //favorite song method
    @FXML
    private void favoriteSong(){

        favorite = !favorite;

        if(favorite){
            System.out.println("Added to Favorites");
        }
        else{
            System.out.println("Removed from Favorites");
        }

    }

    //Library, playlists, home, and settings methods
    @FXML
    private void openLibrary(){
        System.out.println("Library clicked");
    }

    @FXML
    private void openPlaylists(){
        System.out.println("Playlists clicked");
    }

    @FXML
    private void openHome(){
        System.out.println("Home clicked");
    }

    @FXML
    private void openSettings(){
        System.out.println("Settings clicked");
    }
}