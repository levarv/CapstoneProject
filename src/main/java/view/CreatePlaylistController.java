package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.util.ArrayList;


import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Playlist;
import model.Song;

import javafx.scene.control.ListCell;
//import javafx.scene.control.SelectionMode;

public class CreatePlaylistController {
    @FXML
    private TextField playlistNameField;


    @FXML
    private ListView<Song> songListView;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label selectedImageLabel;

    private PlaylistController playlistController;
    private File selectedImageFile;

    // File Chooser method for image cover - RD
    @FXML
    private void chooseImage() {

        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File file = chooser.showOpenDialog(
                chooseImageButton.getScene().getWindow()
        );

        if (file != null) {

            selectedImageFile = file;

            selectedImageLabel.setText(file.getName());

        }

    }

    // Setter to pass PlaylistController into CreatePlaylist - RD
    public void setPlaylistController(PlaylistController playlistController) {
        this.playlistController = playlistController;
    }

    // Creates new Playlist to then be passed back to PlaylistController
    @FXML
    private void createPlaylist() {

        String playlistName = playlistNameField.getText().trim();

        if (playlistName.isEmpty()) {
            return;
        }

        // Create list of selected songs from multi-select - RD
        ArrayList<Song> selectedSongs = new ArrayList<>(
                songListView.getItems()
        );

        // Create string to hold coverImagePath - RD
        String coverImagePath = null;

        // If selectedImageFile is populated, assign image location to coverImagePath - RD
        if (selectedImageFile != null) {
            coverImagePath = selectedImageFile.toURI().toString();
        }

        // Creates new Playlist using user-entered parameters - RD
        Playlist newPlaylist = new Playlist(
                playlistName,
                selectedSongs,
                coverImagePath,
                false
        );

        // Pass newly created playlist to playlistController - RD
        playlistController.addPlaylist(newPlaylist);

        // Close after creation succeeds - RD
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {

        /*songListView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );*/


        songListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);

                if (empty || song == null) {
                    setText(null);
                } else {
                    setText(song.getName());
                }
            }
        });
    }

    //import song method
    @FXML
    private void importSong() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Choose Audio Files");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Audio Files",
                        "*.mp3",
                        "*.wav",
                        "*.au"
                )
        );

        java.util.List<File> selectedFiles =
                fileChooser.showOpenMultipleDialog(
                        songListView.getScene().getWindow()
                );

        if (selectedFiles == null || selectedFiles.isEmpty()) {
            return;
        }

        for (File file : selectedFiles) {
            try {
                Song song = new Song(file);
                songListView.getItems().add(song);

            } catch (RuntimeException exception) {
                System.err.println(
                        "Could not import: " + file.getName()
                );

                exception.printStackTrace();
            }
        }
    }
}
