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

    @Override
    protected void onPause() {
        super.onPause();
        setAppropriateVisibility();
        mThisApplication.registerActivityBackgrounded();
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

    public void setAppropriateVisibility() {
        View v = findViewById(android.R.id.content);
        if (v != null) {
            if (mThisApplication.isLocked()) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }    

}
