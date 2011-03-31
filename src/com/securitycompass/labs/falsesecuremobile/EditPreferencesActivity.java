package com.securitycompass.labs.falsesecuremobile;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class EditPreferencesActivity extends PreferenceActivity {

    private BankingApplication mThisApplication;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisApplication=(BankingApplication)getApplication();
        addPreferencesFromResource(R.xml.userpreferences);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mThisApplication.registerActivityBackgrounded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThisApplication.registerActivityForegrounded();
    }

    

}
