package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Main;
import model.Playlist;
import model.Song;
import model.SceneManager;
import modelview.MusicPlayerController;

import java.util.Objects;

public class PlaylistDetailsController {

    @FXML
    private Label playlistNameLabel;

    @FXML
    private ImageView playlistCoverImageView;

    @FXML
    private ListView<Song> playlistSongsList;

    @FXML
    private Button playButton;

    @FXML
    private Button addToQueueButton;

    @FXML
    private Button deletePlaylistButton;

    private Playlist playlist;
    private PlaylistController playlistController;

    // Stores a record of detailsStage for closing using controller - RD
    private Stage detailsStage;

    // Receive secondary stage for details from PlaylistController - RD
    public void setDetailsStage(Stage detailsStage) {
        this.detailsStage = detailsStage;
    }

    // Sets playlist and its attributes - RD
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;

        playlistNameLabel.setText(playlist.getName());

        playlistSongsList.getItems().setAll(
                playlist.getSongs()
        );

        playlistSongsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                // If text is null, set song name is empty - RD
                if (empty || song == null) {
                    setText(null);
                } else {
                    setText(song.getName());
                }
            }
        });

        loadCoverImage(); // Load existing image - RD
    }

    public void setPlaylistController(PlaylistController playlistController) {
        this.playlistController = playlistController;
    }

    // Checks for and Loads the Cover Image associated with Playlist if there is one - RD
    private void loadCoverImage() {
        String imagePath = playlist.getCoverImagePath();

        if (imagePath == null) {
            playlistCoverImageView.setImage(null);
            return;
        }

        Image image;

        if (imagePath.startsWith("file:")) {
            image = new Image(imagePath);
        } else {
            image = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream(imagePath)
                    )
            );
        }

        playlistCoverImageView.setImage(image);
    }

    // For showing any messages produced by buttons - RD
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (detailsStage != null) {
            alert.initOwner(detailsStage);
        }

        alert.showAndWait();
    }

    // Play Playlist button
    // Functionality pending based on player implementation - RD
    @FXML
    private void playPlaylist() {
        if (playlist == null || playlist.getSongs().isEmpty()) {
            showMessage(
                    "Play Playlist",
                    "This playlist does not contain any songs."
            );
            return;
        }

        Main.clearQueue();
        Main.addPlaylistToQueue(playlist);

        if (detailsStage != null) {
            detailsStage.close();
        }

        SceneManager.setScene(3);
        MusicPlayerController.refreshPlayer();
    }
    @FXML
    private void addToQueue() {
        if (playlist == null || playlist.getSongs().isEmpty()) {
            showMessage(
                    "Add to Queue",
                    "This playlist does not contain any songs."
            );
            return;
        }

        Main.addPlaylistToQueue(playlist);
        MusicPlayerController.refreshPlayer();

        showMessage(
                "Added to Queue",
                "\"" + playlist.getName() +
                        "\" was added to the queue.\n\n" +
                        "Queue now contains " +
                        Main.getQueueSize() +
                        " songs."
        );
    }
    /*
     * Delete Playlist button - RD
     * Deletes playlist and removes from playlist tiles - RD
     * Database implementation needed for saving playlists + deleting playlists - RD
     */
    @FXML
    private void deletePlaylist(ActionEvent event) {

        // Check if playlist exists and connection to control exists before attempting deletion - RD
        if (playlist == null || playlistController == null) {
            showMessage(
                    "Delete Failed",
                    "The playlist could not be deleted."
            );
            return;
        }

        // Get playlist name to be displayed in confirmation - RD
        String playlistName = playlist.getName();

        // Call function to delete the playlist - RD
        playlistController.deletePlaylist(playlist);

        // Confirm playlist deletion to user - RD
        showMessage(
                "Playlist Deleted",
                "\"" + playlistName
                        + "\" was deleted successfully."
        );

        // Closes details stage after deletion - RD
        if (detailsStage != null) {
            detailsStage.close();
        }
    }
}