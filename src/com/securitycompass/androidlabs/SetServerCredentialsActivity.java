/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.androidlabs.filepermissionssolution;

import java.io.IOException;
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
 * One-time setup screen for the user to enter their credentials for the banking service.
 * @author Ewan Sinclair
 */
public class SetServerCredentialsActivity extends Activity {
    
    private EditText mUserField;
    private EditText mPasswordField;
    private Button mDoneButton;
    private Context mCtx;
    private BankingApplication mThisApplication;
    
    private static final String TAG="SetServerCredentialsActivity";
    
    /** Called when the Activity is first created */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setservercredentialsactivity);
        
        mCtx=this;
        mThisApplication=(BankingApplication) getApplication();
        
        mUserField=(EditText) findViewById(R.id.setservercredentialsactivity_username);
        mPasswordField=(EditText) findViewById(R.id.setservercredentialsactivity_password);
        mDoneButton=(Button) findViewById(R.id.setservercredentialsactivity_donebutton);
        
        mDoneButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                setServerCredentials();
            }
        });

    }
    
    private void setServerCredentials(){
        String username=mUserField.getText().toString();
        String password=mPasswordField.getText().toString();
        
        int statuscode=RestClient.NO_OP;
        try{
            statuscode=mThisApplication.performLogin(username, password);
        } catch (JSONException e){
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
            return;
        } catch (KeyManagementException e){
            Toast.makeText(mCtx, R.string.error_ssl_keymanagement, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (NoSuchAlgorithmException e){
            Toast.makeText(mCtx, R.string.error_ssl_algorithm, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (HttpException e) {
            Toast.makeText(mCtx, getString(R.string.error_toast_http_error) + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (IOException e){
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
            return;
        }
        
        if(statuscode==RestClient.NULL_ERROR){
            mThisApplication.lockApplication();
            Intent i=new Intent(mCtx, SetLocalPasswordActivity.class);
            i.putExtra("username", username);
            i.putExtra("password", password);
            startActivity(i);
        } else {
            Toast.makeText(mCtx, R.string.toast_loginfailed, Toast.LENGTH_SHORT).show();
        }
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
