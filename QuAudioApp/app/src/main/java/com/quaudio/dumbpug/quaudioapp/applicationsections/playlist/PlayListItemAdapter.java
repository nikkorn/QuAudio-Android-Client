package com.quaudio.dumbpug.quaudioapp.applicationsections.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;
import com.quaudio.dumbpug.quaudioapp.applicationsections.settings.LocalSettings;
import java.util.LinkedList;
import quclient.ProxyPlaylist.Track;

/**
 * Created by nik on 22/03/16.
 */
public class PlayListItemAdapter extends ArrayAdapter<Track> {
    private QuAppActivity currentActivity;
    // Reference to our ActivePlayList object.
    private ActivePlayList activePlayList;

    public PlayListItemAdapter(QuAppActivity currentActivity, ActivePlayList activePlaylist, LinkedList<Track> tracks) {
        super(currentActivity, 0, tracks);
        this.currentActivity = currentActivity;
        this.activePlayList = activePlaylist;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the current track we are making a view for.
        final Track currentTrack = getItem(position);
        // Determine if this is our track.
        Boolean clientOwnsTrack = LocalSettings.getClientId(currentActivity).equals(currentTrack.getOwnerId());
        // Get whether we are a super user on this QuDevice.
        boolean clientIsSuperUserForDevice = currentActivity.getCurrentQuDeviceInstance().adminModeEnabled();
        // Make the view.
        if(convertView == null) {
            convertView = LayoutInflater.from(currentActivity).inflate(R.layout.playlist_track_item, parent, false);
        }
        // Set the position number.
        TextView itemPositionTextView = (TextView) convertView.findViewById(R.id.playlistItemPositionTextView);
        itemPositionTextView.setText("" + (position+1));
        // Set the track name.
        TextView itemNameTextView = (TextView) convertView.findViewById(R.id.playlistItemNameTextView);
        itemNameTextView.setText(currentTrack.getName());
        // Set the track artist.
        TextView itemArtistTextView = (TextView) convertView.findViewById(R.id.playlistItemArtistTextView);
        itemArtistTextView.setText(currentTrack.getArtist());
        // Set the track album.
        TextView itemAlbumTextView = (TextView) convertView.findViewById(R.id.playlistItemAlbumTextView);
        itemAlbumTextView.setText(currentTrack.getAlbum());
        // Set up all buttons and hide them.
        Button stopButton = (Button) convertView.findViewById(R.id.quPlaylistItemStopButton);
        stopButton.setVisibility(View.GONE);
        stopButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                activePlayList.stopTrack(currentTrack);
            }
        });

        Button pauseButton = (Button) convertView.findViewById(R.id.quPlaylistItemPauseButton);
        pauseButton.setVisibility(View.GONE);
        pauseButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                activePlayList.pauseTrack(currentTrack);
            }
        });

        Button playButton = (Button) convertView.findViewById(R.id.quPlaylistItemPlayButton);
        playButton.setVisibility(View.GONE);
        playButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                activePlayList.playTrack(currentTrack);
            }
        });

        Button removeButton = (Button) convertView.findViewById(R.id.quPlaylistItemRemoveButton);
        removeButton.setVisibility(View.GONE);
        removeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                activePlayList.removeTrack(currentTrack);
            }
        });

        // Show relevant buttons.
        switch(currentTrack.getTrackState()){
            case PLAYING:
                // If the client is the track owner or a super user, then they can pause or stop the track.
                if(clientOwnsTrack || clientIsSuperUserForDevice) {
                    stopButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                }
                break;
            case PAUSED:
                // If the client is the track owner or a super user, then they can play or stop the track.
                if(clientOwnsTrack || clientIsSuperUserForDevice) {
                    stopButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.VISIBLE);
                }
                break;
            case STOPPED:
                // Do nothing here, the track will be removed the first chance the server gets.
                break;
            case PENDING:
                // If the client is the track owner or a super user, then they can remove the track.
                if(clientOwnsTrack || clientIsSuperUserForDevice) {
                    removeButton.setVisibility(View.VISIBLE);
                }
                break;
            case UNKNOWN:
                // Do nothing here.
                break;
        }

        return convertView;
    }
}
