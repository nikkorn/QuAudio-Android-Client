package com.quaudio.dumbpug.quaudioapp.applicationsections.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;
import java.util.ArrayList;

/**
 * Created by nik on 08/03/16.
 */
public class LibraryExpandableListAdapter extends BaseExpandableListAdapter {
    private QuAppActivity currentActivity;
    private ArrayList<LocalTrackGroup> localTrackList;
    private Library library;

    LibraryExpandableListAdapter(QuAppActivity currentActivity, Library library, ArrayList<LocalTrackGroup> trackList) {
        this.currentActivity = currentActivity;
        this.localTrackList  = trackList;
        this.library = library;
    }

    @Override
    public int getGroupCount() {
        return localTrackList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<LocalTrack> trList = localTrackList.get(groupPosition).getTrackList();
        return trList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return localTrackList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<LocalTrack> trList = localTrackList.get(groupPosition).getTrackList();
        return trList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    } // TODO Check if this is true.

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) currentActivity.getSystemService(currentActivity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.library_list_group, null);
        }
        LocalTrackGroup group = (LocalTrackGroup) getGroup(groupPosition);
        TextView groupName = (TextView) convertView.findViewById(R.id.LibraryArtistHeader);
        groupName.setText(group.getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) currentActivity.getSystemService(currentActivity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.library_list_item, null);
        }
        final LocalTrack track = (LocalTrack) getChild(groupPosition, childPosition);
        TextView trackName = (TextView) convertView.findViewById(R.id.LibraryTrackName);
        TextView trackAlbum = (TextView) convertView.findViewById(R.id.LibraryTrackAlbum);
        trackName.setText(track.getTrackName());
        trackAlbum.setText(track.getTrackAlbum());
        Button uploadButton = (Button) convertView.findViewById(R.id.uploadTrackButton);
        uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                library.uploadTrack(track);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
