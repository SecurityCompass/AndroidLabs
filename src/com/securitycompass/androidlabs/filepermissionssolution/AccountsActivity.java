/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.androidlabs.filepermissionssolution;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays a full summary of all the accounts
 * @author Ewan Sinclair
 */
public class AccountsActivity extends BankingListActivity {

    /** Useful for avoiding casts when a Context needs to be passed */
    private Context mCtx;
    /** Central data store, state, and operations */
    private BankingApplication mThisApplication;
    /** A list of the accounts for this user */
    private List<Account> mAccounts;
    
    private static final String TAG = "AccountsActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsactivity);
        setAppropriateVisibility();

        mCtx = this;
        mAccounts=new ArrayList<Account>(); //To avoid null reference on network error
        
        mThisApplication = (BankingApplication) getApplication();
        
        updateAccounts();
        
        setListAdapter(new AccountDetailAdapter(mCtx, R.layout.accountdetailsview, mAccounts));
        
    }

    /** Updates the account information stored locally and refreshes the display */
    private void updateAccounts() {
        try {
            mAccounts = mThisApplication.getAccounts();
        } catch (JSONException e) {
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }  catch (AuthenticatorException e) {
            Log.e(TAG, e.toString());
            authenticate();
        } catch (KeyManagementException e){
            Toast.makeText(mCtx, R.string.error_ssl_keymanagement, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (NoSuchAlgorithmException e){
            Toast.makeText(mCtx, R.string.error_ssl_algorithm, Toast.LENGTH_LONG).show();
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }

        //If the account list failed on retrieval, use an empty list
        if (!mThisApplication.isLocked()) {
            //refreshDisplayInformation();
        } else {
            mAccounts=new ArrayList<Account>();
        }
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

    private class AccountDetailAdapter extends ArrayAdapter<Account>{
        
        public AccountDetailAdapter(Context context, int viewResourceId, List<Account> accounts) {
            super(context, viewResourceId, accounts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View item=convertView;
            
            if (item == null) {
                LayoutInflater inflater = getLayoutInflater();
                item = inflater.inflate(R.layout.accountdetailsview, null);
            }
            
            TextView accountNumber = (TextView) item.findViewById(R.id.accounts_screen_accountnumber);
            TextView accountType = (TextView) item.findViewById(R.id.accounts_screen_accounttype);
            TextView accountBalance = (TextView) item.findViewById(R.id.accounts_screen_accountbalance);
            
            Account displayAccount=mAccounts.get(position);
            accountNumber.setText("Account " + displayAccount.getAccountNumber());
            accountType.setText(capitalise(displayAccount.getAccountType()) + " Account");
            accountBalance.setText("Balance: $" + displayAccount.getBalance());
            
            return item;
        }
        
        
    }
    
}
