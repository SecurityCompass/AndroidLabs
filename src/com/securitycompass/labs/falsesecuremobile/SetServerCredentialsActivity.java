package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetServerCredentialsActivity extends Activity {
    
    private EditText mUserField;
    private EditText mPasswordField;
    private Button mDoneButton;
    private Context mCtx;
    private BankingApplication mThisApplication;
    
    private static final String TAG="SetServerCredentialsActivity";
    
    /** Called when the Activity is first created */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setservercredentialsactivity);
        
        mCtx=this;
        mThisApplication=(BankingApplication) getApplication();
        
        mUserField=(EditText) findViewById(R.id.setservercredentialsactivity_username);
        mPasswordField=(EditText) findViewById(R.id.setservercredentialsactivity_password);
        mDoneButton=(Button) findViewById(R.id.setservercredentialsactivity_donebutton);
        
        mDoneButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                setServerCredentials();
            }
        });

    }
    
    private void setServerCredentials(){
        String username=mUserField.getText().toString();
        String password=mPasswordField.getText().toString();
        
        int statuscode;
        try{
            statuscode=mThisApplication.performLogin(username, password);
        } catch (JSONException e){
            Toast.makeText(mCtx, R.string.error_toast_json_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
            return;
        } catch (IOException e){
            Toast.makeText(mCtx, R.string.error_toast_rest_problem, Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
            return;
        }
        
        if(statuscode==RestClient.NULL_ERROR){
            mThisApplication.setServerCredentials(username, password);
            Editor e=mThisApplication.getSharedPrefs().edit();
            e.putBoolean(BankingApplication.PREF_FIRST_RUN, false);
            e.commit();
            mThisApplication.lockApplication();
            Intent i=new Intent(mCtx, SetLocalPasswordActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(mCtx, R.string.toast_loginfailed, Toast.LENGTH_SHORT).show();
        }
    }
}
