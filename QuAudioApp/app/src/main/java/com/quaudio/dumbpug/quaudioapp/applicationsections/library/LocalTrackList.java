package com.quaudio.dumbpug.quaudioapp.applicationsections.library;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import java.util.ArrayList;

/**
 * Created by Nikolas Howard on 04/03/2016.
 */
public class LocalTrackList {
    private ArrayList<LocalTrackGroup> artists = new ArrayList<LocalTrackGroup>();

    public LocalTrackList(Activity currentActivity) {
        populateFromDevice(currentActivity);
    }

    /**
     * Get artist track map.
     * @return
     */
    public ArrayList<LocalTrackGroup> getTrackGroupList() {
        return artists;
    }

    /**
     * Populate the list of artists and their tracks.
     * @param currentActivity
     */
    private void populateFromDevice(Activity currentActivity) {
        String select = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        // Define query projection.
        String[] prjct = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        // Do query to get media on device.
        Cursor trackCursor = currentActivity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, prjct, select, null, null);
        // Populate artists list.
        while(trackCursor.moveToNext()) {
            LocalTrackGroup relevantArtist = null;
            for(LocalTrackGroup artist : artists) {
               if(artist.getName().equals(trackCursor.getString(1))) {
                   relevantArtist = artist;
                   break;
               }
            }
            if(relevantArtist == null) {
                relevantArtist = new LocalTrackGroup(trackCursor.getString(1));
                artists.add(relevantArtist);
            }
            LocalTrack newTrack = new LocalTrack(trackCursor.getString(4), trackCursor.getString(3), trackCursor.getString(1), trackCursor.getString(2));
            relevantArtist.getTrackList().add(newTrack);
        }
    }
}
