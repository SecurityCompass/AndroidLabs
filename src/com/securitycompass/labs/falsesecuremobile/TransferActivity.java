/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TransferActivity extends Activity {

    /** Useful for avoiding casts when a Context needs to be passed. */
    private Context mCtx;
    /** Central data store, state, and operations. */
    private BankingApplication mThisApplication;
    /** A list of the accounts for this user. */
    private List<Account> mAccounts;
    /** The dropdown selector for the 'from' account. */
    private Spinner mFromAccountSpinner;
    /** The dropdown selector for the 'to' account. */
    private Spinner mToAccountSpinner;
    /** The adapter we'll attach the 'from' spinner to. */
    private AccountListAdapter mFromAccountListAdapter;
    /** The adapter we'll attach the 'to' spinner to. */
    private AccountListAdapter mToAccountListAdapter;
    /** Holds the currently selected account to transfer funds from */
    private Account mFromAccount;
    /** Holds the currently selected account to transfer funds to */
    private Account mToAccount;

    private final static int TRANSFER_FROM = 1;
    private final static int TRANSFER_TO = 2;
    private static final String TAG = "TransferActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transferactivity);

        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();

        mFromAccountSpinner = (Spinner) findViewById(R.id.transferscreen_fromaccount_spinner);
        mToAccountSpinner = (Spinner) findViewById(R.id.transferscreen_toaccount_spinner);

        mFromAccountListAdapter = new AccountListAdapter();
        mToAccountListAdapter = new AccountListAdapter();

        updateAccounts();

        mFromAccountSpinner.setAdapter(mFromAccountListAdapter);
        mToAccountSpinner.setAdapter(mToAccountListAdapter);

        mFromAccountSpinner.setOnItemSelectedListener(new AccountSelectionListener(TRANSFER_FROM));
        mToAccountSpinner.setOnItemSelectedListener(new AccountSelectionListener(TRANSFER_TO));

        refreshDisplayInformation();

    }

    /** Updates the account information stored locally and refreshes the display */
    private void updateAccounts() {
        try {
            mAccounts = mThisApplication.getAccounts();
        } catch (JSONException e) {
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (AuthenticatorException e) {
            Log.e(TAG, e.toString());
            authenticate();
        }

        // If the account list failed on retrieval, use an empty list
        if (!mThisApplication.isLocked()) {
            refreshDisplayInformation();
        } else {
            mAccounts = new ArrayList<Account>();
        }
    }

    /**
     * Called when the app needs authentication, normally due to a session timeout. The current
     * activity stack will be cleared, and the login Activity brought to the front.
     */
    private void authenticate() {
        Intent i = new Intent(mCtx, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /** Updates the display to reflect the currently held account information. */
    private void refreshDisplayInformation() {
        mFromAccountListAdapter.notifyDataSetChanged();
        mToAccountListAdapter.notifyDataSetChanged();
    }

    /**
     * Returns a version of the given string with the first letter in uppercase.
     * @param input The String to capitalise.
     * @return The capitalised String.
     */
    private String capitalise(String input) {
        String result = input.substring(0, 1).toUpperCase() + input.substring(1);
        return result;
    }

    private class AccountSelectionListener implements OnItemSelectedListener {

        private int transferDirection;
        
        public AccountSelectionListener(int direction){
            super();
            transferDirection=direction;
        }
        
        @Override
        public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id)
                throws IndexOutOfBoundsException {
            if (transferDirection == TRANSFER_FROM) {
                mFromAccount = mAccounts.get(position);
                System.err.println("Selected from account: " + mFromAccount);
            } else if (transferDirection == TRANSFER_TO) {
                mToAccount = mAccounts.get(position);
                System.err.println("Selected to account: " + mToAccount);
            } else
                throw new IndexOutOfBoundsException(
                        "From/To indicator int out of bounds in AccountSelectionListener");
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // Nothing to do here
        }

    }

    /** Helper class to present a List<Account> to a Spinner for selecting one */
    private class AccountListAdapter extends BaseAdapter implements SpinnerAdapter {

        @Override
        public int getCount() {
            return mAccounts.size();
        }

        @Override
        public Object getItem(int position) {
            return mAccounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView == null) {
                view = (TextView) getLayoutInflater().inflate(
                        (android.R.layout.simple_spinner_item), null);
            } else {
                view = (TextView) convertView;
            }
            view.setText(capitalise(mAccounts.get(position).getAccountType()) + ": "
                    + mAccounts.get(position).getBalance());
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView == null) {
                view = (TextView) getLayoutInflater().inflate(
                        (android.R.layout.simple_spinner_dropdown_item), null);
            } else {
                view = (TextView) convertView;
            }
            view.setText(capitalise(mAccounts.get(position).getAccountType()) + ": "
                    + mAccounts.get(position).getBalance());
            return view;
        }

    }

}