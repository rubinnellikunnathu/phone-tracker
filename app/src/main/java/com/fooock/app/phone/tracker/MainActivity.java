package com.fooock.app.phone.tracker;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.util.Log;

import com.fooock.lib.phone.tracker.Configuration;
import com.fooock.lib.phone.tracker.PhoneTracker;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private PhoneTracker phoneTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneTracker = new PhoneTracker(this);

        // Listen for missing permissions
        phoneTracker.addPermissionListener(new PhoneTracker.PermissionListener() {
            @Override
            public void onPermissionNotGranted(String... permission) {
                Log.d(TAG, "Permission not granted: " + Arrays.deepToString(permission));
            }
        });

        // Listen for configuration changes
        phoneTracker.setConfigurationChangeListener(new PhoneTracker.ConfigurationChangeListener() {
            @Override
            public void onConfigurationChange(Configuration configuration) {
                Log.d(TAG, "Tracker configuration changed!");
            }
        });

        // Check the state of the tracker
        boolean running = phoneTracker.isRunning();
        Log.d(TAG, "Running: " + running);

        // Create a new wifi configuration
        Configuration.Wifi wifiConf = new Configuration.Wifi();
        wifiConf.setScanDelay(3000);

        // Create a new cell configuration
        Configuration.Cell cellConf = new Configuration.Cell();
        cellConf.setScanDelay(1000);

        // Create a gps configuration
        Configuration.Gps gpsConf = new Configuration.Gps();

        // Create a bluetooth configuration
        Configuration.Bluetooth bluetoothConf = new Configuration.Bluetooth();

        // Create a new configuration
        Configuration configuration = new Configuration.Builder()
                .useCell(true).cell(cellConf)
                .useWifi(true).wifi(wifiConf)
                .useGps(true).gps(gpsConf)
                .useBluetooth(true).bluetooth(bluetoothConf)
                .create();

        // Set the new init configuration
        phoneTracker.setConfiguration(configuration);

        // Set the listener for cell scans
        phoneTracker.setCellScanListener(new PhoneTracker.CellScanListener() {
            @Override
            public void onCellInfoReceived(long timestamp, List<CellInfo> cells) {
                Log.d(TAG, "timestamp = [" + timestamp + "], cells = [" + cells.size() + "]");
            }

            @Override
            public void onNeighborCellReceived(long timestamp, List<NeighboringCellInfo> cells) {
                Log.d(TAG, "timestamp = [" + timestamp + "], cells = [" + cells.size() + "]");
            }
        });

        // Also you can use and cell listener adapter
        phoneTracker.setCellScanListener(new PhoneTracker.CellScanAdapter() {
            @Override
            public void onCellInfoReceived(long timestamp, List<CellInfo> cells) {
                Log.d(TAG, "[+] timestamp = [" + timestamp + "], cells = [" + cells.size() + "]");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        phoneTracker.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create a new configuration
                Configuration configuration = new Configuration.Builder()
                        .useCell(true)
                        .useWifi(true)
                        .useGps(true)
                        .useBluetooth(false)
                        .create();

                // Update the current configuration
                phoneTracker.updateConfiguration(configuration);
            }
        }, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        phoneTracker.stop();
    }
}
