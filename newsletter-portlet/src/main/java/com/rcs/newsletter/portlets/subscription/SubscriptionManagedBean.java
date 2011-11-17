
package com.rcs.newsletter.portlets.subscription;

import com.rcs.newsletter.core.service.NewsletterPortletSettingsService;
import java.io.Serializable;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriptionManagedBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String lastName;
    private String email;
    
    @Inject
    private NewsletterPortletSettingsService settingsService;
    
    
    public void doRegister() {
    }
    
    ////////////////// GETTERS AND SETTERS /////////////////////
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
