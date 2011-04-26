/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.content.Intent;
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

/**
 * Displays a list of statements, each of which can be clicked to view it.
 * @author Ewan Sinclair
 */
public class StatementActivity extends BankingListActivity {

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
        setAppropriateVisibility();

        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();

        mClearButton = (Button) findViewById(R.id.statementscreen_clear_button);

        mStatementsDir = getFilesDir();
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
                Intent intent = new Intent(mCtx, ViewStatementActivity.class);
                intent.putExtra("statement_filename", mStatements[position].getAbsolutePath());
                startActivity(intent);
            }
        });

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
        }  catch (KeyManagementException e) {
            Toast.makeText(mCtx, R.string.error_ssl_keymanagement, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(mCtx, R.string.error_ssl_algorithm, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (AuthenticatorException e){
            Log.e(TAG, e.toString());
            authenticate();  
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
      //The list will now display with the most recent at the top
        Collections.reverse(filteredStatements);
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

            int formatFlags = DateUtils.LENGTH_MEDIUM | DateUtils.FORMAT_24HOUR
                    | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_SHOW_TIME;
            String formattedDateString = DateUtils.formatDateTime(mCtx, timeStamp, formatFlags);

            view.setText(formattedDateString);
            return view;
        }

    }

}