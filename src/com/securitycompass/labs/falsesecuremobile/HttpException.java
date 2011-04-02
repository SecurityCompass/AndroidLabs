package com.securitycompass.labs.falsesecuremobile;

import java.io.IOException;

public class HttpException extends Exception{

    private int mStatusCode;
    
    public HttpException(int statusCode){
        mStatusCode=statusCode;
    }
    
    public int getStatusCode() {
        return mStatusCode;
    }
    
}
