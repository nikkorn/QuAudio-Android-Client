package com.quaudio.dumbpug.quaudioapp.applicationsections.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;
import java.util.ArrayList;
import quclient.NetProbe.ReachableQuDevice;

/**
 * Created by nik on 11/03/16.
 */
public class AvailableDeviceListAdapter extends ArrayAdapter<ReachableQuDevice> {
    private QuAppActivity currentActivity;
    // Reference to our Settings object.
    private Settings settings;

    public AvailableDeviceListAdapter(QuAppActivity currentActivity, Settings settings, ArrayList<ReachableQuDevice> devices) {
        super(currentActivity, 0, devices);
        this.settings = settings;
        this.currentActivity = currentActivity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Lets get the id of the quserver we (might be) connected to. We need this because if the
        // scan picks up the server we are already connected to we want to show that we are already connected.
        String quServerID = null;
        if(currentActivity.getCurrentQuDeviceInstance() != null && currentActivity.getCurrentQuDeviceInstance().isConnected()) {
            // We are connected! Get this id.
            quServerID = currentActivity.getCurrentQuDeviceInstance().getDeviceId();
        }
        // Get the current device we are making a view for.
        ReachableQuDevice device = getItem(position);
        // Make the view.
        if(convertView == null) {
             convertView = LayoutInflater.from(currentActivity).inflate(R.layout.settings_available_device_item, parent, false);
        }
        // Does this ReachableQuDevice represent a device that we are already connected to?
        boolean isConnected = (quServerID != null && quServerID.equals(device.getDeviceId()));
        // Tweak the view to match the device details.
        // Set the device name.
        TextView deviceNameTextView = (TextView) convertView.findViewById(R.id.QuDeviceNameTextView);
        deviceNameTextView.setText(device.getDeviceName());
        // Enable/Disable connected icon.
        ImageView deviceConnectedImageView = (ImageView) convertView.findViewById(R.id.quDeviceConnectedIcon);
        deviceConnectedImageView.setVisibility(isConnected ? View.VISIBLE : View.GONE);
        // Enable/Disable protected icon.
        ImageView deviceProtectedImageView = (ImageView) convertView.findViewById(R.id.quDeviceLockIcon);
        deviceProtectedImageView.setVisibility(device.isProtected() ? View.VISIBLE : View.GONE);
        // Based on whether this quserver device is already connected we will have to either make
        // a disconnect button or a connect one.
        Button connectionButton = (Button) convertView.findViewById(R.id.quDeviceConnectionButton);
        if(isConnected) {
            connectionButton.setBackgroundResource(R.drawable.qu_orange_disconnect);
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settings.doDisconnect(getItem(position));
                }
            });
        } else {
            connectionButton.setBackgroundResource(R.drawable.qu_orange_connect);
            connectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settings.doConnect(getItem(position));
                }
            });
        }
        return convertView;
    }
}
