package com.securitycompass.labs.falsesecuremobile;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

public class EditPreferencesActivity extends PreferenceActivity {

    private BankingApplication mThisApplication;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisApplication=(BankingApplication)getApplication();
        addPreferencesFromResource(R.xml.userpreferences);
    }  

}
