package net.kismetwireless.android.pcapcapture;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class WiFiScan extends AppCompatActivity {
   WifiManager wifiManager;
   TextView scanText;
   ListView wifiList;
   List<ScanResult> results;
   Button scanButton, scanEvilButton;
   ArrayList<String> arrayList = new ArrayList<>();
   ArrayAdapter arrayAdapter;
   public int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scan);
        scanButton = findViewById(R.id.btn_List);
        scanEvilButton = findViewById(R.id.btn_scan_evil);
        wifiList = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        //Delete Data about scanned APs if they exist, this allows to start a fresh scan
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        databaseHandler.deleteApContents();

        //Button to scan Wifi
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
                //arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
                //wifiList.setAdapter(arrayAdapter);
                scanWiFi();
            }
        });

        //Button to scan Fake APs
        scanEvilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call the Attack scanning method
                databaseHandler.getStoredDuplicateAPs();
            }
        });

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
            int size = results.size();
            DatabaseHandler databaseHandler = new DatabaseHandler(context);

            //Check if there is any AP
            if (size > 0) {
                //Loop according to the number of APs OR for each available AP, post the details into
                //DB
                for (int i = 0; i < size; i++) {
                    ScanResult scanResult = wifiManager.getScanResults().get(i);

                    //AP parameters
                    String ssid = scanResult.SSID;
                    String bssid = scanResult.BSSID;
                    int rssi = scanResult.level;
                    String capabilities = scanResult.capabilities;
                    //long another = scanResult.timestamp;
                    java.util.Date date = new java.util.Date();
                    java.sql.Date currentTime = new java.sql.Date(date.getTime());
                    SimpleDateFormat dft = new SimpleDateFormat("HH:mm:ss.SSS");
                    String time = dft.format(currentTime);

                    //Write a statement to post these into Database
                    boolean insert = databaseHandler.addAccessPoints(ssid, bssid, rssi, capabilities, count, false, time);
                    if (insert == true){
                        Toast.makeText(getApplicationContext(), count+" Round Data inserted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Data insert Failed", Toast.LENGTH_SHORT).show();
                    }
                }
                count++;
                if(count <= 10){
                    //Another scan
                    wifiManager.startScan();
                    //Sleep for three seconds before attempting another scan
                    try {
                        TimeUnit.SECONDS.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    unregisterReceiver(this);
                }
            }else{
                unregisterReceiver(this);
                Toast.makeText(getApplicationContext(), "No Access Points found..", Toast.LENGTH_SHORT).show();
            }

            for (ScanResult scanResult: results){
                arrayList.add(scanResult.SSID +" * "+ scanResult.capabilities + " * "+ scanResult.BSSID+ " * "
                        + scanResult.level);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public class WifiScanAsyncTask extends AsyncTask<Void, String, String> {
        ProgressDialog dg = new ProgressDialog(getApplicationContext());

        protected void onPreExecute(){
            dg.setMessage("Scanning async");
            dg.setIndeterminate(false);
            dg.setCancelable(false);
            dg.show();
        }

        protected void onPostExecute(Void result) {
            dg.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {
            scanWiFi();
            return null;
        }
    }

    public class DatabaseHandler extends SQLiteOpenHelper{
        private static final String TAG = "DatabaseHelper";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "fakeapdetect";
        private static final String TABLE_AP = "accesspoints";
        private static final String ID = "id";
        private static final String SSID = "ssid";
        private static final String BSSID = "bssid";
        private static final String SIGNAL = "level";
        private static final String CAPABILITIES = "capabilities";
        private static final String SCAN_ROUND = "round";
        private static final String COMMENT = "comment";
        private static final String TIME = "time";
        Time time = new Time();

        public DatabaseHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_AP_TABLE = "CREATE TABLE " + TABLE_AP + "("
                    + ID + " INTEGER PRIMARY KEY,"
                    + SSID + " TEXT,"
                    + BSSID + " TEXT,"
                    + SIGNAL + " INTEGER,"
                    + CAPABILITIES + " TEXT,"
                    + SCAN_ROUND + " INTEGER,"
                    + COMMENT + " BOOLEAN,"
                    + TIME + " TEXT"
                    + ")";
            db.execSQL(CREATE_AP_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AP);
            onCreate(db);
        }

        //Add AP to Database
        public boolean addAccessPoints(String ssid, String bssid, int signal, String capabilities,
                                       int round, boolean comment, String time){
            SQLiteDatabase db = this.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(SSID, ssid);
            values.put(BSSID, bssid);
            values.put(SIGNAL, signal);
            values.put(CAPABILITIES, capabilities);
            values.put(SCAN_ROUND, round);
            values.put(COMMENT, comment);
            values.put(TIME, String.valueOf(time));

            Log.d(TAG, "addAccessPoints: Adding " +ssid+ " and "+bssid+" to "+TABLE_AP);
            long result = db.insert(TABLE_AP, null, values);

            if (result == -1){
                return false;
            }else{
                return true;
            }
        }

        public void deleteApContents(){
            SQLiteDatabase db = this.getWritableDatabase();
            String deleteContent = "DELETE FROM accesspoints";
            db.execSQL(deleteContent);
        }

        public void getStoredDuplicateAPs(){
            //TODO Fetch data from database then do the logic
            SQLiteDatabase db = getReadableDatabase();
//            String selectQuery1 = "SELECT ssid, bssid, level from accesspoints a1 where exists " +
//                    "(select 1 from accesspoints a2 where a1.ssid = a2.ssid and a1.bssid = a2.bssid " +
//                    "and a1.capabilities <> a2.capabilities) GROUP BY ssid";

            String selectQuery = "SELECT ssid, bssid from accesspoints GROUP BY ssid, bssid HAVING " +
                    "min(capabilities) <> max(capabilities)";
            Cursor resultSet = db.rawQuery(selectQuery, null);
            resultSet.moveToFirst();
            if(resultSet.getCount()>=1){
                String fakeAPssid = resultSet.getString(0);
                Toast.makeText(getApplicationContext(), "Fake AP "+fakeAPssid+" detected", Toast.LENGTH_SHORT).show();
            }else{
                //TODO Calculate average signal strength based on the benchmarhmark of the first scan
                //The everything that falls not in the range of the average signal level might be fake

                String selectFirstSignal = "select ssid, first_value(level) OVER win as" +
                        "first_level from accesspoints WINDOW win" +
                        "AS (ORDER BY time ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)";

                String viewOfDuplicateOpenAP = "CREATE view duplicateCapabilities AS " +
                        "SELECT ssid, bssid, capabilities, level FROM accesspoints a1 " +
                        "WHERE EXISTS (SELECT 1 FROM accesspoints a2 WHERE " +
                        "a1.ssid = a2.ssid AND a1.bssid = a2.bssid AND " +
                        "a1.capabilities = a2.capabilities) EXCEPT" +
                        "SELECT ssid, bssid, capabilities, level FROM accesspoints a1 " +
                        "WHERE EXISTS (SELECT 1 FROM accesspoints a2 WHERE " +
                        "a1.ssid = a2.ssid AND a1.bssid = a2.bssid AND " +
                        "a1.capabilities != a2.capabilities)";

                Toast.makeText(getApplicationContext(), "No fake AP", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

