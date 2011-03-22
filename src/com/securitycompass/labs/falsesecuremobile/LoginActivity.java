/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    /** The text field to collect/hold the password. */
    private EditText mPasswordField;
    /** Central data store, state, and operations */
    private BankingApplication mThisApplication;
    /** This application's preferences */
    private SharedPreferences mSharedPrefs;
    
    private static final String TAG="LoginActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCtx=this;
        
        mThisApplication=(BankingApplication) getApplication();
        
        super.onCreate(savedInstanceState);
        
        mSharedPrefs=mThisApplication.getSharedPrefs();
        checkFirstRun();
        
        setContentView(R.layout.loginactivity);
        
        mLoginButton = (Button) findViewById(R.id.loginscreen_login_button);
        mPasswordField= (EditText) findViewById(R.id.loginscreen_password);
        
        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        
        populatePasswordField();
        

    }
    
    /** Checks if the application is running for the first time, and sends the user to the appropriate setup if it is. */
    private void checkFirstRun(){
        if (mSharedPrefs.getBoolean(BankingApplication.PREF_FIRST_RUN, true)){
            Intent i=new Intent(mCtx, SetLocalPasswordActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
    
    //TODO: Remove this convenience method
    private void populatePasswordField(){
        mPasswordField.setText("c");
    }

    /** Grabs the username and password and attempts to log in with them */
    private void performLogin() {
        
        String password=mPasswordField.getText().toString();
        
        int unlockStatus=RestClient.NO_OP;
        
        try{
            unlockStatus=mThisApplication.unlockApplication(password);
        } catch (UnsupportedEncodingException e){
            Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (NoSuchAlgorithmException e){
            Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (JSONException e){
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }
        
        if(unlockStatus == RestClient.NULL_ERROR){
            Intent launchIntent = new Intent(mCtx, SummaryActivity.class);
            startActivity(launchIntent);   
        } else {
            Toast.makeText(mCtx, R.string.toast_loginfailed, Toast.LENGTH_SHORT).show();
        }
    }

}