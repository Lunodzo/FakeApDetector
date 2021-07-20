package net.kismetwireless.android.pcapcapture;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WiFiScan extends Activity {
    WifiManager wifi;
    ListView myList;
    Button btnScan;
    TextView textStatus;
    int size = 0;
    List<ScanResult> results;
    String wifiList[];

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scan);
        myList = findViewById(R.id.wifiList);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }
};

