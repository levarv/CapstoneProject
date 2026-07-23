package modelview;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import model.Main;
import model.SQLQuery;
import model.Song;
import javafx.scene.control.ListCell;

import model.SceneManager;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class MusicPlayerController {

    private static MusicPlayerController instance;
    public Text TTtext;

    private int currentIndex = 0;

    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private Song nextQueuedSong;

    private Song mPick;
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

        instance = this;

        setupQueue();
        drawStars();

        volumeSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(
                                newValue.doubleValue() / 100.0
                        );
                    }
                }
        );

        if (!Main.getPlaybackQueue().isEmpty()) {
            currentIndex = 0;
            loadSong();
        } else {
            showEmptyQueue();
        }
    }




    private void loadSong() {

        if (Main.getPlaybackQueue().isEmpty()) {
            showEmptyQueue();
            return;
        }

        if (
                currentIndex < 0 ||
                        currentIndex >= Main.getPlaybackQueue().size()
        ) {
            currentIndex = 0;
        }

        Song selectedSong =
                Main.getPlaybackQueue().get(currentIndex);

        if (selectedSong == null || selectedSong.getUrl() == null) {
            System.err.println(
                    "Cannot play song at queue index " + currentIndex
            );

            showEmptyQueue();
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        currentSong = selectedSong;

        Media media = new Media(
                currentSong.getUrl().toExternalForm()
        );

        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setVolume(
                volumeSlider.getValue() / 100.0
        );

        updateSongInfo();
        updateQueue();
        setupSlider();
        setupVisualizer();
        setNextSongListener();

        queueList.getSelectionModel().select(currentIndex);

        queueList.refresh();

        mediaPlayer.setOnError(() -> {
            System.err.println(
                    "Media player error: " +
                            mediaPlayer.getError()
            );
        });
    }

    public void printToTextBox() {
        String q = Main.getSongPicker().SQLquery();
        System.out.println(q);
        ArrayList<Song> s = SQLQuery.getSongs(q);

        //try to query a relevant subset 5 or fewer times
        for (int i = 0; q == null &&
                i < 5 &&
                !Main.getPlaybackQueue().isEmpty(); ++i) {

            q = Main.getSongPicker().SQLquery();
            s = SQLQuery.getSongs(q);
        }

        if (!s.isEmpty()) {
            TTtext.setText(s.getFirst().getName());
            mPick = s.getFirst();
        }
        else { TTtext.setText(""); System.out.println("empty query"); }
    }


    private void updateSongInfo(){

        songLabel.setText(
                currentSong.getName()
        );

        artistLabel.setText(
                currentSong.getArtist()
        );

        //song is added to M.S.P timeline
        Main.getTimeline().add(currentSong);
        //setTheLastListen of song
        currentSong.setLastListen(LocalDateTime.now());
        //text corresponding to the song chosen is generated
        printToTextBox();
    }





    private void updateQueue() {

        if (Main.getPlaybackQueue().isEmpty()) {
            nextQueuedSong = null;

            if (nextSongLabel != null) {
                nextSongLabel.setText("Up Next: None");
            }

            return;
        }

        int nextIndex = currentIndex + 1;

        if (nextIndex >= Main.getPlaybackQueue().size()) {
            nextIndex = 0;
        }

        nextQueuedSong =
                Main.getPlaybackQueue().get(nextIndex);

        if (nextSongLabel != null && nextQueuedSong != null) {
            nextSongLabel.setText(
                    "Up Next: " + nextQueuedSong.getName()
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
    private void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }




    @FXML
    private void pauseSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    private Button shuffleButton;

    @FXML
    private Button repeatButton;




    @FXML
    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }





    @FXML
    private void nextSong() {

        if (Main.getPlaybackQueue().isEmpty()) {
            showEmptyQueue();
            return;
        }

        if (shuffle) {

            int randomIndex;

            do {
                randomIndex =
                        (int) (Math.random() *
                                Main.getPlaybackQueue().size());

            } while (
                    randomIndex == currentIndex &&
                            Main.getPlaybackQueue().size() > 1
            );

            currentIndex = randomIndex;

        } else {

            currentIndex++;

            if (currentIndex >= Main.getPlaybackQueue().size()) {
                currentIndex = 0;
            }
        }

        loadSong();

        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }




    @FXML
    private void previousSong() {

        if (Main.getPlaybackQueue().isEmpty()) {
            showEmptyQueue();
            return;
        }

        currentIndex--;

        if (currentIndex < 0) {
            currentIndex =
                    Main.getPlaybackQueue().size() - 1;
        }

        loadSong();

        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
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
    private void setupQueue() {
        queueList.getItems().setAll(Main.getPlaybackQueue());

        queueList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);

                if (empty || song == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(song.getName());

                if (getIndex() == currentIndex) {
                    setStyle(
                            "-fx-text-fill: #ff1493;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-color: rgba(255, 20, 147, 0.18);" +
                                    "-fx-border-color: #ff1493;" +
                                    "-fx-border-radius: 8;" +
                                    "-fx-background-radius: 8;"
                    );
                } else {
                    setStyle(
                            "-fx-text-fill: white;" +
                                    "-fx-background-color: transparent;"
                    );
                }
            }
        });

        queueList.setOnMouseClicked(event -> {
            int index = queueList.getSelectionModel().getSelectedIndex();

            if (index >= 0 && index < Main.getPlaybackQueue().size()) {
                currentIndex = index;
                loadSong();

                if (mediaPlayer != null) {
                    mediaPlayer.play();
                }
            }
        });

        queueList.refresh();
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

    //Check empty queue method
    private void showEmptyQueue() {

        songLabel.setText("No Song");
        artistLabel.setText("Queue is empty");
        currentTimeLabel.setText("0:00");
        totalTimeLabel.setText("0:00");

        progressSlider.setValue(0);
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

    //Refresh Queue method lets the project refresh the player after adding a playlist:
    public void refreshQueue() {

        setupQueue();

        if (Main.getPlaybackQueue().isEmpty()) {

            currentIndex = 0;

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }

            showEmptyQueue();
            return;
        }

        if (currentIndex >= Main.getPlaybackQueue().size()) {
            currentIndex = 0;
        }

        loadSong();
    }
    //refreshqueue 2
    public static void refreshPlayer() {
        if (instance != null) {
            instance.refreshQueue();
        }
    }

    //menu methods

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

    @FXML
    private void openMenu() {
        SceneManager.setScene(1);
    }
    @FXML
    public void addTTSong(ActionEvent actionEvent) {
        if (mPick != null)
            Main.getPlaybackQueue().add(mPick);
    }
}
