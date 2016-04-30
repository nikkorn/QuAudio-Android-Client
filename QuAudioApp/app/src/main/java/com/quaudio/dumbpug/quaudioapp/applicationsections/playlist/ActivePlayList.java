package com.quaudio.dumbpug.quaudioapp.applicationsections.playlist;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;
import com.quaudio.dumbpug.quaudioapp.applicationsections.settings.AvailableDeviceListAdapter;

import java.util.LinkedList;

import quclient.ProxyPlaylist.PlayList;
import quclient.ProxyPlaylist.Track;

/**
 * Created by nik on 09/03/16.
 */
public class ActivePlayList {
    // The actual playlist view.
    private View playlistView;
    // The possible views.
    private View disconnectedView;
    private View emptyPlaylistView;
    private View populatedListView;
    // The main activity.
    private QuAppActivity appActivity;
    // The most up-to-date PlayList
    private PlayList currentPlayList = new PlayList(new LinkedList<Track>());
    // Adapter used to update our playlist list view.
    private PlayListItemAdapter playListItemAdapter = null;

    public ActivePlayList(QuAppActivity currentActivity) {
        this.appActivity = currentActivity;
        // Create our playlist tab view. Initially will use disconnected layout.
        disconnectedView = currentActivity.getLayoutInflater().inflate(R.layout.playlist_disconnected_layout, null);
        emptyPlaylistView = currentActivity.getLayoutInflater().inflate(R.layout.playlist_empty, null);
        populatedListView = currentActivity.getLayoutInflater().inflate(R.layout.playlist_layout, null);
        // Set the current view.
        playlistView = disconnectedView;
        // Set up our playlist items list adapter.
        this.playListItemAdapter = new PlayListItemAdapter(appActivity, this, this.currentPlayList.getTracks());
        ListView playlistItemsListView = (ListView) populatedListView.findViewById(R.id.playlistItemListView);
        playlistItemsListView.setAdapter(this.playListItemAdapter);
    }

    /**
     * Called  when the client presses the 'play' button on a track.
     * @param track
     */
    public void playTrack(Track track) {
        System.out.println("PLAYLIST : PLAY");
        track.play();
    }

    /**
     * Called  when the client presses the 'pause' button on a track.
     * @param track
     */
    public void pauseTrack(Track track) {
        System.out.println("PLAYLIST : PAUSE");
        track.pause();
    }

    /**
     * Called  when the client presses the 'stop' button on a track.
     * @param track
     */
    public void stopTrack(Track track) {
        System.out.println("PLAYLIST : STOP");
        track.stop();
    }

    /**
     * Called  when the client presses the 'remove' button on a track.
     * @param track
     */
    public void removeTrack(Track track) {
        System.out.println("PLAYLIST : REMOVE");
        track.remove();
    }

    /**
     * This method will be called when we get a playlist update.
     * @param playlist
     */
    public void onPlayListUpdate(PlayList playlist) {
        this.currentPlayList = playlist;
        // Set the view based on the state of the playlist.
        if(currentPlayList.getTracks().size() == 0) {
            playlistView = this.emptyPlaylistView;
        } else {
            playlistView = this.populatedListView;
        }
        // Update our playlist items listview on the UI thread.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                refreshPlaylistItemsListView();
            }
        });
    }

    /**
     * Called when we disconnect from a server.
     */
    public void onDisconnection() {
        // Set the disconnected view as the playlist view.
        playlistView = disconnectedView;
    }

    /**
     * Called when we connect to a server.
     * @param initialPlayList
     */
    public void onConnection(PlayList initialPlayList) {
        this.currentPlayList = initialPlayList;
        // Set the view based on the state of the playlist.
        if(currentPlayList.getTracks().size() == 0) {
            playlistView = this.emptyPlaylistView;
        } else {
            playlistView = this.populatedListView;
            // Update our playlist items listview on the UI thread.
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                refreshPlaylistItemsListView();
            }
        });
    }

    /**
     * Called when we want our playlist items list to refresh.
     */
    public void refreshPlaylistItemsListView() {
        playListItemAdapter.clear();
        playListItemAdapter.addAll(this.currentPlayList.getTracks());
        playListItemAdapter.notifyDataSetChanged();
    }

    public View getPlaylistView() { return playlistView; }

    public void setPlaylistView(View playlistView) { this.playlistView = playlistView; }
}
