/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Simple interface to allow the user to edit some preferences.
 * @author Ewan Sinclair
 */
public class EditPreferencesActivity extends PreferenceActivity {

    private BankingApplication mThisApplication;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisApplication=(BankingApplication)getApplication();
        addPreferencesFromResource(R.xml.userpreferences);
    }  

}