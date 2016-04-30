package com.quaudio.dumbpug.quaudioapp.applicationsections.library;

/**
 * Created by nh163 on 04/03/2016.
 */
public class LocalTrack {
    private final String path;
    private final String trackName;
    private final String trackArtist;
    private final String trackAlbum;
    // The position of this track in the current playlist, 0 if not in playlist and can be uploaded.
    private int queuePosition;

    public LocalTrack(String path, String trackName, String trackArtist, String trackAlbum) {
        this.path = path;
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.trackAlbum = trackAlbum;
    }

    public void setQueuePosition(int position) { this.queuePosition = (position < 0) ? 0 : position; }

    public int getQueuePosition() { return queuePosition; }

    public String getTrackName() { return trackName; }

    public String getTrackArtist() {
        return trackArtist;
    }

    public String getPath() {
        return path;
    }

    public String getTrackAlbum() {
        return trackAlbum;
    }
}
