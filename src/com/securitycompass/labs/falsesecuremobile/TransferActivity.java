/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TransferActivity extends BankingActivity {

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
    /** The button to trigger the transfer. */
    private Button mTransferButton;
    /** The field in which the amount to transfer will be entered */
    private EditText mAmountField;

    /** The adapter we'll attach the 'from' spinner to. */
    private AccountListAdapter mFromAccountListAdapter;
    /** The adapter we'll attach the 'to' spinner to. */
    private AccountListAdapter mToAccountListAdapter;
    /** Holds the currently selected account to transfer funds from. */
    private Account mFromAccount;
    /** Holds the currently selected account to transfer funds to. */
    private Account mToAccount;

    private final static int TRANSFER_FROM = 1;
    private final static int TRANSFER_TO = 2;
    private static final String TAG = "TransferActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transferactivity);
        setAppropriateVisibility();

        mCtx = this;
        mThisApplication = (BankingApplication) getApplication();

        mAccounts = new ArrayList<Account>();

        // Set up the dropdown account selectors
        mFromAccountSpinner = (Spinner) findViewById(R.id.transferscreen_fromaccount_spinner);
        mToAccountSpinner = (Spinner) findViewById(R.id.transferscreen_toaccount_spinner);

        mFromAccountListAdapter = new AccountListAdapter();
        mToAccountListAdapter = new AccountListAdapter();

        updateAccounts();

        mFromAccountSpinner.setAdapter(mFromAccountListAdapter);
        mToAccountSpinner.setAdapter(mToAccountListAdapter);

        mFromAccountSpinner.setOnItemSelectedListener(new AccountSelectionListener(TRANSFER_FROM));
        mToAccountSpinner.setOnItemSelectedListener(new AccountSelectionListener(TRANSFER_TO));

        // Set up the button to make it all go, and the amount field
        mTransferButton = (Button) findViewById(R.id.transferscreen_transfer_button);

        mTransferButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performTransfer();

            }
        });

        mAmountField = (EditText) findViewById(R.id.transferscreen_enteramount_field);

        if (mAccounts.size() >= 2) {
            mToAccountSpinner.setSelection(1);
        }
        refreshDisplayInformation();

    }

    /** Updates the account information stored locally and refreshes the display */
    private void updateAccounts() {
        try {
            // We can't just replace this with a new list, as there may be references held to it.
            List<Account> tempAccounts = mThisApplication.getAccounts();
            mAccounts.clear();
            for (Account a : tempAccounts) {
                mAccounts.add(a);
            }
        } catch (JSONException e) {
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        } catch (AuthenticatorException e) {
            Log.e(TAG, e.toString());
            authenticate();
        } catch (KeyManagementException e) {
            Toast.makeText(mCtx, R.string.error_ssl_keymanagement, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(mCtx, R.string.error_ssl_algorithm, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        }

        // If the account list failed on retrieval, use an empty list
        if (!mThisApplication.isLocked()) {
            refreshDisplayInformation();
        } else {
            mAccounts.clear();
        }
    }

    /**
     * Pulls the currently entered information from the UI and performs a transfer using the set
     * values
     */
    private void performTransfer() {
        // TODO: Check status code and act accordingly if an error is present.
        Log.i(TAG, "Member Accounts [" + mFromAccount.toString() + "] [" + mToAccount.toString()
                + "]");
        if (mFromAccount == mToAccount) {
            Toast.makeText(mCtx, R.string.error_transfer_same_account, Toast.LENGTH_SHORT).show();
        } else {
            String amountText = mAmountField.getText().toString();
            double amount = 0;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                Toast.makeText(mCtx, R.string.error_transfer_invalid_amount, Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            if (amount > 0) {
                try {
                    int responseCode = mThisApplication.transferFunds(mFromAccount
                            .getAccountNumber(), mToAccount.getAccountNumber(), amount);
                    Log.i(TAG, "Transferred. Response code: " + responseCode);
                } catch (KeyManagementException e) {
                    Toast.makeText(mCtx, R.string.error_ssl_keymanagement, Toast.LENGTH_LONG)
                            .show();
                    Log.e(TAG, e.toString());
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(mCtx, R.string.error_ssl_algorithm, Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.toString());
                } catch (HttpException e) {
                    Toast.makeText(mCtx, R.string.error_toast_http_error + e.getStatusCode(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                } catch (IOException e) {
                    Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT)
                            .show();
                    Log.e(TAG, e.toString());
                }

                updateAccounts();
            } else {
                Toast.makeText(mCtx, R.string.error_transfer_invalid_amount, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /** Updates the display to reflect the currently held account information. */
    private void refreshDisplayInformation() {
        /*
         * Simply calling notifyDataSetChanged() on the adapters here doesn't work, as the
         * mFromAccount reference remains set to the same as it was before the transaction, E.G. the
         * same type and account number, but with the earlier balance. Clearly the Spinner's
         * OnItemSelected() method isn't triggered when calling notifyDataSetChanged().
         */

        int fromPos = mFromAccountSpinner.getSelectedItemPosition();
        int toPos = mToAccountSpinner.getSelectedItemPosition();

        mFromAccountListAdapter = new AccountListAdapter();
        mToAccountListAdapter = new AccountListAdapter();

        mFromAccountSpinner.setAdapter(mFromAccountListAdapter);
        mToAccountSpinner.setAdapter(mToAccountListAdapter);

        mFromAccountSpinner.setSelection(fromPos);
        mToAccountSpinner.setSelection(toPos);

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

        public AccountSelectionListener(int direction) {
            super();
            transferDirection = direction;
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
            String accountNumString = Integer.toString(mAccounts.get(position).getAccountNumber());
            view.setText(capitalise(mAccounts.get(position).getAccountType()) + " ("
                    + accountNumString.substring(accountNumString.length() - 4) + "): $"
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
            String accountNumString = Integer.toString(mAccounts.get(position).getAccountNumber());
            view.setText(capitalise(mAccounts.get(position).getAccountType()) + " ("
                    + accountNumString.substring(accountNumString.length() - 4) + "): $"
                    + mAccounts.get(position).getBalance());
            return view;
        }

    }

}