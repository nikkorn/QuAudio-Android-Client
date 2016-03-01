package com.quaudio.dumbpug.quaudioapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import quclient.*;
import quclient.Config.ClientConnectionConfig;
import quclient.FileTransfer.FileFormat;
import quclient.NetProbe.NetProbe;
import quclient.NetProbe.ReachableQuDevice;
import quclient.Server.Device;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        System.out.println("Here we go!");
        NetProbe probe = new NetProbe();
        probe.initialise("192.168.1.111");
        ArrayList<ReachableQuDevice> devices = probe.getReachableQuDevices(true);

        if(devices.size() > 0) {
            ReachableQuDevice rqd = devices.get(0);

            ClientConnectionConfig config = new ClientConnectionConfig();
            config.setClientId(UUID.randomUUID().toString()); // Generate a random id so we can connect multiple clients.
            config.setClientName("Nik");
            config.setAccessPassword("a1a1");

            // Attempt to initialise our Device object.
            Device runningQuServerDevice = new Device();
            try {
                runningQuServerDevice.link(rqd, config);
            } catch (IOException e) {
                System.out.println("IOException on link!");
                e.printStackTrace();
            } catch (RuntimeException e1) {
                System.out.println("RuntimeException on link!");
                e1.printStackTrace();
            }

            try {
                // TRY SOME STUFF...

                // Send for fun
                runningQuServerDevice.uploadAudioFile(new File(""), FileFormat.MP3, "nik", "band", "allybum");

            } catch (RuntimeException e1) {
                System.out.println("RuntimeException on upload!");
                e1.printStackTrace();
            }
        } else {
            System.out.println("We did not get any ReachableQuDevices!");
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
