/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class prompts the user to set their local unlock password
 * @author Ewan Sinclair
 */
public class SetLocalPasswordActivity extends Activity {

    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private Button mDoneButton;
    private Context mCtx;
    private BankingApplication mThisApplication;

    private String restUser;
    private String restPass;

    private final static String TAG = "SetLocalPasswordActivity";

    /** Called when the Activity is first created */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setlocalpasswordactivity);

        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();

        restUser = getIntent().getStringExtra("restUser");
        restPass = getIntent().getStringExtra("restPass");

        mPasswordField = (EditText) findViewById(R.id.setupunlockactivity_password);
        mConfirmPasswordField = (EditText) findViewById(R.id.setupunlockactivity_repeat_password);
        mDoneButton = (Button) findViewById(R.id.setupunlockactivity_donebutton);

        mDoneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                grabAndSetPassword();
            }
        });

    }

    /** Grabs the two passwords entered, and sets them as the local unlock password if they match */
    private void grabAndSetPassword() {
        String pass1 = mPasswordField.getText().toString();
        String pass2 = mConfirmPasswordField.getText().toString();
        if (!pass1.equals(pass2)) {
            Toast.makeText(mCtx, R.string.error_passwords_not_matching, Toast.LENGTH_SHORT).show();
        } else if (!isValidPassword(pass2)) {
            Toast.makeText(mCtx, R.string.error_weak_password, Toast.LENGTH_LONG).show();
        } else {
            try {
                mThisApplication.setCredentials(pass2, restUser, restPass);
                mThisApplication.unlockApplication(pass2);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
                Log.e(TAG, e.toString());
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
                Log.e(TAG, e.toString());
            } catch (JSONException e) {
                Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            } catch (HttpException e) {
                Toast.makeText(mCtx, getString(R.string.error_toast_http_error) + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            } catch (KeyManagementException e){
                Toast.makeText(mCtx, R.string.error_ssl_keymanagement, Toast.LENGTH_LONG).show();
                Log.e(TAG, e.toString());
            } catch (IOException e) {
                Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            } catch (GeneralSecurityException e) {
                Toast.makeText(mCtx, "Crypto failure", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }

            Toast.makeText(mCtx, R.string.initialsetup_success, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(mCtx, SummaryActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    /**
     * Checks a String to see if it is a valid password.
     * @param password The password to check.
     * @return Whether the password was strong enough.
     */
    public boolean isValidPassword(String password) {
        boolean valid = (password.matches(".*[A-Z].*") && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*") && password.matches(".*[@#$%\\^&+=!].*") && password
                .length() >= 6);
        return valid;
    }
    
    /** Called when an item is selected from the options menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.optionsmenu_reset:
            resetApplication();
            return true;
        case R.id.optionsmenu_preferences:
            launchPreferenceScreen();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /** Creates an options menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    /** Resets the application */ 
    private void resetApplication() {
        BankingApplication ba = (BankingApplication) getApplication();
        ba.clearStatements();
        Editor e = ba.getSharedPrefs().edit();
        e.clear();
        e.commit();
        ba.lockApplication();
        launchLoginScreen();
    }

    /** Launches the preferences screen */
    private void launchPreferenceScreen() {
        Intent i = new Intent(this, EditPreferencesActivity.class);
        startActivity(i);
    }
    
    /** Launches the accounts screen, doing any necessary processing first */
    private void launchLoginScreen() {
        Intent launchLogin = new Intent(this, LoginActivity.class);
        launchLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchLogin);
    }
    
}
