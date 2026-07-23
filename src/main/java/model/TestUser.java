package model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Random;

/*
    Test Class
 */
public class TestUser extends Thread {
    private String name;
    private boolean running = true;

    public TestUser(String name, ThreadGroup g) {
        super(g, name);
    }

    public void run() {
        Song[] test = {
                new Song(new File("src/main/resources/Home.mp3")),
                new Song(new File("src/main/resources/RumbleFish.mp3")),
                new Song(new File("src/main/resources/Spikes.mp3")),
                new Song(new File("src/main/resources/AquaTofana.mp3")),
                new Song(new File("src/main/resources/ClosetoHome.mp3"))
        };

        Timeline timeline = new Timeline("TENSECOND");
        Random rng = new Random();

        MagicSongPicker mpicker = new MagicSongPicker.Builder()
                .minutes(timeline)
                .arg1(.5)
                .arg2(.4)
                .build();


        int index = 0;
        while (running) {
            try {
                sleep(rng.nextLong(4000, 8000));

                test[index].setLastListen(LocalDateTime.now());
                timeline.add(test[index]);

                ++index;
                index = index % test.length;
            } catch (InterruptedException e) {
                interrupt();
            }
        }

        Main.writeToFile(
                timeline.getGenreCountMap(),
                timeline.getArtistCountMap(),
                timeline.getAverageBPM()
        );

        mpicker.SQLquery();

        System.out.println("Songs retained");
        System.out.println(timeline.getSongs().size());
    }

    @Override
    public void interrupt() {
        running = false;
    }
}
