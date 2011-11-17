
package com.rcs.newsletter.portlets.subscription;

import com.rcs.newsletter.core.model.RegistrationConfig;
import com.rcs.newsletter.core.service.NewsletterPortletSettingsService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.io.Serializable;
import javax.annotation.PostConstruct;
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
    
    private RegistrationConfig currentConfig;
    
    @PostConstruct
    public void init() {
        currentConfig = new RegistrationConfig();
        RegistrationConfig conf = settingsService.findConfig(FacesUtil.getPortletUniqueId());
        currentConfig.setConfirmationEmailArticleId(conf.getConfirmationEmailArticleId());
        currentConfig.setGreetingEmailArticleId(conf.getGreetingEmailArticleId());
        currentConfig.setListId(conf.getListId());
        currentConfig.setDisableName(conf.isDisableName());
    }
    
    @Inject
    private NewsletterPortletSettingsService settingsService;
    
    public void doSaveSettings() {
        ServiceActionResult result = settingsService.updateConfig(FacesUtil.getPortletUniqueId(), currentConfig);
        
        if (result.isSuccess()) {
            FacesUtil.infoMessage("Settings updated successfully");
        } else {
            FacesUtil.errorMessage("Could not update settings");
        }
    }
    
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

    public RegistrationConfig getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(RegistrationConfig currentConfig) {
        this.currentConfig = currentConfig;
    }
    
}
