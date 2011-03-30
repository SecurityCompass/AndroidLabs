/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StatementActivity extends ListActivity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    private BankingApplication mThisApplication;
    private File mStatementsDir;
    private File[] mStatements;
    private Button mClearButton;
    private StatementAdapter mAdapter;

    private static final String TAG = "StatementActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statementactivity);

        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();

        mClearButton = (Button) findViewById(R.id.statementscreen_clear_button);

        mStatementsDir = new File(mThisApplication.getStatementDir());
        downloadStatement();
        readStatementFiles();

        mAdapter = new StatementAdapter(mCtx, android.R.layout.simple_list_item_1, mStatements);
        setListAdapter(mAdapter);

        mClearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mThisApplication.clearStatements();
                refreshView();
            }
        });

        getListView().setOnItemClickListener(new OnItemClickListener(

        ) {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Uri uri = Uri.parse("file://" + mStatements[position].getAbsolutePath());
                Intent intent = new Intent();
                intent.setData(uri);
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                intent.setAction(Intent.ACTION_VIEW);

                startActivity(intent);
            }
        });

    }

    /** Locks the application if this activity is backgrounded */
    @Override
    public void onStop() {
        super.onStop();
        // mThisApplication.lockApplication();
    }

    /**
     * Clears out all downloaded files, downloads the latest one, and refreshes the list.
     */
    private void refreshView() {
        downloadStatement();
        readStatementFiles();
        mAdapter = new StatementAdapter(mCtx, android.R.layout.simple_list_item_1, mStatements);
        setListAdapter(mAdapter);
    }

    /** Downloads the most recent statement */
    private void downloadStatement() {
        try {
            mThisApplication.downloadStatement();
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }
    }

    /** Looks in the statements directory and loads what look like statement filenames into the list */
    private void readStatementFiles() {
        File[] allFiles = mStatementsDir.listFiles();
        List<File> filteredStatements = new ArrayList<File>();
        for (File f : allFiles) {
            if (f.getName().matches("^[0-9]*\\.html")) {
                filteredStatements.add(f);
            }
        }
        mStatements = filteredStatements.toArray(new File[0]);
    }

    private class StatementAdapter extends ArrayAdapter<File> {

        public StatementAdapter(Context context, int textViewResourceId, File[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView != null) {
                view = (TextView) convertView;
            } else {
                view = (TextView) getLayoutInflater().inflate(android.R.layout.simple_list_item_1,
                        null);
            }

            // Extract the creation time of the file from its filename
            String timeStampString = mStatements[position].getName().replaceAll("\\.html", "");
            long timeStamp = Long.parseLong(timeStampString);
            Date fileDate = new Date(timeStamp);

            // String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
            int formatFlags = DateUtils.LENGTH_MEDIUM | DateUtils.FORMAT_24HOUR
                    | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_SHOW_TIME;
            String formattedDateString = DateUtils.formatDateTime(mCtx, timeStamp, formatFlags);

            view.setText(formattedDateString);
            return view;
        }

    }

}