package com.rcs.newsletter.util;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;

/**
 * Utility methods for handling jsf common operations.
 * @author juan
 */
public class FacesUtil {
    
    private static final String HTML_BR = "<br>";
    
    /**
     * Convenience method to add a global message for the info category
     * @param message 
     */
    public static void infoMessage(String message) {
        addMessage(message, FacesMessage.SEVERITY_INFO);
    }
    
    /**
     * Convenience method to add a global message for the error category
     * @param messages list of error strings 
     */
    public static void errorMessage(List<String> messages) {
        StringBuilder errorStringBuilder = new StringBuilder();
        for (String errorMessage : messages) {
            errorStringBuilder.append(errorMessage);
            errorStringBuilder.append(HTML_BR);
        }
                
        addMessage(errorStringBuilder.toString(), FacesMessage.SEVERITY_ERROR);
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
    
    
    public static String getPortletUniqueId() {
        PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getWindowID();
    }
    
    public static String getActionUrl() {
        PortletResponse response = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        
        if (!(response instanceof RenderResponse)) {
            return "null";
        }
        
        RenderResponse rresp = (RenderResponse) response;
        
        return rresp.createActionURL().toString();
    }
}
