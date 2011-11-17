
package com.rcs.newsletter.portlets.subscription;

import com.liferay.portlet.journal.model.JournalArticle;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.RegistrationConfig;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterPortletSettingsService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import com.rcs.newsletter.portlets.admin.UserUiStateManagedBean;
import com.rcs.newsletter.util.FacesUtil;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

import static com.rcs.newsletter.NewsletterConstants.*;

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
    
//    @Value("${newsletter.mail.from}")
//    private String fromEmailAddress;
    
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
    
    @Inject
    private NewsletterCategoryService categoryService;
    
    @Inject
    private NewsletterSubscriptorService subscriptorService;
    
    @Inject
    private NewsletterSubscriptionService subscriptionService;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    public void doSaveSettings() {
        ServiceActionResult result = settingsService.updateConfig(FacesUtil.getPortletUniqueId(), currentConfig);
        
        if (result.isSuccess()) {
            FacesUtil.infoMessage("Settings updated successfully");
        } else {
            FacesUtil.errorMessage("Could not update settings");
        }
    }
    
    public void doRegister() {
        ServiceActionResult<NewsletterCategory> result = categoryService.findById(currentConfig.getListId());
        
        if(result.isSuccess()) {            
            NewsletterCategory newsletterCategory = result.getPayload();
            JournalArticle subscriptionJournalArticle = uiState.getJournalArticleByArticleId(newsletterCategory.getSubscriptionArticleId());
            
            if(subscriptionJournalArticle != null) {
                NewsletterSubscriptor subscriptor = subscriptorService.findByEmail(email);
                
                if(subscriptor == null) {
                    subscriptor = new NewsletterSubscriptor();
                    subscriptor.setEmail(email);
                    subscriptor.setFirstName(name);
                    subscriptor.setLastName(lastName);
                    
                    subscriptorService.save(subscriptor);
                }
                
                NewsletterSubscription subscription = subscriptionService.findBySubscriptorAndCategory(subscriptor, newsletterCategory);
                
                if(subscription == null) {
                    subscription = new NewsletterSubscription();
                    subscription.setSubscriptor(subscriptor);
                    subscription.setCategory(newsletterCategory);
                    subscription.setStatus(SubscriptionStatus.INVITED);
                
                    subscription = subscriptionService.save(subscription).getPayload();
                }               
                
                //Depending on the actual status we send or not the registration email
                boolean sendEmail = true;
                if(subscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                    sendEmail = false;
                } else if(subscription.getStatus().equals(SubscriptionStatus.INACTIVE)) {
                    subscription.setStatus(SubscriptionStatus.ACTIVE);
                    sendEmail = true;
                } else if(subscription.getStatus().equals(SubscriptionStatus.INVITED)) {
                    sendEmail = true;
                }                
                
                if(sendEmail) {
                    String content = uiState.getContent(subscriptionJournalArticle);
                    String subject = subscriptionJournalArticle.getTitle();
                    
                    //TODO: confirmation link
                    String link = "confirmation link";
                    
                    content = content.replace(CONFIRMATION_LINK_TOKEN, link);

                    //TODO: email From obtain from properties
                    LiferayMailingUtil.sendEmail("admin@liferay.com", email, subject, content);
                    
                    FacesUtil.infoMessage("A mail was send to your direction. Please check it to confirm the registration");
                }
                
            } else {
                FacesUtil.errorMessage("Could not register. Please contact the administrator");
            }
        }
        
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
