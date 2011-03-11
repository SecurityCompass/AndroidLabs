/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SummaryActivity extends Activity {

    /* UI Buttons */
    private Button mAccountsButton;
    private Button mTransferButton;
    private Button mStatementButton;

    private Context mCtx;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summaryactivity);

        mCtx = this;

        mAccountsButton = (Button) findViewById(R.id.summaryscreen_accounts_button);
        mTransferButton = (Button) findViewById(R.id.summaryscreen_transfer_button);
        mStatementButton = (Button) findViewById(R.id.summaryscreen_statement_button);

        mAccountsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                launchAccountsScreen();
            }
        });
        
        mTransferButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                launchTransferScreen();
            }
        });
        
        mStatementButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
               launchStatementScreen();
                
            }
        });

    }

    /** Launches the accounts screen, doing any necessary processing first */
    private void launchAccountsScreen() {
        // Stub for UI navigation
        Intent launchAccounts = new Intent(mCtx, AccountsActivity.class);
        startActivity(launchAccounts);
    }

    /** Launches the transfer screen, doing any necessary processing first */
    private void launchTransferScreen() {
        // Stub for UI navigation
        Intent launchTransfer = new Intent(mCtx, TransferActivity.class);
        startActivity(launchTransfer);
    }

    /** Launches the statement screen, doing any necessary processing first */
    private void launchStatementScreen() {
        // Stub for UI navigation
        Intent launchTransfer = new Intent(mCtx, StatementActivity.class);
        startActivity(launchTransfer);
    }

}