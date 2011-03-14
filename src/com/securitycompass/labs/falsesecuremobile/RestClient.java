package com.securitycompass.labs.falsesecuremobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RestClient {

    private static final String TAG = "RestClient";

    private ApplicationState appState;
    
    //TODO: Store this elsewhere, more sensibly
    private int mHttpPort=8080;

    /**
     * Creates the RestClient and connects it to the state of the application. This allows for it to
     * modify the state based on what happens during requests.
     */
    public RestClient(ApplicationState appState) {
        this.appState = appState;
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
 /*   public HttpResponse doPost(String url, HashMap<String, String> hm, String username,
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
    }*/

    /**
     * Performs an HTTP POST with the given data
     * 
     * @param urlString
     *            The URL to POST to
     * @param postData
     *            the data to POST
     * @return The data passed back from the server, as a String
     */
    public String postHttpContent(String urlString, Map<String,String> variables) {
        String response="";
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            //Assemble a String out of the parameters we're posting
            String postData="";
            for(String key : variables.keySet()){
                postData+="&" + key + "=" + variables.get(key);
            }
            postData=postData.substring(1);
            
            //Send the POST data
            DataOutputStream postOut=new DataOutputStream(httpConnection.getOutputStream());
            postOut.writeBytes(postData);
            postOut.flush();
            postOut.close();
            
            //Now get the response the server gives us
            int responseCode=httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                while((line = br.readLine()) != null){
                    response+=line;
                }
            } else{
                response=null;
                System.err.println("HTTP request failed on: " + urlString + " With error code: " + responseCode);
            }
            
        } catch (Exception e) {
            System.err.println("Exception while making HttpConnection to  URL: " + urlString
                    + "\n\tException: " + e);
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
        String url = "http://" + server + ":8080/login";
        Map<String, String> parameters=new HashMap();
        parameters.put("username", username);
        parameters.put("password", password);
        String response=postHttpContent(url, parameters);
        System.err.println("Login response: " + response);
        // POST to /login with parameters "username" and "password"
        // JSON { "error" : "E1"} indiciates failure
        return null;
    }

}
