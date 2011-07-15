/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.androidlabs.secureloggingsolution;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * A superclass containing methods and variables that all Activities in this application will use
 * @author Ewan Sinclair
 */
public class BankingListActivity extends ListActivity {
    
    /** Central data store, state, and operations */
    protected BankingApplication mThisApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisApplication = (BankingApplication) getApplication();
        if (mThisApplication.isLocked()) {
            launchLoginScreen();
        }
    }

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

    /**
     * Checks whether the application is locked and makes this Activity invisible if so.
     */
    public void setAppropriateVisibility() {
        View v = findViewById(R.id.root_view);
        if (v != null) {
            if (mThisApplication.isLocked()) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Makes this Activity invisible.
     */
    public void setInvisible() {
        View v = findViewById(R.id.root_view);
        if (v != null) {
                v.setVisibility(View.GONE);
            } 

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAppropriateVisibility();
        mThisApplication.registerActivityForegrounded();
        if (mThisApplication.isLocked()) {
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setInvisible();
        mThisApplication.registerActivityBackgrounded();
    }

    private void resetApplication() {
        BankingApplication ba = (BankingApplication) getApplication();
        ba.clearStatements();
        Editor e = ba.getSharedPrefs().edit();
        e.clear();
        e.commit();
        ba.lockApplication();
        launchLoginScreen();
    }

    private void launchPreferenceScreen() {
        Intent i = new Intent(this, EditPreferencesActivity.class);
        startActivity(i);
    }

    /** Launches the accounts screen, doing any necessary processing first */
    protected void launchLoginScreen() {
        Intent launchLogin = new Intent(this, LoginActivity.class);
        launchLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchLogin);
    }

    /** Launches the accounts screen, doing any necessary processing first */
    protected void authenticate() {
        Intent launchLogin = new Intent(this, LoginActivity.class);
        launchLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchLogin);
    }

}
