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
import android.widget.EditText;

/*
 * Graphical class which allows the user to enter a username and password, 
 * then perform a login with them.
 */
public class LoginActivity extends Activity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    /** The button that initiates the login */
    private Button mLoginButton;
    /** The text field to collect/hold the username. */
    private EditText mUsernameField;
    /** The text field to collect/hold the password. */
    private EditText mPasswordField;
    /** The REST client used to perform network operations */
    private RestClient mRestClient;
    
    //TODO: Decide whether this needs to be a member field, or if we can just call ApplicationState.getInstance() when we need it.
    /** Central data store, state, and operations */
    private ApplicationState mApplicationState;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCtx=this;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        mRestClient=new RestClient(ApplicationState.getInstance());
        
        mLoginButton = (Button) findViewById(R.id.loginscreen_login_button);
        mUsernameField= (EditText) findViewById(R.id.loginscreen_username);
        mPasswordField= (EditText) findViewById(R.id.loginscreen_password);
        
        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        
        populateCredentialFields();

    }
    
    private void populateCredentialFields(){
        //TODO: Populate username and password from the DB
    }

    /** Grabs the username and password and attempts to log in with them */
    private void performLogin() {
        String username=mUsernameField.getText().toString();
        String password=mPasswordField.getText().toString();
        
        System.err.println("Logging in with user/pass: " + username + " / " + password);
        String loginResult=mRestClient.performHTTPLogin(ApplicationState.getInstance().getRestServer(), username, password);
        
        Intent launchIntent = new Intent(mCtx, SummaryActivity.class);
        startActivity(launchIntent);
    }

}