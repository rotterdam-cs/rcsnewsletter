package com.rcs.newsletter.portlets.subscription;

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
import com.rcs.newsletter.util.FacesUtil;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriptionManagedBean implements Serializable {
    private static Log log = LogFactoryUtil.getLog(SubscriptionManagedBean.class); 
    private static final long serialVersionUID = 1L;
    private String name;
    private String lastName;
    private String email;
    private String portletUrl;
    
    private RegistrationConfig currentConfig;

    @PostConstruct
    public void init() {
        currentConfig = new RegistrationConfig();        
        RegistrationConfig conf = settingsService.findConfig(FacesUtil.getPortletUniqueId());
        //log.error("***********************" + conf.getListId());  TODO ARIEL
        currentConfig.setListId(conf.getListId());
        currentConfig.setDisableName(conf.isDisableName());
        portletUrl = FacesUtil.getActionUrl();
        clearData();
    }
    @Inject
    private NewsletterPortletSettingsService settingsService;
    @Inject
    private NewsletterCategoryService categoryService;
    @Inject
    private NewsletterSubscriptorService subscriptorService;
    @Inject
    private NewsletterSubscriptionService subscriptionService;

    public void doSaveSettings() {
        ServiceActionResult result = settingsService.updateConfig(FacesUtil.getPortletUniqueId(), currentConfig);
        if (result.isSuccess()) {
            log.error("Settings updated successfully");
            FacesUtil.infoMessage("Settings updated successfully");
        } else {
            log.error("Could not update settings");
            FacesUtil.errorMessage("Could not update settings");
        }
    }

    public String doRegister() {
        String result = null;
        
        if (null == currentConfig.getListId()) {            
            FacesUtil.errorMessage("The Newsletter is not configured yet");            
        } else {        
            
            ServiceActionResult<NewsletterCategory> categoryResult = categoryService.findById(currentConfig.getListId());

            if (categoryResult.isSuccess()) {
                NewsletterCategory newsletterCategory = categoryResult.getPayload();
                String subscriptionEmail = newsletterCategory.getSubscriptionEmail();

                if (subscriptionEmail != null) {
                    NewsletterSubscriptor subscriptor = subscriptorService.findByEmail(email);
                    //If the subscriptor doesnt exists we should create it
                    if (subscriptor == null) {
                        subscriptor = new NewsletterSubscriptor();
                        subscriptor.setEmail(email);
                        subscriptor.setFirstName(name);
                        subscriptor.setLastName(lastName);

                        subscriptorService.save(subscriptor);
                    }

                    NewsletterSubscription subscription = subscriptionService.findBySubscriptorAndCategory(subscriptor, newsletterCategory);
                    //If the subscription for this subscriptor and category 
                    //does not exists we should create it
                    if (subscription == null) {
                        subscription = new NewsletterSubscription();
                        subscription.setSubscriptor(subscriptor);
                        subscription.setCategory(newsletterCategory);
                        subscription.setStatus(SubscriptionStatus.INVITED);

                        subscription = subscriptionService.save(subscription).getPayload();
                    }

                    //Depending on the actual status of the subscription we send or not the registration email
                    boolean sendEmail = true;
                    if (subscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                        FacesUtil.errorMessage("You already belong to this list");
                        sendEmail = false;
                    } else if (subscription.getStatus().equals(SubscriptionStatus.INACTIVE)) {
                        subscription.setStatus(SubscriptionStatus.ACTIVE);
                        subscriptionService.update(subscription);
                        sendEmail = true;
                    } else if (subscription.getStatus().equals(SubscriptionStatus.INVITED)) {
                        sendEmail = true;
                    }

                    if (sendEmail) {
                        String content = subscriptionEmail;
                        //TODO Ariel
                        String subject = "subject";

                        StringBuilder stringBuilder = new StringBuilder(portletUrl);
                        stringBuilder.append("&subscriptionId=");
                        stringBuilder.append(subscription.getId());

                        content = content.replace(CONFIRMATION_LINK_TOKEN, stringBuilder.toString());
                        content = content.replace(LIST_NAME_TOKEN, newsletterCategory.getName());

                        LiferayMailingUtil.sendEmail(newsletterCategory.getFromEmail(), email, subject, content);

                        FacesUtil.infoMessage("A mail was send to your direction. Please check it to confirm the registration");

                        result = "registrationSucess";
                    }

                } else {
                    //could not retrieve the subscription email
                    FacesUtil.errorMessage("Could not register. Please contact the administrator");
                }
            } else {
                //could not retrieve the category
                FacesUtil.errorMessage("Could not register. Please contact the administrator");
            }

            clearData();
        }
        return result;
    }

    public String doUnregister() {
        String result = null;
        try {
            ServiceActionResult<NewsletterCategory> categoryResult = categoryService.findById(currentConfig.getListId());

            if (categoryResult.isSuccess()) {
                NewsletterCategory newsletterCategory = categoryResult.getPayload();
                String unsubscriptionEmail = newsletterCategory.getUnsubscriptionEmail();
                
                if (unsubscriptionEmail != null) {
                    NewsletterSubscriptor subscriptor = subscriptorService.findByEmail(email);                    

                    if (subscriptor == null) {
                        FacesUtil.errorMessage("Could not complet your unregistration. Please try again or contact the administrator");
                    } else {
                        NewsletterSubscription subscription =
                                subscriptionService.findBySubscriptorAndCategory(subscriptor, newsletterCategory);                        
                        if (subscription != null) {
                            String content = unsubscriptionEmail;
                            String subject = "subject";

                            StringBuilder stringBuilder = new StringBuilder(portletUrl);
                            stringBuilder.append("&unsubscriptionId=");
                            stringBuilder.append(subscription.getId());

                            content = content.replace(CONFIRMATION_LINK_TOKEN, stringBuilder.toString());
                            content = content.replace(LIST_NAME_TOKEN, newsletterCategory.getName());

                            LiferayMailingUtil.sendEmail(newsletterCategory.getFromEmail(), email, subject, content);

                            FacesUtil.infoMessage("A mail was send to your direction. Please check it to confirm the registration");

                            result = "unregistrationSucess";
                        } else {
                            FacesUtil.errorMessage("Could not complet your unregistration. Please try again or contact the administrator");
                        }
                    }
                }
            }
        } catch (Exception e) {
            FacesUtil.errorMessage("Could not complet your unregistration. Please try again or contact the administrator");
        }

        return result;
    }

    public void doConfirmRegistration(ValueChangeEvent event) {
        try {
            long subscriptionId = (Long.parseLong((String) event.getNewValue()));
            ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);

            if (subscriptionResult.isSuccess()) {
                NewsletterSubscription subscription = subscriptionResult.getPayload();

                subscription.setStatus(SubscriptionStatus.ACTIVE);

                subscriptionResult = subscriptionService.update(subscription);

                //Send the greeting mail
                if (subscriptionResult.isSuccess()) {
                    NewsletterCategory category = subscription.getCategory();
                    String greetingEmail = category.getGreetingEmail();                            
                    String content = greetingEmail;
                    String subject = "subject";

                    content = content.replace(LIST_NAME_TOKEN, category.getName());

                    LiferayMailingUtil.sendEmail(category.getFromEmail(), subscription.getSubscriptor().getEmail(), subject, content);

                    //resource bundle: newsletter.registration.confirmed.msg
                    FacesUtil.infoMessage("You are succesfully subscribed to the newsletter");
                } else {
                    //resource bundle: newsletter.registration.unconfirmed.msg
                    FacesUtil.errorMessage("Could not complet your registration. Please try again or contact the administrator");
                }
            } else {
                //resource bundle: newsletter.registration.unconfirmed.msg
                FacesUtil.errorMessage("Could not complet your registration. Please try again or contact the administrator");
            }
        } catch (Exception ex) {
            //resource bundle: newsletter.registration.unconfirmed.msg
            FacesUtil.errorMessage("Could not complet your registration. Please try again or contact the administrator");
        }
    }

    public void doConfirmUnregistration(ValueChangeEvent event) {
        try {
            long subscriptionId = (Long.parseLong((String) event.getNewValue()));
            ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);

            if (subscriptionResult.isSuccess()) {
                NewsletterSubscription subscription = subscriptionResult.getPayload();

                subscription.setStatus(SubscriptionStatus.INACTIVE);

                subscriptionResult = subscriptionService.update(subscription);

                //Send the greeting mail
                if (subscriptionResult.isSuccess()) {
                    FacesUtil.infoMessage("You are succesfully unsubscribed to the newsletter");
                } else {
                    //resource bundle: newsletter.registration.unconfirmed.msg
                    FacesUtil.errorMessage("Could not complet your unregistration. Please try again or contact the administrator");
                }
            } else {
                //resource bundle: newsletter.registration.unconfirmed.msg
                FacesUtil.errorMessage("Could not complet your unregistration. Please try again or contact the administrator");
            }
        } catch (Exception ex) {
            //resource bundle: newsletter.registration.unconfirmed.msg
            FacesUtil.errorMessage("Could not complet your unregistration. Please try again or contact the administrator");
        }
    }

    private void clearData() {
        this.name = "";
        this.lastName = "";
        this.email = "";
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

    public String getPortletUrl() {
        return portletUrl;
    }

    public void setPortletUrl(String portletUrl) {
        this.portletUrl = portletUrl;
    }
}
