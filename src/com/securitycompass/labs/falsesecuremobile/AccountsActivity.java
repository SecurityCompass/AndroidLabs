/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class AccountsActivity extends Activity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    /** The place where we'll show the account info */
    private TextView mInformationArea;
    /** Central data store, state, and operations */
    private BankingApplication mThisApplication;
    /** A list of the accounts for this user */
    private List<Account> mAccounts;

    private static final String TAG = "AccountsActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsactivity);
        
        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();
        mInformationArea = (TextView) findViewById(R.id.accountsscreen_text_summary);
        
        updateAccounts();
    }

    /** Updates the account information stored locally and refreshes the display */ 
    private void updateAccounts() {
        try {
            mAccounts=mThisApplication.getAccounts();
        } catch (JSONException e) {
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (IOException e){
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (AuthenticatorException e){
            Log.e(TAG, e.toString());
            authenticate();
        }
        refreshDisplayInformation();
    }

    /** Called when the app needs authentication, normally due to a session timeout */
    private void authenticate(){
        Intent i=new Intent(mCtx, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    
    /** Updates the display to reflect the currently held account information */
    private void refreshDisplayInformation() {
        String accountsDetails = "";
        int count=1;
        for (Account a : mAccounts) {
            accountsDetails += "Account " + count++ + "\n" ;
            accountsDetails += "\t" + a.getAccountType() + "\n";
            //accountsDetails += "\t" + a.getAccountNumber() + "\n";
            accountsDetails += "\t" + a.getBalance() + "\n\n";
        }
        
        mInformationArea.setText(accountsDetails);
    }

}
