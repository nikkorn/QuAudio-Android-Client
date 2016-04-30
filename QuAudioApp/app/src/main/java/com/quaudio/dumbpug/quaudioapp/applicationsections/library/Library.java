package com.quaudio.dumbpug.quaudioapp.applicationsections.library;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;
import java.io.File;
import quclient.FileTransfer.FileFormat;
import quclient.ProxyPlaylist.PlayList;
import quclient.Server.Device;

/**
 * Created by nik on 06/03/16.
 */
public class Library {
    // Data structure that represents the tracks stored on the device.
    private LocalTrackList localTrackList;
    // The main activity.
    private QuAppActivity appActivity;
    // Adapter that adapts the local track list to the expandable listview under the library tab.
    private LibraryExpandableListAdapter trackListViewAdapter;
    // The actual library view.
    private View libraryView = null;

    public Library(QuAppActivity currentActivity) {
        this.appActivity = currentActivity;
        // Generate local track list.
        localTrackList = new LocalTrackList(currentActivity);
        // Create our library tab view.
        libraryView = currentActivity.getLayoutInflater().inflate(R.layout.library_layout, null);
        // Create our expandable list adapter and link it to out library list view.
        ExpandableListView trackListView = (ExpandableListView) libraryView.findViewById(R.id.LibraryListView);
        trackListViewAdapter = new LibraryExpandableListAdapter(currentActivity, this, localTrackList.getTrackGroupList());
        trackListView.setAdapter(trackListViewAdapter);
    }

    /**
     * Upload a track to the server (if we are connected).
     * @param track
     */
    public void uploadTrack(LocalTrack track) {
        // Lets attempt to upload this track. Firstly, get the current proxy device instance.
        Device currentDevice = appActivity.getCurrentQuDeviceInstance();
        if(currentDevice != null && currentDevice.isConnected()) {
            Toast.makeText(this.appActivity, "Uploading track...", Toast.LENGTH_SHORT).show();
            // Let's do our upload
            // TODO eventually we will support more that MP3.
            currentDevice.uploadAudioFile(new File(track.getPath()), FileFormat.MP3, track.getTrackName(), track.getTrackArtist(), track.getTrackAlbum());
            // TODO Add catch here in case the upload fails.

        } else {
            Toast.makeText(this.appActivity, "Can't upload track, not connected", Toast.LENGTH_SHORT).show();
        }
    }

    // This method will be called when we get a playlist update
    public void updateLibraryView(PlayList playList) {
        //     TODO localTrackList.updateListState(playList)
        //     TODO Update the library list view using the local track list and the list adapter.
    }

    public View getLibraryView() {
        return this.libraryView;
    }

    public void setLibraryView(View libraryView) {
        this.libraryView = libraryView;
    }
}
