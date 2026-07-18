module com.example.songs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires jaudiotagger;

    opens com.example.songs to javafx.fxml;
    exports com.example.songs;
}