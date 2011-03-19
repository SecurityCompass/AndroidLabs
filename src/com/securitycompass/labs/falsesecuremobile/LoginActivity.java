/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    /** Central data store, state, and operations */
    private BankingApplication mThisApplication;
    
    private static final String TAG="LoginActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCtx=this;
        
        mThisApplication=(BankingApplication) getApplication();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        
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
        //TODO: Remove this convenience method
        mUsernameField.setText("jdoe");
        mPasswordField.setText("password");
    }

    /** Grabs the username and password and attempts to log in with them */
    private void performLogin() {
        String username=mUsernameField.getText().toString();
        String password=mPasswordField.getText().toString();
        
        System.err.println("Logging in with user/pass: " + username + " / " + password);
        int statusCode=RestClient.NULL_ERROR;
        try{
            statusCode=mThisApplication.performLogin(username, password);
            Log.i(TAG, "Login completed");
        } catch (JSONException e){
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
            return;
        } catch (IOException e){
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
            return;
        }
        if(statusCode == RestClient.NULL_ERROR){
            Intent launchIntent = new Intent(mCtx, SummaryActivity.class);
            startActivity(launchIntent);   
        } else {
            Toast.makeText(mCtx, R.string.toast_loginfailed, Toast.LENGTH_SHORT).show();
        }
    }

}