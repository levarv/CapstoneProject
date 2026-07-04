package com.example.songs;

import java.util.ArrayList;

public class Playlist {

    private ArrayList<Song> list;
    private String name;

    public Playlist() {
        list = new ArrayList<>();
    }

    public Playlist(ArrayList<Song> list, String name) {
        this.list = list;
        this.name = name;
    }

    public ArrayList<Song> getList() {
        return list;
    }

    public void setList(ArrayList<Song> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



