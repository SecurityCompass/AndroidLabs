package com.securitycompass.androidlabs.base;

import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity {
    
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        // start this with
        // activity > dump.txt
        // # am start com.securitycompass.androidlabs.base/.ContactActivity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactactivity);
       
        // initialize webview
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);       
        myWebView.loadUrl("http://www.google.com");

        // check EMM scheme for intent-filter
        try {
            Uri data = getIntent().getData();
            List<String> params = data.getPathSegments();
            if (!params.isEmpty()) {
                String first = params.get(0); // "status"        
            
                // we will toast the EMM scheme
                Context context = getApplicationContext();
                CharSequence text = first;
                int duration = Toast.LENGTH_LONG;
    
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        } catch(Exception e) {
            Log.d("EMM", "No intent provided");
        }
       
    }    
    
    
}
