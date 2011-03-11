/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


public class StatementActivity extends Activity {
    
    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        mCtx=this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statementactivity);
    }
    

}