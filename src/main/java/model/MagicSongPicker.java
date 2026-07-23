package model;

import java.util.*;

public class MagicSongPicker {
    private Timeline minutes;
    private Timeline hours;
    private Timeline days;
    private Timeline weeks;
    private double arg1;  //0 - 1
    private double arg2;  //0 - 1
    private double arg3;

    private MagicSongPicker(Builder b) {
        minutes = b.minutes;
        hours = b.hours;
        days = b.days;
        weeks = b.weeks;
        arg1 = b.arg1;  //0 - 1
        arg2 = b.arg2;  //0 - 1
        arg3 = b.arg3;
    }
        public static class Builder {
            private Timeline minutes;
            private Timeline hours;
            private Timeline days;
            private Timeline weeks;
            private double arg1;  //0 - 1
            private double arg2;  //0 - 1
            private double arg3;

            public Builder() { arg1 = .7; arg2 = .8; }

            public Builder minutes(Timeline t) { this.minutes = t; return this; }

            public Builder hours(Timeline t) { this.hours = t; return this; }

            public Builder weeks(Timeline t) { this.days = t; return this; }

            public Builder days(Timeline t) { this.weeks = t; return this; }

            public Builder arg1(double d) { arg1 = withinBounds(d); return this; }

            public Builder arg2(double d) { arg2 = withinBounds(d); return this; }

            public Builder arg3(double d) { arg3 = withinBounds(d); return this; }

            public double withinBounds(double d) {
                if (d > 1)
                    d = 1;
                if (d < 0)
                    d = 0;
                return d;
            }

            public MagicSongPicker build() { return new MagicSongPicker(this); }
        }

    /*      User controllable settings of the magic song picker gimmick:
     *
     *      1) Genre Novelty -- How likely am I to get a genre I normally list to?    TODO
     *      2) Artist Novelty -- How likely am I to get a song by an artist I normally list to?    TODO
     *      3) bpm function of time -- more energetic music will be discriminated against    TODO
     *                                           at certain times of the day.
     *      4) history weight -- are songs listened to months ago going to impact my recommendation    TODO
     *                       as much as newly listened to ones.
     */

    /**
     * byGenre
     * @param arg1 a factor between 0 - 1
     * @param t The timeline assessed
     * @return a set of genres that have more user listens
     *     than the most popular one's multiplied by arg1 .
     */
     public HashSet<String> byGenre(double arg1, Timeline t) {
         HashSet<String> hs = new HashSet<>();
         Map<String,Integer> genreCountMap = t.getGenreCountMap();

         int n = genreCountMap.get(t.topGenre());
         for (String s : genreCountMap.keySet())
             if (genreCountMap.get(s) > n * arg1)
                 hs.add(s);

         return hs;
     }

    /**
     * byArtist
     * @param arg2 a factor between 0 - 1
     * @param t The timeline assessed
     * @return a set of artists that have more user listens
     *     than the most popular one's multiplied by arg2.
     */
    public HashSet<String> byArtist(double arg2, Timeline t) {
        HashSet<String> hs = new HashSet<>();
        Map<String,Integer> artistCountMap = t.getArtistCountMap();

        int n = artistCountMap.get(t.mostPopularArtist());
        for (String s : artistCountMap.keySet())
            if (artistCountMap.get(s) > n * arg2)
                hs.add(s);

        return hs;
    }

    /**
     * byBpm
     * @param arg3 a width for the upper and lower bounds
     * @param t The timeline assessed
     * @return an upper and lower bound
     */
    public double[] byBpm(double arg3, Timeline t) {
         double bpm = t.averageBPM();

         return new double[]{bpm * (1 - arg3), bpm * (1 + arg3)};
    }

    /**
     * a simple and uncommited demonstration of how the algorithm will query a suggestion
     */
    public String SQLquery() {
        Random index = new Random();
        HashSet<String> hs1 = byGenre(arg1, minutes);
        HashSet<String> hs2 = byArtist(arg2, minutes);

        Object[] list = hs1.toArray();
        String a = (String) list[index.nextInt(0,list.length)];

        list = hs2.toArray();
        String b = (String) list[index.nextInt(0,list.length)];

        return "SELECT * FROM SONG WHERE genre = " + "'"+a+"'" + " AND artist = " + "'"+b+"'";
    }
}
