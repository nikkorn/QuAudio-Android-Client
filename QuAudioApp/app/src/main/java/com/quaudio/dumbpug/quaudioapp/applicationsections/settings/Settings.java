package com.quaudio.dumbpug.quaudioapp.applicationsections.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quaudio.dumbpug.quaudioapp.QuAppActivity;
import com.quaudio.dumbpug.quaudioapp.R;

import java.util.ArrayList;

import quclient.Config.ClientConnectionConfig;
import quclient.NetProbe.NetProbe;
import quclient.NetProbe.ReachableQuDevice;
import quclient.Server.Device;

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
        // Set our static username.
        TextView userNameTextView = (TextView) settingsView.findViewById(R.id.userName);
        userNameTextView.setText(LocalSettings.getUserName(appActivity));
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
        Thread scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetProbe probe = new NetProbe();
                // Get reachable qu devices.
                reachableQuDevices = probe.getReachableQuDevices(false);
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
            }
        });
        scanThread.start();
    }

    /**
     * Called when the user requests a connection to a found quserver instance.
     * @param device
     */
    public void doConnect(final ReachableQuDevice device) {
        // Make a new QuServer proxy device object.
        final Device newDevice = new Device();
        // Set this new device as the current one.
        appActivity.setCurrentQuDeviceInstance(newDevice);
        // Set the apps main activity as the QuEventListener for this new device.
        newDevice.addQuEventListener(appActivity);
        // Construct our client config
        final ClientConnectionConfig config = new ClientConnectionConfig();
        config.setAccessPassword("");
        config.setClientId(LocalSettings.getClientId(appActivity));
        config.setClientName(LocalSettings.getUserName(appActivity));
        // Does the device have an access password? if so then get it from the user. If
        // not then just do our link.
        if(device.isProtected()) {
            // Get the prompt view for access password input.
            LayoutInflater inflater = LayoutInflater.from(appActivity);
            View inputAccessPasswordPromptView = inflater.inflate(R.layout.settings_input_access_password_prompt, null);
            // Set up a dialog builder.
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(appActivity);
            // Get a reference to the input on the view.
            final EditText usernameInput = (EditText) inputAccessPasswordPromptView.findViewById(R.id.accessPasswordInput);
            // Set the view for the dialog builder and finish setting it up.
            dialogBuilder.setView(inputAccessPasswordPromptView);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // TODO Verify that access password is valid.
                    // Set the access password.
                    config.setAccessPassword(usernameInput.getText().toString());
                    // Do the link! The main app activity should now get notified of any events.
                    newDevice.link(device, config);
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // We want to cancel, just close the dialog.
                    dialog.cancel();
                }
            });
            // create alert dialog
            AlertDialog dialog = dialogBuilder.create();
            // Actually show the dialog
            dialog.show();
        } else {
            // Do the link! The main app activity should now get notified of any events.
            newDevice.link(device, config);
        }
    }

    /**
     * Called when the user requests a disconnection from the currently connected server..
     * @param device
     */
    public void doDisconnect(ReachableQuDevice device) {
        // Attempt to disconnect from the server.
        Device currentDevice = appActivity.getCurrentQuDeviceInstance();
        if(currentDevice != null) {
            // Disconnect and sever connection to the server.
            currentDevice.disconnect();
        }
    }

    /**
     * Called when the user clicks on the edit username button.
     */
    public void editUserName() {
        // Get the prompt view for editing the username.
        LayoutInflater inflater = LayoutInflater.from(appActivity);
        View editUserNamePromptView = inflater.inflate(R.layout.settings_edit_username_prompt, null);
        // Set up a dialog builder.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(appActivity);
        // Get a reference to the input on the view.
        final EditText usernameInput = (EditText) editUserNamePromptView.findViewById(R.id.editUserNameInput);
        // Put the current UserName in the edit box on the dialog.
        String currentUserName = LocalSettings.getUserName(appActivity);
        usernameInput.setText(currentUserName);
        // Set the view for the dialog builder and finish setting it up.
        dialogBuilder.setView(editUserNamePromptView);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                boolean isUserNameValid = true;
                // Get the new username
                String newUserName = usernameInput.getText().toString();
                // TODO Make sure new username is valid.
                if(isUserNameValid) {
                    // Persist the new username to local storage.
                    LocalSettings.setUserName(newUserName, appActivity);
                    // Update the static username on the settings tab.
                    TextView userNameTextView = (TextView) settingsView.findViewById(R.id.userName);
                    userNameTextView.setText(newUserName);
                } else {
                    // TODO Throw a hissy fit
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // We want to cancel, just close the dialog.
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog dialog = dialogBuilder.create();
        // Actually show the dialog
        dialog.show();
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
