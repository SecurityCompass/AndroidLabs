package com.securitycompass.androidlabs.filepermissionssolution;


/**
 * Exception that contains an HTTP error code
 * @author Ewan Sinclair
 */
public class HttpException extends Exception{

    private int mStatusCode;
    
    /**
     * Creates a new instance with a given status code
     * @param statusCode the status code returned by the HTTP transaction
     */
    public HttpException(int statusCode){
        mStatusCode=statusCode;
    }
    
    /**
     * Returns the status code held in this Exception
     * @return the status code held in this Exception.
     */
    public int getStatusCode() {
        return mStatusCode;
    }
    
}
