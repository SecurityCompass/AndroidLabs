/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SummaryActivity extends BankingListActivity {

    private Context mCtx;
    private final String[] optionNames = { "Accounts", "Statement", "Transfer" };

    private static final int LAUNCH_ACCOUNTS = 0;
    private static final int LAUNCH_STATEMENT = 1;
    private static final int LAUNCH_TRANSFER = 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summaryactivity);

        mCtx = this;

        //Don't display if the application is locked
        if (!mThisApplication.isLocked()) {

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
    }

    private void launchSelectedScreen(int screenId) {
        if (screenId == LAUNCH_ACCOUNTS) {
            launchAccountsScreen();
        } else if (screenId == LAUNCH_STATEMENT) {
            launchStatementScreen();
        } else if (screenId == LAUNCH_TRANSFER) {
            launchTransferScreen();
        }
    }

    /** Launches the accounts screen, doing any necessary processing first */
    private void launchAccountsScreen() {
        Intent launchAccounts = new Intent(mCtx, AccountsActivity.class);
        startActivity(launchAccounts);
    }

    /** Launches the transfer screen, doing any necessary processing first */
    private void launchTransferScreen() {
        Intent launchTransfer = new Intent(mCtx, TransferActivity.class);
        startActivity(launchTransfer);
    }

    /** Launches the statement screen, doing any necessary processing first */
    private void launchStatementScreen() {
        Intent launchTransfer = new Intent(mCtx, StatementActivity.class);
        startActivity(launchTransfer);
    }

}