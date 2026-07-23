package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.SQLQuery;
import model.SceneManager;
import model.Song;

public class LibraryController {

    @FXML
    private ListView<Song> libraryList;

    private final ObservableList<Song> librarySongs =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        libraryList.setItems(librarySongs);
        loadSongs();
    }

    private void loadSongs() {
        librarySongs.setAll(SQLQuery.getSongs());
    }

    public ObservableList<Song> getLibrarySongs() {
        return librarySongs;
    }

    @FXML
    private void goHome() {
        SceneManager.setScene(3);
    }

    @FXML
    private void goPlaylist() {
        SceneManager.setScene(1);
    }




}