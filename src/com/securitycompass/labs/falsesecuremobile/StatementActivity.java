/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StatementActivity extends ListActivity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    private BankingApplication mThisApplication;
    
    private static final String[] optionNames={"Download Statement","Download Combined Statement"};
    
    private static final int DL_STATEMENT=0;
    private static final int DL_COMBINED_STATEMENT=1;

    private static final String TAG = "StatementActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statementactivity);

        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();
        
        ListAdapter la = new ArrayAdapter<String>(mCtx, android.R.layout.simple_list_item_1,
                optionNames);
        setListAdapter(la);

        getListView().setOnItemClickListener(new OnItemClickListener(

        ) {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                launchSelectedScreen(position);
            }
        });

    }
    
    private void launchSelectedScreen(int screenId) {
        if (screenId == DL_STATEMENT) {
            downloadStatement();
        }
        else if (screenId == DL_COMBINED_STATEMENT){
            downloadCombinedStatement();
        }
    }

    private void downloadStatement() {
        try {
            mThisApplication.downloadStatement(this);
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }

    }
    
    private void downloadCombinedStatement(){
        Toast.makeText(mCtx, "Not implemented", Toast.LENGTH_SHORT).show();
    }

}