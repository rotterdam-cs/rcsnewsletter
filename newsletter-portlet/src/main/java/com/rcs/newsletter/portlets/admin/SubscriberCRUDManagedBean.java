package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 *
 * @author Prj.M@x
 */
@Named
@Scope("request")
public class SubscriberCRUDManagedBean extends NewsletterCRUDManagedBean {
    private static Log log = LogFactoryUtil.getLog(SubscriberCRUDManagedBean.class);
    
    private String firstName;
    private String middleName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static Log getLog() {
        return log;
    }

    public static void setLog(Log log) {
        SubscriberCRUDManagedBean.log = log;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    
    
    
    /*
     * Redirections
     */
    public String redirectCreateSubscriber() {
        this.setAction(CRUDActionEnum.CREATE);
        return "editSubscriber";
    }
    
    
}