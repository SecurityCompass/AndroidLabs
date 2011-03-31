package com.securitycompass.labs.falsesecuremobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BankingActivity extends Activity {

    protected BankingApplication mThisApplication;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisApplication=(BankingApplication)getApplication();
        if(mThisApplication.isLocked() && (this.getClass()!=LoginActivity.class)){
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
        if(mThisApplication.isLocked() && (this.getClass()!=LoginActivity.class)){
            Intent i=new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mThisApplication.lockApplication();
    }

    private void resetApplication() {
        BankingApplication ba = (BankingApplication) getApplication();
        Editor e = ba.getSharedPrefs().edit();
        e.clear();
        e.commit();
        ba.lockApplication();
        launchLoginScreen();
    }
    
    private void launchPreferenceScreen(){
        Intent i=new Intent(this, EditPreferencesActivity.class);
        startActivity(i);
    }
    
    /** Launches the accounts screen, doing any necessary processing first */
    private void launchLoginScreen() {
        Intent launchLogin = new Intent(this, LoginActivity.class);
        launchLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchLogin);
    }
    
}
