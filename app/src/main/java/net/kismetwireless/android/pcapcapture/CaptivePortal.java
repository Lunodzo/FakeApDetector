package net.kismetwireless.android.pcapcapture;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CaptivePortal extends Activity {
    String url = "https://fakeap.lunodzo.com";
    //String url = "https://sis.nm-aist.ac.tz/student/student.php";

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
                Toast.makeText(getApplicationContext(), "Code received "+errorCode, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("javascript:document.getElementsByName('username').value = 'username'");
        webView.loadUrl("javascript:document.getElementsByName('password').value = 'password'");
        webView.loadUrl("javascript:document.forms['login'].submit()");

        //Read HTTP response
        WebResourceResponse errorResponse = null;
        WebResourceRequest request;
        //TODO Capture error codes after submiting
//        int statusCode = errorResponse.getStatusCode();
//        Toast.makeText(getApplicationContext(), "Hello status code is "+statusCode, Toast.LENGTH_SHORT).show();
//        if(statusCode == 200){
//            //Do something...
//        }else{
//            //Do something else...
//        }
    }
}