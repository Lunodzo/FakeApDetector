package net.kismetwireless.android.pcapcapture;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CaptivePortal extends Activity {
    String url = "http://fakeap.lunodzo.com";
    //String url = "https://sis.nm-aist.ac.tz/student/student.php";

    //Method to generate string
//    static String randomString(int k){
//        String RandomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//                + "0123456789"
//                + "abcdefghijklmnopqrstuvxyz";
//        // create StringBuffer size of AlphaNumericString
//        StringBuilder sb = new StringBuilder(10);
//
//        for (int i=0; i<10; i++){
//            int index = (int) (randomString(10).length()*Math.random());
//            sb.append(RandomString.charAt(index));
//        }
//        return sb.toString();
//    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_captive_portal);
        WebView webView = new WebView(this);
        setContentView(webView);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            public void onReceivedError(WebView view, int errorCode, String desc, String failUrl){
                webView.loadUrl("https://sis.nm-aist.ac.tz/");
                //Toast.makeText(getApplicationContext(), "Code received "+errorCode, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);

        //Generate random credentials
        //String username = CaptivePortal.randomString(10);
        String username = "Abcdefghhrj";
        //String password = CaptivePortal.randomString(10);
        String password = "BDHHSD78678";
        webView.loadUrl("javascript:document.getElementsByName('username').value = "+username);
        webView.loadUrl("javascript:document.getElementsByName('password').value = "+password);
        webView.loadUrl("javascript:document.forms['submit'].submit()");

        //Read HTTP response
        WebResourceResponse errorResponse = null;
        int statusCode = errorResponse.getStatusCode();
        if(statusCode != 401){
            Toast.makeText(getApplicationContext(), "Fake Captive Portal"+statusCode, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Legit Captive Portal"+statusCode, Toast.LENGTH_SHORT).show();
        }
    }
}