package net.kismetwireless.android.pcapcapture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class WiFiScan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scan);

        //wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //wifiManager.startScan();

    }

    public static void backupWiFi(Context context){
        WifiManager wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String ID = wifiInfo.getBSSID();
        int IP = wifiInfo.getIpAddress();
        String MAC = wifiInfo.getMacAddress();
        String SSID = wifiInfo.getSSID();
        int NWID = wifiInfo.getNetworkId();

        Log.v("ID", ID);
        Log.v("IP", String.valueOf(IP));
        Log.v("MAC", MAC);
        Log.v("SSID", SSID);
        Log.v("NWID", String.valueOf(NWID));


        final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION){
                    List<ScanResult> mScanResults = wifiManager.getScanResults();
                    //Add logic
                }
            }
        };
    }
}