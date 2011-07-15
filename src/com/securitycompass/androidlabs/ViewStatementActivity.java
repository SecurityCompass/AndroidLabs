/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.androidlabs.filepermissionssolution;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Screen which displays a statement file.
 * @author Ewan Sinclair
 */
public class ViewStatementActivity extends BankingActivity {

    WebView mStatementDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewstatementactivity);
        setAppropriateVisibility();

        mStatementDisplay = (WebView) findViewById(R.id.viewstatementscreen_webview);

        Intent i = getIntent();
        if (i.hasExtra("statement_filename")) {
            String filename = i.getStringExtra("statement_filename");
            mStatementDisplay.loadUrl("file://" + filename);
        } else {
            Toast.makeText(this, R.string.error_invalid_statment, Toast.LENGTH_SHORT).show();
        }

    }

}
