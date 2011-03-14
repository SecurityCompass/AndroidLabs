package com.securitycompass.labs.falsesecuremobile;

import android.content.Context;

/**
 * Stores session keys and handles moving the application between locked and unlocked states
 * 
 * @author Ewan Sinclair
 */
public class ApplicationState {

    //TODO: Figure out how this class can acquire and hold global application context
    
    private static ApplicationState instance;

    private String sessionKey;
    private String sessionCreateDate;

    private Context mCtx;
    DatabaseAdapter dbA;

    /** Creates a new ApplicationState object */
    protected ApplicationState(Context ctx) {
        mCtx = ctx;
        dbA = new DatabaseAdapter(mCtx);
    }
    
    /** No-arguments constructor */
    protected ApplicationState(){
        
    }

    /** Returns an instance of this class, with no context */ 
    public static ApplicationState getInstance() {
        if (instance == null) {
            return new ApplicationState();
        } else {
            return instance;
        }
    }
    
    /** Returns a string representation of the server we will be making our requests on. 
     * @return A string representation of the server address*/
    public String getRestServer(){
        //TODO: Remove this awful hack!
        return "192.168.1.60";
    }

    /**
     * Logs into the REST service, generating a new session key
     * 
     * @param username
     *            Username to log in with
     * @param password
     *            Password to log in with
     * @return Whether the login was successful
     */
    public boolean performLogin(String username, String password) {
        RestClient restClient = new RestClient(this);
        // POST to /login with parameters "username" and "password"
        // JSON { "error" : "E1"} indicates failure
        return false;
    }

}