package modelview;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.SQLQuery;
import model.SceneManager;

import java.net.URL;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws Exception {

        URL stylesheet = getClass().getResource("/style.css");

        if (stylesheet == null) {
            throw new IllegalStateException(
                    "style.css was not found in src/main/resources"
            );
        }

        // Playlist Scene
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Playlist.fxml")
        );

        Scene playlistScene = new Scene(loader.load());
        playlistScene.getStylesheets().add(stylesheet.toExternalForm());

        SceneManager.addScene(playlistScene, 1);


        // Library Scene
        loader = new FXMLLoader(
                getClass().getResource("/Library.fxml")
        );

        Scene libraryScene = new Scene(loader.load());
        libraryScene.getStylesheets().add(stylesheet.toExternalForm());

        SceneManager.addScene(libraryScene, 2);


        // Music Player Scene
        loader = new FXMLLoader(
                getClass().getResource("/hello-view.fxml")
        );

        Scene musicPlayerScene = new Scene(loader.load());
        musicPlayerScene.getStylesheets().add(stylesheet.toExternalForm());

        SceneManager.addScene(musicPlayerScene, 3);


        stage.setTitle("Teller");

        SceneManager.setStage(stage);

// Start on Library scene
        SceneManager.setScene(2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}