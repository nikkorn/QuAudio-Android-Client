package com.quaudio.dumbpug.quaudioapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.quaudio.dumbpug.quaudioapp.applicationsections.library.Library;
import com.quaudio.dumbpug.quaudioapp.applicationsections.playlist.ActivePlayList;
import com.quaudio.dumbpug.quaudioapp.applicationsections.settings.Settings;
import quclient.NetProbe.ReachableQuDevice;
import quclient.QuEvent.QuEventListener;
import quclient.Server.Device;
import quclient.Server.HandshakeResponse;

public class QuAppActivity extends Activity implements QuEventListener {
    // Instance of connected Device
    private Device currentQuDeviceInstance = null;

    // Application Components.
    private Library library               = null;
    private ActivePlayList activePlayList = null;
    private Settings settings             = null;

    // The section type of the active tab.
    ApplicationSection activeApplicationSection = ApplicationSection.PLAYLIST;

    // The sub-view (library,playlist,settings) that currently populates the main layout.
    private View activeApplicationSectionView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialise our application components.
        library         = new Library(this);
        activePlayList  = new ActivePlayList(this);
        settings        = new Settings(this);
        // On the app startup we want to navigate to the Library view.
        MenuButtonPressed(findViewById(R.id.playlist_menu_button));
    }

    /**
     * Called when one of the main menu buttons (library,playlist,settings) is pressed.
     * @param v
     */
    public void MenuButtonPressed(View v) {
        // Get which menu button was pressed.
        Button pressedMenuButton = (Button) v;
        // Set all buttons to use inactive button image.
        ((Button) findViewById(R.id.library_menu_button)).setBackgroundResource(R.drawable.qu_orange_background);
        ((Button) findViewById(R.id.playlist_menu_button)).setBackgroundResource(R.drawable.qu_orange_background);
        ((Button) findViewById(R.id.settings_menu_button)).setBackgroundResource(R.drawable.qu_orange_background);
        // Set the pressed button as the active one
        pressedMenuButton.setBackgroundResource(R.drawable.qu_orange_background_active);
        // Change the view based on which menu button was pressed.
        switch(ApplicationSection.valueOf(pressedMenuButton.getText().toString())){
            case LIBRARY:
                showLibrary();
                break;
            case PLAYLIST:
                showPlaylist();
                break;
            case SETTINGS:
                showSettings();
                break;
        }
    }

    /**
     * Show the Library view.
     */
    public void showLibrary() {
        // TODO Do pre-processing
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_activity_linear_layout);
        // Remove the view that is currently occupying the layout ...
        if(this.activeApplicationSectionView != null) {
            mainLayout.removeView(this.activeApplicationSectionView);
        }
        // ... and replace it with the view we want.
        View libraryView = library.getLibraryView();
        mainLayout.addView(libraryView);
        this.activeApplicationSectionView = libraryView;
        this.activeApplicationSection = ApplicationSection.LIBRARY;
    }

    /**
     * Show the PlayList view.
     */
    public void showPlaylist() {
        // TODO Do pre-processing
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_activity_linear_layout);
        // Remove the view that is currently occupying the layout ...
        if(this.activeApplicationSectionView != null) {
            mainLayout.removeView(this.activeApplicationSectionView);
        }
        // ... and replace it with the view we want.
        View playlistView = activePlayList.getPlaylistView();
        mainLayout.addView(playlistView);
        this.activeApplicationSectionView = playlistView;
        this.activeApplicationSection = ApplicationSection.PLAYLIST;
    }

    /**
     * Show the Settings view.
     */
    public void showSettings() {
        // TODO Do pre-processing
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_activity_linear_layout);
        // Remove the view that is currently occupying the layout ...
        if(this.activeApplicationSectionView != null) {
            mainLayout.removeView(this.activeApplicationSectionView);
        }
        // ... and replace it with the view we want.
        View settingsView = settings.getSettingsView();
        mainLayout.addView(settingsView);
        this.activeApplicationSectionView = settingsView;
        this.activeApplicationSection = ApplicationSection.SETTINGS;
    }

    /**
     * Get the current Qu Device instance that represents a QuServer on the network (if connected).
     * @return Qu Device
     */
    public Device getCurrentQuDeviceInstance() {
        return this.currentQuDeviceInstance;
    }

    /**
     * Set the current Qu Device instance that represents a QuServer on the network.
     * @param newDeviceInstance
     */
    public void setCurrentQuDeviceInstance(Device newDeviceInstance) {
        this.currentQuDeviceInstance = newDeviceInstance;
    }

    //------------------------------------------------------------
    //-------------------Qu Event Handlers------------------------

    @Override
    public void onQuSettingsUpdate(Device sourceDevice) {
        System.out.println("Got settings update");
    }

    @Override
    public void onQuPlayListUpdate(Device sourceDevice) {
        // Update our playlist view.
        activePlayList.onPlayListUpdate(sourceDevice.getPlayList());
        // Update our library view.
        library.updateLibraryView(sourceDevice.getPlayList());
        // Do stuff on UI thread.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Refresh PlayList view if this is the current view.
                if(activeApplicationSection == ApplicationSection.PLAYLIST) {
                    showPlaylist();
                }
                // Refresh PlayList view if this is the current view.
                if(activeApplicationSection == ApplicationSection.LIBRARY) {
                    showLibrary();
                }
            }
        });
    }

    @Override
    public void onQuMasterVolumeUpdate(Device sourceDevice) {
        System.out.println("Got volume update");
    }

    @Override
    public void onQuDisconnect(Device sourceDevice) {
        final QuAppActivity currentActivity = this;
        final String deviceName = sourceDevice.getDeviceName();
        // Update our playlist view.
        activePlayList.onDisconnection();
        // Do stuff on UI thread.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                settings.refreshConnectedDevicesListView();
                Toast.makeText(currentActivity, "Disconnected from '" + deviceName + "'" , Toast.LENGTH_SHORT).show();
                // Refresh PlayList view if this is the current view.
                if(activeApplicationSection == ApplicationSection.PLAYLIST) {
                    showPlaylist();
                }
            }
        });
    }

    @Override
    public void onQuConnect(Device sourceDevice) {
        final QuAppActivity currentActivity = this;
        final String deviceName = sourceDevice.getDeviceName();
        // Update our playlist view.
        activePlayList.onConnection(sourceDevice.getPlayList());
        // Do stuff on UI thread.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                settings.refreshConnectedDevicesListView();
                Toast.makeText(currentActivity, "Connected to '" + deviceName + "'" , Toast.LENGTH_SHORT).show();
                // Refresh PlayList view if this is the current view.
                if(activeApplicationSection == ApplicationSection.PLAYLIST) {
                    showPlaylist();
                }
            }
        });
    }

    @Override
    public void onQuLinkFailure(ReachableQuDevice device, final HandshakeResponse handshakeResponse) {
        // All we have to do here is let the user know that the connection was a failure.
        final QuAppActivity currentActivity = this;
        final String deviceName = device.getDeviceName();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Let the user now why the link failed.
                switch (handshakeResponse) {
                    case WRONG_ACCESS_PASSWORD:
                        Toast.makeText(currentActivity, "Incorrect access password" , Toast.LENGTH_LONG).show();
                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(currentActivity, "Connection to '" + deviceName + "' failed" , Toast.LENGTH_LONG).show();
                        break;
                    case DECLINED:
                        Toast.makeText(currentActivity, "Connection to '" + deviceName + "' was declined" , Toast.LENGTH_LONG).show();
                        break;
                    case CLIENT_ALREADY_CONNECTED:
                        Toast.makeText(currentActivity, "Connection to '" + deviceName + "' was declined, user is already connected" , Toast.LENGTH_LONG).show();
                        break;
                    case UNIDENTIFIED:
                        Toast.makeText(currentActivity, "Connection to '" + deviceName + "' failed due to unknown reason" , Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }
}
