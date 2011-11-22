package com.rcs.newsletter.portlets.subscription;

import javax.faces.context.FacesContext;
import java.util.ResourceBundle;
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

import com.liferay.portal.kernel.util.Validator;
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
    @Inject
    private NewsletterPortletSettingsService settingsService;
    @Inject
    private NewsletterCategoryService categoryService;
    @Inject
    private NewsletterSubscriptorService subscriptorService;
    @Inject
    private NewsletterSubscriptionService subscriptionService;

    @PostConstruct
    public void init() {
        currentConfig = new RegistrationConfig();
        RegistrationConfig registrationConfig = settingsService.findConfig(FacesUtil.getPortletUniqueId());
        if (registrationConfig != null) {
            currentConfig.setListId(registrationConfig.getListId());
            currentConfig.setDisableName(registrationConfig.isDisableName());
        }
        portletUrl = FacesUtil.getActionUrl();
        clearData();
    }

    public void doSaveSettings() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        ServiceActionResult result = settingsService.updateConfig(FacesUtil.getPortletUniqueId(), currentConfig);
        if (result.isSuccess()) {
            String infoMessage = serverMessageBundle.getString("newsletter.registration.settings.save.successfully");
            FacesUtil.infoMessage(infoMessage);
            log.error("Settings updated successfully");
        } else {
            String errorMessage = serverMessageBundle.getString("newsletter.registration.settings.save.error");
            FacesUtil.errorMessage(errorMessage);
        }
    }

    public String doRegister() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle newsletterBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());

        String result = null;
        try {
            if (null == currentConfig.getListId()) {
                String errorMessage = serverMessageBundle.getString("newsletter.registration.notconfigured");
                FacesUtil.errorMessage(errorMessage);
            } else if (!Validator.isEmailAddress(email)) {
                String errorMessage = serverMessageBundle.getString("newsletter.registration.invalidemail");
                FacesUtil.errorMessage(errorMessage);
            } else {

                ServiceActionResult<NewsletterCategory> categoryResult = categoryService.findById(currentConfig.getListId());

                if (categoryResult.isSuccess()) {
                    NewsletterCategory newsletterCategory = categoryResult.getPayload();
                    String subscriptionEmail = newsletterCategory.getSubscriptionEmail();

                    if (subscriptionEmail != null) {
                        NewsletterSubscriptor subscriptor = null;
                        ServiceActionResult<NewsletterSubscriptor> subscriptorResult;
                        subscriptorResult = subscriptorService.findByEmail(email);
                        //If the subscriptor doesnt exists we should create it
                        if (!subscriptorResult.isSuccess()) {
                            subscriptor = new NewsletterSubscriptor();
                            subscriptor.setEmail(email);
                            subscriptor.setFirstName(name);
                            subscriptor.setLastName(lastName);

                            subscriptorResult = subscriptorService.save(subscriptor);

                            //If the subscriptor does not exists
                            //and we could not create it, we abort the process
                            if (!subscriptorResult.isSuccess()) {
                                FacesUtil.errorMessage(subscriptorResult.getValidationKeys());
                                return result;
                            }
                        } else {
                            subscriptor = subscriptorResult.getPayload();
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
                            String errorMessage = serverMessageBundle.getString("newsletter.registration.alreadysubscribed");
                            FacesUtil.errorMessage(errorMessage);
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
                            String subject = newsletterBundle.getString("newsletter.subscription.mail.subject");

                            StringBuilder stringBuilder = new StringBuilder(portletUrl);
                            stringBuilder.append("&subscriptionId=");
                            stringBuilder.append(subscription.getId());

                            content = content.replace(CONFIRMATION_LINK_TOKEN, stringBuilder.toString());
                            content = content.replace(LIST_NAME_TOKEN, newsletterCategory.getName());

                            LiferayMailingUtil.sendEmail(newsletterCategory.getFromEmail(), email, subject, content);

                            String infoMessage = serverMessageBundle.getString("newsletter.registration.success");
                            FacesUtil.infoMessage(infoMessage);

                            result = "registrationSucess";
                        }

                    } else {
                        log.error("could not retrieve the subscription email");
                        String errorMessage = serverMessageBundle.getString("newsletter.registration.generalerror");
                        FacesUtil.errorMessage(errorMessage);
                    }
                } else {
                    log.error("could not retrieve the category");
                    String errorMessage = serverMessageBundle.getString("newsletter.registration.generalerror");
                    FacesUtil.errorMessage(errorMessage);
                }
            }

        } catch (Exception e) {
            log.error("could not retrieve the category", e);
            String errorMessage = serverMessageBundle.getString("newsletter.unregistration.generalerror");
            FacesUtil.errorMessage(errorMessage);
        }

        clearData();
        return result;
    }

    public String doUnregister() {
        String result = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle newsletterBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());

        try {
            ServiceActionResult<NewsletterCategory> categoryResult = categoryService.findById(currentConfig.getListId());

            if (!categoryResult.isSuccess()) {
                String errorMessage = serverMessageBundle.getString("newsletter.registration.notconfigured");
                FacesUtil.errorMessage(errorMessage);
            } else if (!Validator.isEmailAddress(email)) {
                String errorMessage = serverMessageBundle.getString("newsletter.registration.invalidemail");
                FacesUtil.errorMessage(errorMessage);
            } else {
                NewsletterCategory newsletterCategory = categoryResult.getPayload();
                String unsubscriptionEmail = newsletterCategory.getUnsubscriptionEmail();

                if (unsubscriptionEmail != null) {
                    ServiceActionResult<NewsletterSubscriptor> subscriptorResult;
                    subscriptorResult = subscriptorService.findByEmail(email);

                    if (!subscriptorResult.isSuccess()) {
                        String errorMessage = serverMessageBundle.getString("newsletter.unregistration.generalerror");
                        FacesUtil.errorMessage(errorMessage);
                    } else {
                        NewsletterSubscriptor subscriptor = subscriptorResult.getPayload();
                        NewsletterSubscription subscription =
                                subscriptionService.findBySubscriptorAndCategory(subscriptor, newsletterCategory);

                        if (subscription != null) {

                            String content = unsubscriptionEmail;
                            String subject = newsletterBundle.getString("newsletter.unsubscription.mail.subject");

                            StringBuilder stringBuilder = new StringBuilder(portletUrl);
                            stringBuilder.append("&unsubscriptionId=");
                            stringBuilder.append(subscription.getId());

                            content = content.replace(CONFIRMATION_LINK_TOKEN, stringBuilder.toString());
                            content = content.replace(LIST_NAME_TOKEN, newsletterCategory.getName());

                            LiferayMailingUtil.sendEmail(newsletterCategory.getFromEmail(), email, subject, content);

                            String infoMessage = serverMessageBundle.getString("newsletter.registration.success.info");
                            FacesUtil.infoMessage(infoMessage);

                            result = "unregistrationSucess";
                        } else {
                            String errorMessage = serverMessageBundle.getString("newsletter.unregistration.generalerror");
                            FacesUtil.errorMessage(errorMessage);
                        }
                    }
                } else {
                    log.error("could not retrieve the unsubscription email");
                    String errorMessage = serverMessageBundle.getString("newsletter.registration.generalerror");
                    FacesUtil.errorMessage(errorMessage);
                }
            }
        } catch (Exception e) {
            String errorMessage = serverMessageBundle.getString("newsletter.unregistration.generalerror");
            FacesUtil.errorMessage(errorMessage);
        }

        clearData();
        return result;
    }

    public void doConfirmRegistration(ValueChangeEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        ResourceBundle newsletterBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        try {
            long subscriptionId = (Long.parseLong((String) event.getNewValue()));
            ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);

            if (subscriptionResult.isSuccess()) {
                NewsletterSubscription subscription = subscriptionResult.getPayload();

                subscription.setStatus(SubscriptionStatus.ACTIVE);

                subscriptionResult = subscriptionService.update(subscription);

                //Send the greeting mail
                if (subscriptionResult.isSuccess() && subscription.getCategory().getGreetingEmail() != null) {
                    NewsletterCategory category = subscription.getCategory();
                    String greetingEmail = category.getGreetingEmail();
                    String content = greetingEmail;
                    String subject = newsletterBundle.getString("newsletter.greetings.mail.subject");

                    content = content.replace(LIST_NAME_TOKEN, category.getName());

                    LiferayMailingUtil.sendEmail(category.getFromEmail(), subscription.getSubscriptor().getEmail(), subject, content);

                    String infoMesage = serverMessageBundle.getString("newsletter.registration.confirmed.msg");
                    FacesUtil.infoMessage(infoMesage);
                } else {
                    log.error("Could not retrieve the subscription.");
                    String errorMessage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
                    FacesUtil.errorMessage(errorMessage);
                }
            } else {
                log.error("Could not retrieve the subscription or the greeting mail is null");
                String errorMessage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
                FacesUtil.errorMessage(errorMessage);
            }
        } catch (Exception ex) {
            log.error("Error", ex);
            String errorMessage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
            FacesUtil.errorMessage(errorMessage);
        }
    }

    public void doConfirmUnregistration(ValueChangeEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        try {
            long subscriptionId = (Long.parseLong((String) event.getNewValue()));
            ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);

            if (subscriptionResult.isSuccess()) {
                NewsletterSubscription subscription = subscriptionResult.getPayload();

                subscription.setStatus(SubscriptionStatus.INACTIVE);

                subscriptionResult = subscriptionService.update(subscription);

                //Send the greeting mail
                if (subscriptionResult.isSuccess()) {
                    String infoMesage = serverMessageBundle.getString("newsletter.unregistration.confirmed.msg");
                    FacesUtil.infoMessage(infoMesage);
                } else {
                    String errorMessage = serverMessageBundle.getString("newsletter.unregistration.unconfirmed.msg");
                    FacesUtil.errorMessage(errorMessage);
                }
            } else {
                String errorMessage = serverMessageBundle.getString("newsletter.unregistration.unconfirmed.msg");
                FacesUtil.errorMessage(errorMessage);
            }
        } catch (Exception ex) {
            String errorMessage = serverMessageBundle.getString("newsletter.unregistration.unconfirmed.msg");
            FacesUtil.errorMessage(errorMessage);
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
}
