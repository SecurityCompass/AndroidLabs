/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StatementActivity extends Activity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    private BankingApplication mThisApplication;
    private Button mDownloadButton;

    private static final String TAG = "StatementActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statementactivity);

        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();

        mDownloadButton = (Button) findViewById(R.id.statementscreen_download_button);

        mDownloadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                downloadStatement();

            }
        });

    }

    private void downloadStatement() {
        try {
            mThisApplication.downloadStatement(this);
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }

    }

}