/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;


public class AccountsActivity extends Activity {
    
    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    /** The place where we'll show the account info */
    private TextView mInformationArea;
    /** Central data store, state, and operations */
    private BankingApplication mThisApplication;
    /** A list of the accounts for this user */
    private List<Account> mAccounts;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        mCtx=this;
        mThisApplication=(BankingApplication) getApplication();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsactivity);
        updateAccounts();
    }
    
    private void updateAccounts(){
        mThisApplication.getAccounts();
    }

}
