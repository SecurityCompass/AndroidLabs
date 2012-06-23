package com.securitycompass.androidlabs.base;

import java.net.URLDecoder;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity {
    
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        // start this with
        // activity > dump.txt
        // # am start -n com.securitycompass.androidlabs.base/.ContactActivity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactactivity);
       
        // initialize webview
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);       
        myWebView.loadUrl("file:///android_asset/emm_contact.html");
        myWebView.requestFocusFromTouch();
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient());
        
        // check EMM scheme for intent-filter, if it exists pre populate the user name to be friendly!
        try {
            Uri data = getIntent().getData();
            List<String> params = data.getPathSegments();
            if (!params.isEmpty()) {
                String name = URLDecoder.decode(params.get(0), "UTF-8"); // "get the first name of the user"        
            
                // this loads the name parameters into our local HTML file
                myWebView.loadUrl("file:///android_asset/emm_contact.html?name=" + name);

                
                
                // we will toast the EMM scheme
                Context context = getApplicationContext();
                CharSequence text = name;
                int duration = Toast.LENGTH_LONG;
    
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                
            }
        } catch(Exception e) {
            Log.d("EMM", "No intent provided");
        }
       
    }    
    
    
}
