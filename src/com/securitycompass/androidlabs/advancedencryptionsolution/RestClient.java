/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.androidlabs.advancedencryptionsolution;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AuthenticatorException;
import android.util.Log;

/**
 * Handles HTTP/S communication with the banking service, abstracting it as a set of methods that
 * can be called by the rest of the application for any given banking function.
 * @author Ewan Sinclair
 */
public class RestClient {

    private static final String TAG = "RestClient";

    /*
     * These codes are used to indicate the status of transactions that have finished, mainly
     * representing error codes returned by the server
     */
    /** REST operation didn't happen. */
    public static final int NO_OP = -2;
    /** No error ocurred, transaction completed. */
    public static final int NULL_ERROR = -1;
    /** The banking service rejected the given username and password. */
    public static final int ERROR_CREDENTIALS = 1;
    /** The banking service rejected the session key. */
    public static final int ERROR_SESSION_KEY = 2;
    /** The account referenced in the request doesn't exist. */
    public static final int ERROR_ACCOUNT_NOT_EXIST = 3;
    /** The balance is too low to perform the given transaction. */
    public static final int ERROR_BALANCE_TOO_LOW = 4;
    /** Operation was forbidden. */
    public static final int ERROR_FORBIDDEN = 5;
    /** Permission for the operation was denied. */
    public static final int ERROR_PERMISSION_DENIED = 6;

    private BankingApplication mAppState;
    private boolean mHttpsMode;
    private HostnameVerifier mHostnameVerifier;

    /**
     * Creates the RestClient and connects it to the state of the application. This allows for it to
     * modify the state based on what happens during requests.
     * @param appState The BankingApplication containing the app-wide state variables.
     * @param enableHttps Whether to use HTTPS.
     * @throws NoSuchAlgorithmException if the SSL encryption algorithm chosen is not available.
     * @throws KeyManagementException if lax SSL initialisation fails.
     */
    public RestClient(BankingApplication appState, boolean enableHttps)
            throws NoSuchAlgorithmException, KeyManagementException {
        this.mAppState = appState;
        mHttpsMode = enableHttps;
        setLaxSSL();
    }

    /**
     * Performs a simple HTTP GET and returns the result.
     * @param urlName API Service endpoint.
     * @return HttpContent from the url.
     * @throws IOException if the network connection failed
     */
    public String getHttpContent(String urlName) throws IOException {
        String line;
        String result;
        StringBuilder httpContent = new StringBuilder();

        URL url = new URL(urlName);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                httpContent.append(line);
            }

            httpConnection.disconnect();
        }
        result = httpContent.toString();
        return result;
    }

    /**
     * Performs an HTTP POST with the given data.
     * @param urlString The URL to POST to.
     * @param variables key/value pairs for all parameters to be POSTed.
     * @return The data passed back from the server, as a String.
     * @throws IOException if the network connection failed.
     * @throws HttpException if the HTTP/S request failed
     */
    public String postHttpContent(String urlString, Map<String, String> variables)
            throws IOException, HttpException {
        String response = "";
        URL url = new URL(urlString);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setDoInput(true);
        httpConnection.setDoOutput(true);
        httpConnection.setUseCaches(false);
        httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Assemble a String out of the parameters we're posting
        String postData = "";
        for (String key : variables.keySet()) {
            postData += "&" + key + "=" + variables.get(key);
        }
        postData = postData.substring(1);

        // Send the POST data
        DataOutputStream postOut = new DataOutputStream(httpConnection.getOutputStream());
        postOut.writeBytes(postData);
        postOut.flush();
        postOut.close();

        // Now get the response the server gives us
        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection
                    .getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        } else {
            response = "";
            Log.e(TAG, "HTTP request failed on: " + urlString + " With error code: "
                            + responseCode);
            throw new HttpException(responseCode);
        }
        return response;
    }

    /**
     * Performs a simple HTTPS GET and returns the result.
     * @param urlName API Service endpoint.
     * @return HttpContent from the url.
     * @throws IOException if the network connection failed.
     */
    public String getHttpsContent(String urlName) throws IOException {
        String line;
        String result;
        StringBuilder httpsContent = new StringBuilder();

        URL url = new URL(urlName);
        HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
        if (mHostnameVerifier != null) {
            httpsConnection.setHostnameVerifier(mHostnameVerifier);
        }
        int responseCode = httpsConnection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            InputStream inputStream = httpsConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                httpsContent.append(line);
            }

            httpsConnection.disconnect();
        }
        result = httpsContent.toString();
        return result;
    }

    /**
     * Performs an HTTPS POST with the given data.
     * @param urlString The URL to POST to.
     * @param variables key/value pairs for all parameters to be POSTed.
     * @return The data passed back from the server, as a String.
     * @throws IOException if the network connection failed. 
     * @throws HttpException if the HTTP transaction failed
     */
    public String postHttpsContent(String urlString, Map<String, String> variables)
            throws IOException, HttpException {
        String response = "";
        URL url = new URL(urlString);
        HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
        if (mHostnameVerifier != null) {
            httpsConnection.setHostnameVerifier(mHostnameVerifier);
        }
        httpsConnection.setDoInput(true);
        httpsConnection.setDoOutput(true);
        httpsConnection.setUseCaches(false);
        httpsConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Assemble a String out of the parameters we're posting
        String postData = "";
        for (String key : variables.keySet()) {
            postData += "&" + key + "=" + variables.get(key);
        }
        postData = postData.substring(1);

        // Send the POST data
        DataOutputStream postOut = new DataOutputStream(httpsConnection.getOutputStream());
        postOut.writeBytes(postData);
        postOut.flush();
        postOut.close();

        // Now get the response the server gives us
        int responseCode = httpsConnection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(httpsConnection
                    .getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        } else {
            response = "";
            Log.e(TAG, "HTTPs request failed on: " + urlString + " With error code: "
                    + responseCode);
            throw new HttpException(responseCode);
        }
        return response;
    }

    /**
     * Logs into the REST service, generating a new session key.
     * @param server The address of the server to log into.
     * @param port The port number to use.
     * @param username Username to log in with.
     * @param password Password to log in with.
     * @return Whether the login was successful.
     * @throws JSONException if the server returned invalid JSON.
     * @throws IOException if the network connection failed.
     * @throws HttpException if the HTTP transaction failed.
     */
    public int performLogin(String server, String port, String username, String password)
            throws JSONException, IOException, HttpException {
        // First perform the RESTful operation
        String protocol = mHttpsMode ? "https://" : "http://";
        String url = protocol + server + ":" + port + "/login";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", username);
        parameters.put("password", password);
        String JsonResponse;
        if (mHttpsMode) {
            JsonResponse = postHttpsContent(url, parameters);
        } else {
            JsonResponse = postHttpContent(url, parameters);
        }

        // Now parse out the JSON response and act accordingly
        int errorCode = parseError(JsonResponse);
        if (errorCode == NULL_ERROR) {
            JSONObject jsonObject = new JSONObject(JsonResponse);
            String key = jsonObject.getString("key");
            String created = jsonObject.getString("created");
            mAppState.setSession(key, created);
            return errorCode;
        }
        return errorCode;
    }

    /**
     * Queries the server for a list of accounts, and returns a list of them.
     * @param server The address of the server to query.
     * @param port The port we will make our query on.
     * @return A list of all accounts the server told us about, and their details.
     * @throws JSONException if the server returned invalid JSON.
     * @throws IOException if the network connection failed.
     * @throws AuthenticatorException if the server rejected the proferred session key. 
     */
    public List<Account> getAccounts(String server, String port) throws JSONException, IOException,
            AuthenticatorException {
        List<Account> accounts = new ArrayList<Account>();
        String protocol = mHttpsMode ? "https://" : "http://";
        String url = protocol + server + ":" + port + "/accounts" + "?session_key="
                + URLEncoder.encode(mAppState.getSessionKey());
        String result;
        if (mHttpsMode) {
            result = getHttpsContent(url);
        } else {
            result = getHttpContent(url);
        }
        int errorCode = parseError(result);

        if (errorCode == NULL_ERROR) {

            JSONArray resultArray = new JSONArray(result);
            for (int count = 0; count < resultArray.length(); count++) {
                JSONObject accountJson = resultArray.getJSONObject(count);
                double balance = accountJson.getDouble("balance");
                String accountType = accountJson.getString("type");
                int accountNumber = accountJson.getInt("account_number");
                accounts.add(new Account(accountNumber, accountType, balance));
            }

        } else if (errorCode == ERROR_SESSION_KEY) {
            throw new AuthenticatorException("Session key invalid");
        }

        return accounts;
    }

    /**
     * Queries the server for a list of accounts, and returns a list of them.
     * @param server The address of the server to query.
     * @param port The port we will make our query on.
     * @return A list of all accounts the server told us about, and their details.
     * @throws IOException if the network connection failed.
     * @throws AuthenticatorException if the server rejected the proferred session key. 
     */
    public String getStatement(String server, String port) throws IOException,
            AuthenticatorException {
        String protocol = mHttpsMode ? "https://" : "http://";
        String url = (protocol + server + ":" + port + "/statement" + "?session_key=" + URLEncoder
                .encode(mAppState.getSessionKey()));
        String result;
        if (mHttpsMode) {
            result = getHttpsContent(url);
        } else {
            result = getHttpContent(url);
        }

        int errorCode = parseError(result);

        if (errorCode == NULL_ERROR) {
            // No need to do anything to the data
        } else if (errorCode == ERROR_SESSION_KEY) {
            throw new AuthenticatorException("Session key invalid");
        }

        return result;
    }

    /**
     * Transfers funds between the given accounts.
     * @param server The server to use.
     * @param port The port to use.
     * @param fromAccount The number of the account we're transferring from.
     * @param toAccount The number of the account we're transferring to.
     * @param amount The amount to be transferred
     * @param sessionKey The session key to use
     * @return A code indicating if the transaction succeeded, or why it failed.
     * @throws IOException if the network connection failed.
     * @throws HttpException if the HTTP transaction failed.
     */
    public int transfer(String server, String port, int fromAccount, int toAccount, double amount,
            String sessionKey) throws IOException, HttpException {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("from_account", Integer.toString(fromAccount));
        variables.put("to_account", Integer.toString(toAccount));
        variables.put("amount", Double.toString(amount));
        String response;
        if (mHttpsMode) {
            response = postHttpsContent("https://" + server + ":" + port + "/transfer"
                    + "?session_key=" + URLEncoder.encode(sessionKey), variables);
        } else {
            response = postHttpContent("http://" + server + ":" + port + "/transfer"
                    + "?session_key=" + URLEncoder.encode(sessionKey), variables);
        }
        int statusCode = parseError(response);
        return statusCode;
    }

    /**
     * Takes a JSON string and returns an int representing the error code in it.
     * @param json The string to check for JSON encapsulated error codes.
     * @return The error code the string represented, or a code for no error.
     */
    public int parseError(String json) {
        int errorCode;
        String errorString;
        if (json == null || json.equals("")) {
            return -1;
        }

        // First see if this string is a valid error message
        try {
            JSONObject jsonObject = new JSONObject(json);
            errorString = jsonObject.getString("error");
        } catch (JSONException e) {
            errorCode = NULL_ERROR;
            return errorCode;
        }

        // Since it is, encode it as an int and return it
        // String is of format "E[0-9]+", e.g. "E3"
        return Integer.parseInt(errorString.trim().substring(1));
    }

    /**
     * Sets the application to accept all SSL certificates.
     * @throws NoSuchAlgorithmException if the SSL encryption algorithm wasn't available.
     * @throws KeyManagementException if initialising ther SSLContext fails. 
     */
    private void setLaxSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {

            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {

            }
        } };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        mHostnameVerifier = new AllowAllHostnameVerifier();

    }

}
