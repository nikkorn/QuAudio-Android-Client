package com.quaudio.dumbpug.quaudioapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.quaudio.dumbpug.quaudioapp.applicationsections.library.Library;
import com.quaudio.dumbpug.quaudioapp.applicationsections.playlist.ActivePlayList;
import com.quaudio.dumbpug.quaudioapp.applicationsections.settings.Settings;
import quclient.Server.Device;

public class QuAppActivity extends Activity {
    // Instance of connected Device
    private Device currentQuDeviceInstance = null;

    // Application Components.
    private Library library               = null;
    private ActivePlayList activePlayList = null;
    private Settings settings             = null;

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
    }

    /**
     * Get the current Qu Device instance that represents a QuServer on the network (if connected).
     * @return Qu Device
     */
    public Device getCurrentQuDeviceInstance() {
        return this.currentQuDeviceInstance;
    }
}
