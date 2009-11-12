package com.versionone.common.sdk;

public class DataLayerException extends Exception {

    private static final long serialVersionUID = -678836293883064493L;
    private String errorMessageToDisplay = null;

    public DataLayerException(String message) {
        super(message);
    }
    
    public DataLayerException(String message, Exception exception) {
        super(message, exception);
    }

    public DataLayerException(String string, String message) {
        this(string);
        errorMessageToDisplay = message;
    }
    
    public String getMessageToDisplay() {
        return errorMessageToDisplay;
    }
}
