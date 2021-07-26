package net.kismetwireless.android.pcapcapture;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.File;

public class FakeAPScanner extends Activity {
    Context mContext;
    SharedPreferences mPreferences;
    String mLogDir;
    FilelistFragment mList;

    @Override
    public void onResume(){
        super.onResume();
        //In case we add ability to change
        mLogDir = mPreferences.getString(MainActivity.PREF_LOGDIR, "/");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mLogDir = mPreferences.getString(MainActivity.PREF_LOGDIR, "/");

        setContentView(R.layout.activity_fake_apscanner);

        mList = (FilelistFragment) getFragmentManager().findFragmentById(R.id.fragment_pcaplist);
        mList.registerFiletype("pcap", new PcapFileTyper());
        mList.setDirectory(new File(mLogDir));
        mList.setRefreshTimer(2000);
        mList.setFavorites(true);
        mList.Populate();
    }
}