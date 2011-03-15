package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.app.Application;
import android.content.Intent;

/**
 * Stores session keys and handles moving the application between locked and unlocked states.
 * Instantiated when the application loads.
 * @author Ewan Sinclair
 */
public class BankingApplication extends Application {

    private String sessionKey;
    private String sessionCreateDate;

    DatabaseAdapter dbA;

    /** Setup for when the application initialises */
    @Override
    public void onCreate() {
        super.onCreate();
        dbA = new DatabaseAdapter(getApplicationContext());
    }

    /** Teardown to be performed when the application terminates */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Returns a string representation of the server we will be making our requests on.
     * @return A string representation of the server address
     */
    public String getRestServer() {
        // TODO: Remove this awful hack!
        return "192.168.1.60";
    }

    /**
     * Returns a string representation of the port we will be making our HTTP requests on.
     * @return A string representation of the port set for HTTP communication
     */
    public String getHttpPort() {
        // TODO: Remove this awful hack!
        return "8080";
    }

    /**
     * Logs into the REST service, generating a new session key
     * @param username Username to log in with
     * @param password Password to log in with
     * @return A  status code representing any error that occurred
     */
    public int performLogin(String username, String password) throws JSONException, IOException {
        RestClient restClient = new RestClient(this);
        int statusCode = restClient.performHTTPLogin(getRestServer(), getHttpPort(), username,
                password);
        return statusCode;
    }
    
    /** Returns a list of all Accounts and their details*/
    public List<Account> getAccounts() throws JSONException, IOException {
        RestClient restClient=new RestClient(this);
        return restClient.httpGetAccounts(getRestServer(), getHttpPort());
    }
    
    /** Prompts user to enter their authentication credentials */
    public void authenticate(){
        Intent i=new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
    

    public void setSession(String key, String date) {
        sessionKey = key;
        sessionCreateDate = date;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getSessionCreateDate() {
        return sessionCreateDate;
    }

}