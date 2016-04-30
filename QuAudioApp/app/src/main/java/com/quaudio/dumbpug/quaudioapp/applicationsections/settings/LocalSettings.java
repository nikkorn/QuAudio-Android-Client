package com.quaudio.dumbpug.quaudioapp.applicationsections.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.quaudio.dumbpug.quaudioapp.QuAppActivity;

import java.util.Formattable;
import java.util.UUID;

/**
 * Created by nh163 on 04/03/2016.
 */
public class LocalSettings {
    public static final String pref_id = "QU_AUDIO_PREFS";

    /**
     * Get the permanent UUID for this client, if one does not exist then generate on first.
     * @param currentActivity
     * @return Client Id
     */
    public static String getClientId(Activity currentActivity) {
        // Attempt to get a pre-generated client id from preferences;
        SharedPreferences prefs = currentActivity.getSharedPreferences(pref_id , 0);
        String clientId = prefs.getString("CLIENT_ID", null);
        // If our clientId is null then it has never been set. Generate one and set it.
        if(clientId == null) {
            // Generate new client id.
            String newClientId = UUID.randomUUID().toString();
            // Write this new client id to disk.
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putString("CLIENT_ID", newClientId);
            prefsEditor.commit();
            // Return our brand new client id.
            return  newClientId;
        } else {
            return clientId;
        }
    }

    /**
     * Get the clients username, or a default if one has never been set.
     * @param currentActivity
     * @return username
     */
    public static String getUserName(Activity currentActivity) {
        SharedPreferences prefs = currentActivity.getSharedPreferences(pref_id , 0);
        String userName = prefs.getString("USERNAME", null);
        // If our clientId is null then it has never been set. Generate one and set it.
        if(userName == null) {
            // the user has not personalised their username yet, return a default.
            return  "New User";
        } else {
            return userName;
        }
    }

    /**
     * Set the username for the client.
     * @param userName
     * @param currentActivity
     */
    public static void setUserName(String userName, Activity currentActivity) {
        SharedPreferences prefs = currentActivity.getSharedPreferences(pref_id, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("USERNAME", userName);
        prefsEditor.commit();
    }

    /**
     * Returns the IPv4 local address of this device on WIFI.
     * @param currentActivity
     * @return address.
     */
    public static String getIPv4Address(QuAppActivity currentActivity) {
        WifiManager wMgr = (WifiManager) currentActivity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wMgr.getConnectionInfo();
        // TODO Find our what happens when we're not on wifi.
        return Formatter.formatIpAddress(wInfo.getIpAddress());
    }
}
