package com.rcs.newsletter.core.model.dtos;

import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import java.io.Serializable;
import org.jdto.annotation.Source;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
public class NewsletterSubscriptionDTO implements Serializable{
    private long id;

    private SubscriptionStatus status;    

    @Source("subscriptor.firstName")
    private String subscriptorFirstName;
    
    @Source("subscriptor.lastName")
    private String subscriptorLastName;
    
    @Source("subscriptor.email")
    private String subscriptorEmail;
    
    private String activationKey;
    private String deactivationKey;

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getDeactivationKey() {
        return deactivationKey;
    }

    public void setDeactivationKey(String deactivationKey) {
        this.deactivationKey = deactivationKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getSubscriptorEmail() {
        return subscriptorEmail;
    }

    public void setSubscriptorEmail(String subscriptorEmail) {
        this.subscriptorEmail = subscriptorEmail;
    }

    public String getSubscriptorFirstName() {
        return subscriptorFirstName;
    }

    public void setSubscriptorFirstName(String subscriptorFirstName) {
        this.subscriptorFirstName = subscriptorFirstName;
    }

    public String getSubscriptorLastName() {
        return subscriptorLastName;
    }

    public void setSubscriptorLastName(String subscriptorLastName) {
        this.subscriptorLastName = subscriptorLastName;
    }
}
