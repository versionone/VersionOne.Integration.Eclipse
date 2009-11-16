package com.versionone.common.sdk;

public class ValidatorException extends DataLayerException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ValidatorException(String message) {
        super(message);
    }
    
    public ValidatorException(String message, Exception exception) {
        super(message, exception);
    }
}
