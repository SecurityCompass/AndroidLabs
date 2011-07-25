/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.androidlabs.basicencryptionsolution;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLException;

import org.json.JSONException;

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

/**
 * Graphical class which allows the user to enter a username and password, 
 * then perform a login with them.
 * @author Ewan Sinclair
 */
public class LoginActivity extends BankingActivity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    /** The button that initiates the login */
    private Button mLoginButton;
    /** The text field to collect/hold the password. */
    private EditText mPasswordField;
    /** This application's preferences */
    private SharedPreferences mSharedPrefs;

    private static final String TAG = "LoginActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;

        mSharedPrefs = mThisApplication.getSharedPrefs();
        checkFirstRun();

        setContentView(R.layout.loginactivity);

        mLoginButton = (Button) findViewById(R.id.loginscreen_login_button);
        mPasswordField = (EditText) findViewById(R.id.loginscreen_password);

        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

    }

    /** Pushes the user to the setup screen whenever they land on this Activity without having setup the app. */
    @Override
    public void onResume(){
        super.onResume();
        checkFirstRun();
    }
    
    /**
     * Checks if the application is running for the first time, and sends the user to the
     * appropriate setup if it is.
     */
    private void checkFirstRun() {
        if (mSharedPrefs.getBoolean(BankingApplication.PREF_FIRST_RUN, true)) {
            Intent i = new Intent(mCtx, SetServerCredentialsActivity.class);
            startActivity(i);
        }
    }

    /** Grabs the username and password and attempts to log in with them */
    private void performLogin() {

        String password = mPasswordField.getText().toString();

        int unlockStatus = RestClient.NO_OP;

        try {
            unlockStatus = mThisApplication.unlockApplication(password);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (NoSuchAlgorithmException e) {
            if (e.toString().matches(".*SSL.*")) {
                Toast.makeText(mCtx, R.string.error_ssl_algorithm, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
            }
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (KeyManagementException e) {
            Toast.makeText(mCtx, R.string.error_ssl_keymanagement, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (SSLException e) {
            Toast.makeText(mCtx, R.string.error_ssl_general, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (HttpException e) {
            Toast.makeText(mCtx, getString(R.string.error_toast_http_error) + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (GeneralSecurityException e) {
            Toast.makeText(mCtx, "Crypto failure", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }

        if (unlockStatus == RestClient.NULL_ERROR) {
            Intent launchIntent = new Intent(mCtx, SummaryActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launchIntent);
        } else {
            Toast.makeText(mCtx, R.string.toast_loginfailed, Toast.LENGTH_SHORT).show();
        }
    }

}
