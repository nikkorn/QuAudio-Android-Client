package com.quaudio.dumbpug.quaudioapp.applicationsections.library;

import java.util.ArrayList;

/**
 * Created by nik on 08/03/16.
 */
public class LocalTrackGroup {
    private String name;
    private ArrayList<LocalTrack> tracks = new ArrayList<LocalTrack>();

    public LocalTrackGroup(String artist) {
        this.name = artist;
    }

    public String getName() {
        return name;
    }

    public ArrayList<LocalTrack> getTrackList() {
        return tracks;
    }
}
