package net.kismetwireless.android.pcapcapture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class WiFiScan extends AppCompatActivity {
   WifiManager wifiManager;
   TextView scanText;
   ListView wifiList;
   List<ScanResult> results;
   Button scanButton;
   ArrayList<String> arrayList = new ArrayList<>();
   ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scan);
        scanButton = findViewById(R.id.btn_List);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        wifiList = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        if (!wifiManager.isWifiEnabled()){
            Toast.makeText(this, "Enable WiFi", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        wifiList.setAdapter(arrayAdapter);
        scanWiFi();
    }

    private  void scanWiFi(){
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning..", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult: results){
                arrayList.add(scanResult.SSID +" - "+ scanResult.capabilities + " - "+ scanResult.BSSID);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };
}

