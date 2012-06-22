package com.securitycompass.androidlabs.base;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class DebugActivity extends ListActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
    
        
        // temporary list
        String[] values = new String[] { "Users within EMM", "jdoe", "bsmith"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.debugactivity, R.id.label, values);
        setListAdapter(adapter);
    }
    
    
}
