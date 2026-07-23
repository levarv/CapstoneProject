package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Main {

    private static final HashMap<String, Integer> genreCountMap = new HashMap<>();
    private static final HashMap<String, Integer> artistCountMap = new HashMap<>();
    private static double averageBPM = 0.0;
    private static final ArrayList<Song> playbackQueue = new ArrayList<>(); // Temporary Queue for functionality in Playlist Scene - RD
    private static final File userDir = new File("src/main/resources/songs");
    private static String timelineType = "HOUR";
    private static MagicSongPicker mpicker;
    private static Timeline t;

    /**
     * writes the map entries to a text file
     * @param genreMap
     * @param artistMap
     * @param averageBpm
     */
    public static void writeToFile(Map<String,Integer> genreMap, Map<String,Integer> artistMap, double averageBpm) {
        File file = new File("mapsAndData.txt");
        try (FileWriter writer = new FileWriter(file,false)) {
            for (Map.Entry<String, Integer> entry : genreMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
            writer.write("<EOD>" + "\n");

            for (Map.Entry<String, Integer> entry : artistMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
            writer.write("<EOD>" + "\n");

            writer.write(averageBpm + "\n");
            writer.write("<EOD>" + "\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * readFromFile
     * instantiates the maps from a text file.
     * @param clearCount will replace all values with zero
     */
    public static void readFromFile(boolean clearCount){
        File file = new File("mapsAndData.txt");
        String[] data = null;
        try {
            Scanner scan = new Scanner(file);

            while (!scan.hasNext("<EOD>")) {
                data = scan.nextLine().split("=");
                genreCountMap.put(data[0], clearCount ? 0 : Integer.parseInt(data[1]));
            }
            scan.nextLine();

            while (!scan.hasNext("<EOD>")) {
                data = scan.nextLine().split("=");
                artistCountMap.put(data[0], clearCount ? 0 : Integer.parseInt(data[1]));
            }
            scan.nextLine();

            while (!scan.hasNext("<EOD>"))
                averageBPM = Double.parseDouble(scan.nextLine());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static MagicSongPicker getSongPicker() { return mpicker; }

    public static Timeline getTimeline() { return t; }

    /**
     * getGenreCountMap
     * @return genreCountMap
     */
    public static HashMap<String, Integer> getGenreCountMap() {
        return genreCountMap;
    }

    /**
     * getArtistCountMap
     * @return artistCountMap
     */
    public static HashMap<String, Integer> getArtistCountMap() {
        return artistCountMap;
    }

    /**
     * getAverageBPM
     * @return averageBPM
     */
    public static Double getAverageBPM() {
        return averageBPM;
    }

    /**
     * Temporary Queue functionality for Playlist functionality - RD
     * Adds every song from a playlist to the playback queue
     * @param playlist playlist being added to the queue
     */
    public static void addPlaylistToQueue(Playlist playlist) {
        playbackQueue.addAll(playlist.getSongs());
    }

    /**
     * Temporary Queue functionality for Playlist functionality - RD
     * Returns the current playback queue
     * @return songs currently waiting in the queue
     */
    public static ArrayList<Song> getPlaybackQueue() {
        return playbackQueue;
    }

    /**
     * Temporary Queue functionality for Playlist functionality - RD
     * Returns the number of songs currently in the queue
     * @return queue size
     */
    public static int getQueueSize() {
        return playbackQueue.size();
    }

    /**
     * Temporary Queue functionality for Playlist functionality - RD
     * Removes every song from the queue.
     */
    public static void clearQueue() {
        playbackQueue.clear();
    }

    /**
     * loadSongsFromUserRepo
     * loads music from the user repository into the
     * relational db.
     */
    public static void loadSongsFromUserRepo() {
        File[] listOfMP3s = userDir.listFiles();
        Song tbAdded = null;
        for (File mp3 : listOfMP3s) {
            try {
                if (mp3.toURI().toURL().toString().matches(".*\\.mp3")) {
                    tbAdded = new Song(mp3);

                    String update = String.format(
                            "INSERT INTO Song (Artist,Name,BPM,ReleaseDate,Genre,ImageURL,SongURL)" +
                            " VALUES ('%s', '%s', %s, '%s', '%s','%s','%s')",
                            tbAdded.getArtist(),
                            tbAdded.getName(),
                            tbAdded.getBpm(),
                            "NULL",
                            tbAdded.getGenre(),
                            "NULL",
                            mp3.getPath()
                    );

                    System.out.println(update);
                    SQLQuery.update(update);

                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        SQLQuery.update("DELETE FROM Song");
        loadSongsFromUserRepo();

        t = new Timeline("HOUR");

       mpicker = new MagicSongPicker.Builder()
                .minutes(t)
                .arg1(.5)
                .arg2(.4)
                .build();

        Application.main(args);
    }
}
