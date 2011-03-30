package com.securitycompass.labs.falsesecuremobile;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/** This class prompts the user to set their local unlock password */
public class SetLocalPasswordActivity extends Activity {

    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private Button mDoneButton;
    private Context mCtx;
    private BankingApplication mThisApplication;
    
    /** Called when the Activity is first created */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setlocalpasswordactivity);
        
        mCtx=this;
        mThisApplication=(BankingApplication) getApplication();
        
        mPasswordField=(EditText) findViewById(R.id.setupunlockactivity_password);
        mConfirmPasswordField=(EditText) findViewById(R.id.setupunlockactivity_repeat_password);
        mDoneButton=(Button) findViewById(R.id.setupunlockactivity_donebutton);
        
        mDoneButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                grabAndSetPassword();
            }
        });
        
    }
    
    /** Grabs the two passwords entered, and sets them as the local unlock password if they match */
    private void grabAndSetPassword(){
        String pass1=mPasswordField.getText().toString();
        String pass2=mConfirmPasswordField.getText().toString();
        if(!pass1.equals(pass2)){
            Toast.makeText(mCtx, R.string.error_passwords_not_matching, Toast.LENGTH_SHORT).show();
        } else {
            try {
                mThisApplication.setLocalPassword(pass1);
            } catch (NoSuchAlgorithmException e){
                Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e){
                Toast.makeText(mCtx, R.string.error_toast_hasherror, Toast.LENGTH_LONG).show();
            }
            Toast.makeText(mCtx, R.string.initialsetup_success, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(mCtx, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
    
}
