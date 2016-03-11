package com.quaudio.dumbpug.quaudioapp.applicationsections.playlist;

import android.view.View;
import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;

/**
 * Created by nik on 09/03/16.
 */
public class ActivePlayList {
    // The actual playlist view.
    private View playlistView;

    public ActivePlayList(QuAppActivity currentActivity) {
        // Create our playlist tab view. Initially will use disconnected layout.
        playlistView = currentActivity.getLayoutInflater().inflate(R.layout.playlist_disconnected_layout, null);
    }

    public View getPlaylistView() { return playlistView; }

    public void setPlaylistView(View playlistView) {
        this.playlistView = playlistView;
    }
}
