package com.rcs.newsletter.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

/**
 * Utility methods for handling jsf common operations.
 * @author juan
 */
public class FacesUtil {
    
    /**
     * Convenience method to add a global message for the info category
     * @param message 
     */
    public static void infoMessage(String message) {
        addMessage(message, FacesMessage.SEVERITY_INFO);
    }

    /**
     * Convenience method to add a global message for the error category
     * @param message 
     */
    public static void errorMessage(String message) {
        addMessage(message, FacesMessage.SEVERITY_ERROR);
    }
    
    
    
    /**
     * Add a faces message for a given severity
     * @param message
     * @param severity 
     */
    public static void addMessage(String message, Severity severity) {
        FacesMessage fm = new FacesMessage(severity, message, message);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }
    
}
