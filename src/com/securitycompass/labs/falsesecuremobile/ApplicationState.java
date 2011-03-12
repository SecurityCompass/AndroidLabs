package com.securitycompass.labs.falsesecuremobile;

import android.content.Context;

/**
 * Stores session keys and handles moving the application between locked and unlocked states
 * 
 * @author Ewan Sinclair
 */
public class ApplicationState {

    private static ApplicationState instance;
    
    private String sessionKey;
    private String sessionCreateDate;
    
    private Context mCtx;    
    DatabaseAdapter dbA;
    
    /** Creates a new ApplicationState object */
    public ApplicationState(Context ctx){
        mCtx=ctx;
        dbA=new DatabaseAdapter(mCtx);
    }
    
    /** Logs into the REST service, generating a new session key
     * 
     * @param username Username to log in with
     * @param password Password to log in with
     * @return Whether the login was successful
     */
    public boolean performLogin(String username, String password){
        RestClient restClient=new RestClient(this);
        //POST to /login with parameters "username" and "password" 
        //JSON { "error" : "E1"} indiciates failure
        return false;
    }
    
}