package com.securitycompass.labs.falsesecuremobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class RestClient {

    private static final String TAG = "RestClient";
    
    private ApplicationState appState;

    /** Creates the RestClient and connects it to the state of the application.
     * This allows for it to modify the state based on what happens during requests. */
    public RestClient(ApplicationState appState){
        this.appState=appState;
    }
    
    /**
     * Performs a simple HTTP GET and returns the result
     * 
     * @param urlName
     *            API Service endpoint.
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

    /** Performs an HTTP POST */
    public HttpResponse doPost(String url, HashMap<String, String> hm, String username,
            String password, DefaultHttpClient httpClient) {

        HttpResponse response = null;
        if (username != null && password != null) {
            httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(username, password));
        }

        HttpPost postMethod = new HttpPost(url);
        if (hm == null)
            return null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            Iterator<String> it = hm.keySet().iterator();
            String k, v;
            while (it.hasNext()) {
                k = it.next();
                v = hm.get(k);
                nameValuePairs.add(new BasicNameValuePair(k, v));
            }
            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(postMethod);
            Log.i(TAG, "STATUS CODE: " + String.valueOf(response.getStatusLine().getStatusCode()));
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        } finally {
        }
        return response;
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
    public String performHTTPLogin(String server, String username, String password) {
        String url = "http://" + server + "/login";
        
        // POST to /login with parameters "username" and "password"
        // JSON { "error" : "E1"} indiciates failure
        return null;
    }

}
