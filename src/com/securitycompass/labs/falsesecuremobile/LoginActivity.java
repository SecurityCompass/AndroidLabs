/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
 * Graphical class which allows the user to enter a username and password, 
 * then perform a login with them.
 */
public class LoginActivity extends Activity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    /** The button that initiates the login */
    private Button mLoginButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCtx=this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        mLoginButton = (Button) findViewById(R.id.loginscreen_login_button);
        
        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

    }

    /** Grabs the username and password and attempts to log in with them */
    private void performLogin() {
        // Stub for UI navigation
        Intent launchIntent = new Intent(mCtx, SummaryActivity.class);
        startActivity(launchIntent);
    }

}