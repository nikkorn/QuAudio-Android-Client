package com.quaudio.dumbpug.quaudioapp.applicationsections.library;

import android.app.Activity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;
import quclient.ProxyPlaylist.PlayList;

/**
 * Created by nik on 06/03/16.
 */
public class Library {
    // Data structure that represents the tracks stored on the device.
    private LocalTrackList localTrackList;
    // Adapter that adapts the local track list to the expandable listview under the library tab.
    private LibraryExpandableListAdapter trackListViewAdapter;
    // The actual library view.
    private View libraryView = null;

    public Library(QuAppActivity currentActivity) {
        // Generate local track list.
        localTrackList = new LocalTrackList(currentActivity);
        // Create our library tab view.
        libraryView = currentActivity.getLayoutInflater().inflate(R.layout.library_layout, null);
        // Create our expandable list adapter and link it to out library list view.
        ExpandableListView trackListView = (ExpandableListView) libraryView.findViewById(R.id.LibraryListView);
        trackListViewAdapter = new LibraryExpandableListAdapter(currentActivity, localTrackList.getTrackGroupList());
        trackListView.setAdapter(trackListViewAdapter);
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
