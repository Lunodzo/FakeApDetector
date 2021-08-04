package net.kismetwireless.android.pcapcapture;

import static android.os.SystemClock.sleep;

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
   Button scanButton, scanEvilButton;
   ArrayList<String> arrayList = new ArrayList<>();
   ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scan);
        scanButton = findViewById(R.id.btn_List);
        scanEvilButton = findViewById(R.id.btn_scan_evil);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
                //arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
                //wifiList.setAdapter(arrayAdapter);
                scanWiFi();
            }
        });

        scanEvilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanEvilWiFi();
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

    private void scanEvilWiFi() {
        arrayList.clear();
        registerReceiver(wifiEvilReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        int scanLoop = 2;
        List<ScanResult> ssid;
        List<ScanResult> bssid;
        List<ScanResult> signal;
        List<ScanResult> capabilities;

        String firstSSID;
        String secondSSID;
        String thirdSSID;

        String firstBSSID;
        String secondBSSID;
        String thirdBSSID;

        int firstSignal;
        int secondSignal;
        int thirdSignal;

        String firstCapabilities;
        String secondCapabilities;
        String thirdCapabilities;

//        String [] bssid = new String[10];
//        int [] signal = new int[10];
//        String [] capabilities = new String[10];
        Toast.makeText(this, "Scanning three times to collect enough results..", Toast.LENGTH_SHORT).show();

        //Control number of scans
        for (int i = 0; i <= scanLoop; i++){
            //Loop through all results and try to store each in a separate variable
            //Remember: You may have more that one SSID and everything else
            //TODO create a list of SSID, BSSID, signal strength and capabilities in this loop
            for(ScanResult scanResult: results) {
                wifiManager.startScan();
                //TODO Chech if the startScan() was necessary here

                if(i == 0){
                    Toast.makeText(this, "First scan", Toast.LENGTH_SHORT).show();
                    //firstSSID = Collections.singletonList(arrayList.add(scanResult.SSID));
                    //Loop controls number of results from one scan
                    for (int j=0; j < results.size(); j++){
                        firstSSID = results.get(j).SSID;
                        firstBSSID = results.get(j).BSSID;
                        firstSignal = results.get(j).level;
                        firstCapabilities = results.get(j).capabilities;

                        //Ignore these two lines
                        arrayList.add(firstSSID);
                        Object[] collectedFisrtSSID = arrayList.toArray();

                        //Testing what is being stored
                        Toast.makeText(this, "Found "+firstSSID, Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(this, "Matokeo ni "+firstSSID+" "+firstBSSID+" "
                            //+firstSignal+" "+firstCapabilities, Toast.LENGTH_SHORT).show();
                }else if(i == 1){
                    Toast.makeText(this, "Second scan", Toast.LENGTH_SHORT).show();
                    secondSSID = results.get(i).SSID;
                    secondBSSID = results.get(i).BSSID;
                    secondSignal = results.get(i).level;
                    secondCapabilities = results.get(i).capabilities;
                }else if (i == 2){
                    Toast.makeText(this, "Third scan", Toast.LENGTH_SHORT).show();
                    //thirdSSID = results.get(i).SSID;
                    //thirdBSSID = results.get(i).BSSID;
                    //thirdSignal = results.get(i).level;
                    //thirdCapabilities = results.get(i).capabilities;
                }else{
                    Toast.makeText(this, "Out of boundary", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(this, "Matokeo ni "+ssid[scanLoop]+" "+firstBSSID+" "
                    //+firstSignal+" "+firstCapabilities, Toast.LENGTH_SHORT).show();
            }
            sleep(3000);
        }
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
                arrayList.add(scanResult.SSID +" * "+ scanResult.capabilities + " * "+ scanResult.BSSID+ " * "
                        + scanResult.level);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    BroadcastReceiver wifiEvilReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
        }
    };
}

