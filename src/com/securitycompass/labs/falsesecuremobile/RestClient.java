/** Copyright 2011 Security Compass */

package com.securitycompass.labs.falsesecuremobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class RestClient {

    private static final String TAG = "RestClient";

    private BankingApplication appState;

    /**
     * Creates the RestClient and connects it to the state of the application. This allows for it to
     * modify the state based on what happens during requests.
     */
    public RestClient(BankingApplication appState) {
        this.appState = appState;
    }

    /**
     * Performs a simple HTTP GET and returns the result
     * @param urlName API Service endpoint.
     * @return HttpContent from the url.
     */
    private String getHttpContent(String urlName) {
        String line;
        String result;
        StringBuilder httpContent = new StringBuilder();
        try {
            URL url = new URL(urlName);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    httpContent.append(line);
                }
            } else {
                System.err.println("HTTP request failed on: " + urlName);
            }
            httpConnection.disconnect();
        } catch (Exception ex) {
            System.err.println("Exception while making HttpConnection to APIs URL. - \n" + urlName
                    + "\nException: " + ex);
        }
        result = httpContent.toString();
        return result;
    }

    /**
     * Performs an HTTP POST with the given data
     * @param urlString The URL to POST to
     * @param postData the data to POST
     * @return The data passed back from the server, as a String
     */
    public String postHttpContent(String urlString, Map<String, String> variables) {
        String response = "";
        try {
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
                response = null;
                System.err.println("HTTP request failed on: " + urlString + " With error code: "
                        + responseCode);
            }

        } catch (Exception e) {
            response = null;
            System.err.println("Exception while making HttpConnection to  URL: " + urlString
                    + "\n\tException: " + e);
        }
        return response;
    }

    /**
     * Logs into the REST service, generating a new session key
     * @param username Username to log in with
     * @param password Password to log in with
     * @return Whether the login was successful
     */
    public boolean performHTTPLogin(String server, String port, String username, String password) {
        // First perform the RESTful operation
        String url = "http://" + server + ":" + port + "/login";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", username);
        parameters.put("password", password);
        String JsonResponse = postHttpContent(url, parameters);
        System.err.println("Login response: " + JsonResponse);

        // Now parse out the JSON response and act accordingly
        //TODO: remove error checking to make code cleaner
        String errorCode = null, key = null, created = null;
        boolean error = false;
        try {
            JSONObject jsonObject = new JSONObject(JsonResponse);
            key = jsonObject.getString("key");
            created = jsonObject.getString("created");
        } catch (JSONException e) {
            System.err.println("Error parsing JSON from login:" + e);
        }
        if (error) {
            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                errorCode = jsonObject.getString("error").trim();
            } catch (JSONException e) {
                System.err.println("Error parsing JSON error code from login:" + e);
            }
        }
        if (errorCode != null && errorCode.toLowerCase().matches("e[0-9]")) {
            System.err.println("Login error: " + errorCode);
            return false;
        } else if ((key != null && created != null)) {
            appState.setSession(key, created);
            return true;
        } else {
            System.err.println("Unknown HTTP Login error (No code returned)");
            return false;
        }
    }

}
