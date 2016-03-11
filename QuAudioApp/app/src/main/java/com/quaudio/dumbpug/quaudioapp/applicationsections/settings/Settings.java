package com.quaudio.dumbpug.quaudioapp.applicationsections.settings;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import quclient.NetProbe.C;
import quclient.NetProbe.NetProbe;
import quclient.NetProbe.ReachableQuDevice;

/**
 * Created by nik on 09/03/16.
 */
public class Settings {
    // The actual settings view.
    private View settingsView = null;
    // The main activity.
    private QuAppActivity appActivity;
    // Adapter used to update our connected devices list view.
    private AvailableDeviceListAdapter availableDeviceListAdapter = null;
    // List of ReachableQuDevices used to populate our available devices listview.
    private ArrayList<ReachableQuDevice> reachableQuDevices = new ArrayList<ReachableQuDevice>();

    public Settings(QuAppActivity currentActivity) {
        this.appActivity = currentActivity;
        // Create our settings tab view.
        settingsView = currentActivity.getLayoutInflater().inflate(R.layout.settings_layout, null);
        // Setup event handlers for our controls.
        Button doScanButton = (Button) settingsView.findViewById(R.id.doScanButton);
        Button editUsernameButton = (Button) settingsView.findViewById(R.id.editUserNameButton);
        doScanButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScan();
            }
        });
        editUsernameButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName();
            }
        });
        // Set up our connected devices list adapter.
        this.availableDeviceListAdapter = new AvailableDeviceListAdapter(appActivity, this, this.reachableQuDevices);
        ListView connectedDevicesListView = (ListView) settingsView.findViewById(R.id.ConnectedDevicesListView);
        connectedDevicesListView.setAdapter(this.availableDeviceListAdapter);
    }

    /**
     * Called when user clicks on the scan button.
     * Carry out a scan for listening QuServer instances and populate our reachableQuDevice listview with the results.
     */
    public void doScan() {
        Toast.makeText(this.appActivity, "Looking for QuAudio Devices... ", Toast.LENGTH_SHORT).show();
        final String ipv4Address = LocalSettings.getIPv4Address(appActivity);
        System.out.println("IP: " + ipv4Address);
        Thread scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetProbe probe = new NetProbe();
                // Attempt to initialise our probe.
                if(probe.initialise(ipv4Address)) {
                    // Get reachable qu devices.
                    reachableQuDevices = probe.getReachableQuDevices(true);
                    // Update our connected devices listview on the UI thread.
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // If we found no devices, tell the user as they will have no idea otherwise.
                            if(reachableQuDevices.size() == 0) {
                                Toast.makeText(appActivity, "No QuAudio devices were found", Toast.LENGTH_SHORT).show();
                            }
                            refreshConnectedDevicesListView();
                        }
                    });
                } else {
                    // We had an error initialising our probe.
                    Toast.makeText(appActivity, "Encountered error setting up our scanner!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        scanThread.start();
    }

    /**
     * Called when the user requests a connection to a found quserver instance.
     * @param device
     */
    public void doConnect(ReachableQuDevice device) {
        Toast.makeText(this.appActivity, "Let's do a connect!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the user requests a disconnection from the currently connected server..
     * @param device
     */
    public void doDisconnect(ReachableQuDevice device) {
        Toast.makeText(this.appActivity, "Let's do a disconnect!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the user clicks on the edit username button.
     * Carry out a scan for listening QuServer instances and populate our reachableQuDevice listview with the results.
     */
    public void editUserName() {
        Toast.makeText(this.appActivity, "Let's edit our username!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when we want our connected devices list to refresh.
     */
    public void refreshConnectedDevicesListView() {
        availableDeviceListAdapter.clear();
        availableDeviceListAdapter.addAll(this.reachableQuDevices);
        availableDeviceListAdapter.notifyDataSetChanged();
    }

    /**
     * Get the settings tab view.
     * @return settings tab view.
     */
    public View getSettingsView() {
        return settingsView;
    }

    /**
     * Set the settings tab view.
     * @param settingsView
     */
    public void setSettingsView(View settingsView) {
        this.settingsView = settingsView;
    }
}
