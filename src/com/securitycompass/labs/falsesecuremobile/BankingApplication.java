/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.labs.falsesecuremobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import org.json.JSONException;

import android.accounts.AuthenticatorException;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;

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

    // How many hashing iterations to perform
    private static final int HASH_ITERATIONS = 1000;

    //Where we'll store statements
    public static final String STATEMENT_DIR = "/sdcard/falsesecuremobile/";
    
    /* These variables are used for anchoring preference keys */
    public static final String SHARED_PREFS = "preferences";
    public static final String PREF_FIRST_RUN = "firstrun";
    public static final String PREF_LOCALPASS_HASH = "localpasshash";
    public static final String PREF_LOCALPASS_SALT = "localpasssalt";
    public static final String PREF_REST_USER = "serveruser";
    public static final String PREF_REST_PASSWORD = "serverpass";

    /** Setup for when the application initialises. */
    @Override
    public void onCreate() {
        super.onCreate();
        dbA = new DatabaseAdapter(getApplicationContext());
    }

    /**
     * Returns a string representation of the server we will be making our requests on.
     * @return A string representation of the server address.
     */
    public String getRestServer() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("bankserviceaddress", "10.0.2.2");
    }

    /**
     * Sets the address of the server we'll use for REST queries, as a String.
     * @param newServer The server address to set.
     */
/*    public void setRestServer(String newServer) {
        restServer = newServer;
    }*/

    /**
     * Returns a string representation of the port we will be making our HTTP requests on.
     * @return A string representation of the port set for HTTP communication.
     */
    public String getHttpPort() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("httpport", "8080");
        }

    /**
     * Returns the directory where statements are kept, as a String.
     * @return The directory where statements are kept, as a String.
     */
    public String getStatementDir(){
        return STATEMENT_DIR;
    }
    
    /**
     * Sets the local password, accomplished by storing a hashcode.
     * @param password The plain String version of the password to set.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public void setLocalPassword(String password) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        // First we generate a random salt
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] saltByteArray = new byte[32];
        random.nextBytes(saltByteArray);

        // Perform the hash, getting a Base64 encoded String
        String hashString = hash(password, saltByteArray);

        // Base64 encode the salt, store our strings
        byte[] b64Salt = Base64.encode(saltByteArray, Base64.DEFAULT);
        String saltString = new String(b64Salt);

        Editor e=getSharedPrefs().edit();
        e.putString(PREF_LOCALPASS_HASH, hashString);
        e.putString(PREF_LOCALPASS_SALT, saltString);
        e.commit();
    }

    /**
     * Checks to see if the given password hashes to the same value as the stored one.
     * @param enteredPassword The password to check.
     * @return Whether the password matched.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public boolean checkPassword(String enteredPassword) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String saltString = getSharedPrefs().getString(PREF_LOCALPASS_SALT, "");
        byte[] saltBytes = Base64.decode(saltString, Base64.DEFAULT);
        String hashOfEnteredPassword = hash(enteredPassword, saltBytes);
        String hashOfStoredpassword = getSharedPrefs().getString(PREF_LOCALPASS_HASH, "");
        return hashOfEnteredPassword.equals(hashOfStoredpassword);
    }

    /**
     * Peforms a bunch of hash iterations on the given String and salt and returns the result.
     * @param password String password to hash.
     * @param salt The salt to hash with.
     * @return A base64 encoded string representing the hash.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String hash(String password, byte[] salt) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        byte[] passwordBytes = (password).getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        md.update(salt);
        byte[] hashed = md.digest(passwordBytes);

        for (int count = 0; count < HASH_ITERATIONS; count++) {
            md.reset();
            md.update(salt);
            hashed = md.digest(hashed);
        }

        byte[] b64Hash = Base64.encode(hashed, Base64.DEFAULT);
        return new String(b64Hash);

    }

    /**
     * Returns the shared preferences for this app, with permission mode pre-set.
     * @return The shared preferences for this app, with permission mode pre-set.
     */
    public SharedPreferences getSharedPrefs() {
        return getSharedPreferences(SHARED_PREFS, MODE_WORLD_READABLE);
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
        locked = (statusCode == RestClient.NULL_ERROR) ? false : true;
        return statusCode;
    }

    /** Performs all operations necessary to secure the application. */
    public void lockApplication() {
        locked = true;
    }

    /** 
     * Performs all operations necessary to make the application usable.
     * @param password The password to try unlocking with
     * @return Whether the operation suceeded
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws JSONException
     */
    public int unlockApplication(String password) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, IOException, JSONException {
        if (checkPassword(password)) {
            String user=getRestUsername();
            String pass=getRestPassword();
            int statusCode=performLogin(user, pass);
            if(statusCode==RestClient.NULL_ERROR){
                locked = false;
                return statusCode;
            }
        }
        return RestClient.NO_OP;
    }

    /**
     * Returns the application's state of lockdown.
     * @return The application's state of lockdown.
     */
    public boolean isLocked() {
        return locked;
    }

    /** 
     * Returns the stored username for the REST service.
     * @return The stored username for the REST service.
     */
    public String getRestUsername(){
        return getSharedPrefs().getString(PREF_REST_USER, "");
    }

    /** 
     * Returns the stored password for the REST service.
     * @return The stored password for the REST service.
     */
    public String getRestPassword(){
        return getSharedPrefs().getString(PREF_REST_PASSWORD, "");
    }
    
    /**
     * Sets the user's credentials for the banking service.
     * @param username The username to set.
     * @param password The password to set.
     */
    public void setServerCredentials(String username, String password) {
        Editor e=getSharedPrefs().edit();
        e.putString(PREF_REST_USER, username);
        e.putString(PREF_REST_PASSWORD, password);
        e.commit();
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

    /**
     * Downloads a statement and displays it.
     * @param caller The activity calling this method. This is needed to start a new Activity from
     * within this class.
     * @return A status code representing the REST server's response.
     * @throws IOException
     */
    public int downloadStatement() throws IOException {
        // TODO: Put most of this method in RestClient.
        RestClient restClient = new RestClient(this);
        String htmlData = restClient.getHttpContent("http://" + getRestServer() + ":"
                + getHttpPort() + "/statement" + "?session_key=" + URLEncoder.encode(sessionKey));

        int statusCode = restClient.parseError(htmlData);

        if (statusCode == RestClient.NULL_ERROR) {

            File outputFile = new File(STATEMENT_DIR, Long.toString(System.currentTimeMillis()) + ".html");
            File outputDir = new File(STATEMENT_DIR);
            outputDir.mkdirs();

            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(htmlData.getBytes());
            out.flush();
            out.close();
        }

        return statusCode;
    }
    
    /**
     * Clears all statements from the download directory
     */
    public void clearStatements(){
        File downloadDir=new File(STATEMENT_DIR);
        File[] directoryContents=downloadDir.listFiles();
        for(File f : directoryContents){
            f.delete();
        }
    }

    /**
     * Transfers money between accounts
     * @param fromAccount The account to take funds from.
     * @param toAccount The account in which to deposit the funds.
     * @param amount The amount to transfer.
     * @return A status code representing the server's repsonse
     * @throws IOException
     */
    public int transferFunds(int fromAccount, int toAccount, double amount) throws IOException {
        RestClient restClient = new RestClient(this);
        int statusCode = restClient.httpTransfer(getRestServer(), getHttpPort(), fromAccount,
                toAccount, amount, sessionKey);
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