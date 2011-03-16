/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;

import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;

/**
 * Stores session keys and handles moving the application between locked and unlocked states.
 * Instantiated when the application loads.
 * @author Ewan Sinclair
 */
public class BankingApplication extends Application {

    private String sessionKey;
    private String sessionCreateDate;
    private boolean locked;

    private String restServer = "10.0.2.2";

    DatabaseAdapter dbA;

    /** Setup for when the application initialises. */
    @Override
    public void onCreate() {
        super.onCreate();
        dbA = new DatabaseAdapter(getApplicationContext());
    }

    /** Teardown to be performed when the application terminates. */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Returns a string representation of the server we will be making our requests on.
     * @return A string representation of the server address.
     */
    public String getRestServer() {
        return restServer;
    }

    /**
     * Sets the address of the server we'll use for REST queries, as a String.
     * @param newServer The server address to set.
     */
    public void setRestServer(String newServer) {
        restServer = newServer;
    }

    /**
     * Returns a string representation of the port we will be making our HTTP requests on.
     * @return A string representation of the port set for HTTP communication.
     */
    public String getHttpPort() {
        // TODO: Remove this awful hack!
        return "8080";
    }

    /**
     * Logs into the REST service, generating a new session key.
     * @param username Username to log in with.
     * @param password Password to log in with.
     * @return A status code representing any error that occurred.
     */
    public int performLogin(String username, String password) throws JSONException, IOException {
        RestClient restClient = new RestClient(this);
        int statusCode = restClient.performHTTPLogin(getRestServer(), getHttpPort(), username,
                password);
        return statusCode;
    }

    /** Performs all operations necessary to secure the application. */
    public void lockApplication() {
        locked = true;
    }

    /** Performs all operations necessary to make the application usable. */
    public void unlockApplication() {
        locked = false;
    }

    /**
     * Returns a list of all Accounts and their details.
     * @return A list of the accounts returned by the server, represented as Account objects.
     */
    public List<Account> getAccounts() throws JSONException, IOException, AuthenticatorException {
        RestClient restClient = new RestClient(this);
        List<Account> result = null;
        try {
            result = restClient.httpGetAccounts(getRestServer(), getHttpPort());
        } catch (AuthenticatorException e) {
            lockApplication();
            throw e;
        }

        if (result != null) {
            // If the accounts were retrieved, update them in the DB
            dbA.updateAccounts(result);
        }
        return result;
    }

    public int downloadStatement(Activity caller) throws IOException {
        //TODO: Clean up this method. A lot.
        RestClient restClient = new RestClient(this);
        String htmlData = restClient.getHttpContent("http://" + getRestServer() + ":" + getHttpPort() + "/statement" + "?session_key=" + URLEncoder.encode(sessionKey));

        int statusCode = restClient.parseError(htmlData);

        if (statusCode == RestClient.NULL_ERROR) {
                       
            File outputFile = new File("/sdcard/", "statement.html");

            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(htmlData.getBytes());
            out.flush();
            out.close();
            
            Uri uri = Uri.parse("file://" + outputFile.getAbsolutePath());
            Intent intent = new Intent();
            intent.setData(uri);
            intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
            intent.setAction(Intent.ACTION_VIEW);

            caller.startActivity(intent);
            
        }

        return statusCode;
    }

    /**
     * Sets the details for the current authenticated session.
     * @param key The session key.
     * @param date A string representation of the date this key was issued.
     */
    public void setSession(String key, String date) {
        sessionKey = key;
        sessionCreateDate = date;
    }

    /**
     * Returns the current session key.
     * @return The current session key.
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     * Returns the creation date of the current session key.
     * @return A String representation of the creation date of the current session key.
     */
    public String getSessionCreateDate() {
        return sessionCreateDate;
    }

}