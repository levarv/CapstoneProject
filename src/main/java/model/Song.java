package model;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Song implements Scorable {

    private String genre; //genre of song
    private String name;  //name of song
    private String artist;  //artist of song
    private int bpm;  //bpm of song
    private Media media; //java fx media object to be accepted by player
    private LocalDateTime lastListen;
    private URL url;

    /**
     * constructor from database
     * @param name   name of song
     * @param bpm    bpm attribute
     * @param genre  genre category
     * @param artist artist name
     * @param file   mp3 test file
     */
    public Song(String name, int bpm, String genre, String artist, File file) {
        this.name = name;
        this.bpm = bpm;
        this.genre = genre;
        this.artist = artist;
        this.media = new Media(file.getPath());
    }

    /**
     * constructor from file picker
     * @param file mp3 test file
     */
    public Song(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Song file does not exist.");
        }

        String fileName = file.getName();
        String lowerName = fileName.toLowerCase();

        this.name = fileName.replaceFirst("\\.[^.]+$", "");
        this.artist = "Unknown Artist";
        this.genre = "Unknown";
        this.bpm = -1;

        if (lowerName.endsWith(".mp3")) {
            try {
                Mp3File mp3File = new Mp3File(file);

                if (mp3File.hasId3v2Tag()) {
                    String title = mp3File.getId3v2Tag().getTitle();
                    String tagArtist = mp3File.getId3v2Tag().getArtist();
                    String tagGenre = mp3File.getId3v2Tag().getGenreDescription();

                    if (title != null && !title.isBlank()) this.name = title;
                    if (tagArtist != null && !tagArtist.isBlank()) this.artist = tagArtist;
                    if (tagGenre != null && !tagGenre.isBlank()) this.genre = tagGenre;

                    this.bpm = mp3File.getId3v2Tag().getBPM();
                } else if (mp3File.hasId3v1Tag()) {
                    String title = mp3File.getId3v1Tag().getTitle();
                    String tagArtist = mp3File.getId3v1Tag().getArtist();
                    String tagGenre = mp3File.getId3v1Tag().getGenreDescription();

                    if (title != null && !title.isBlank()) this.name = title;
                    if (tagArtist != null && !tagArtist.isBlank()) this.artist = tagArtist;
                    if (tagGenre != null && !tagGenre.isBlank()) this.genre = tagGenre;
                }

            } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                System.err.println("Could not read MP3 metadata: " + file.getName());
            }
        }

        try {
            this.url = file.toURI().toURL();
            this.media = new Media(file.toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not load song: " + file.getAbsolutePath(), e);
        }
    }
    /**
     * genreScore
     * @param t Timeline to derive score
     * @return n/N where:
     * n = number of times this song's genre appeared in Timeline t
     * N = number of times any genre appeared in Timeline t
     */
    @Override
    public double genreScore(Timeline t) {
        Map<String, Integer> map = t.getGenreCountMap();

        Integer thisCount = map.get(this.genre);
        Integer topCount = map.get(t.topGenre());

        if (thisCount != null && topCount != null)
            return (double) thisCount / topCount;
        else
            return -1.0;
    }

    /**
     * BPMScore
     * @param t Timeline to derive score
     * @return this song's error relative to the average bpm
     * (relative error is similar to percent error)
     */
    @Override
    public double BPMScore(Timeline t) {
        Integer avgBpm = t.averageBPM();

        if (avgBpm != null && this.bpm != 0)
            return Math.abs(t.averageBPM() - this.bpm) / t.averageBPM();
        else
            return -1.0;
    }

    /**
     * artistScore
     * @param t Timeline t
     * @return n/N where:
     * n = number of times this song's artist appeared in Timeline t
     * N = number of times any artist appeared in Timeline t
     */
    @Override
    public double artistScore(Timeline t) {
        Map<String, Integer> map = t.getArtistCountMap();

        Integer thisCount = map.get(this.artist);
        Integer topCount = map.get(t.mostPopularArtist());

        if (thisCount != null && topCount != null)
            return (double) thisCount / topCount;
        else
            return -1.0;
    }

    /**
     * setMedia
     * @param media to be set
     */
    public void setMedia(Media media) {
        this.media = media;
    }

    /**
     * getGenre
     * @return genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * getName
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getArtist
     * @return artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * getBpm
     * @return bpm
     */
    public int getBpm() {
        return bpm;
    }

    /**
     * getLastListen
     * @return lastListen
     */
    public LocalDateTime getLastListen() { return lastListen; }

    /**
     * getUrl
     * @return url
     */
    public URL getUrl() { return url; }

    /**
     * setLastListen
     * @param lastListen
     */
    public void setLastListen(LocalDateTime lastListen) { this.lastListen = lastListen; }

    /**
     * toString
     * @return String representation of this Song
     */
    @Override
    public String toString() {
        return "model.Song{" +
                "genre=" + genre +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", bpm=" + bpm +
                ", lastListen=" + lastListen +
                '}';
    }
}
