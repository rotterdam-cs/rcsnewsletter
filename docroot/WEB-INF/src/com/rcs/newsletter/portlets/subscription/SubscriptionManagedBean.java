package com.rcs.newsletter.portlets.subscription;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.Validator;
import static com.rcs.newsletter.NewsletterConstants.*;
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
import com.rcs.newsletter.core.service.util.EmailFormat;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import com.rcs.newsletter.portlets.admin.UserUiStateManagedBean;
import com.rcs.newsletter.util.FacesUtil;
import com.rcs.newsletter.core.service.util.SubscriptionUtil;
import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
/*import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;*/
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
/*@Named
@Scope("request")*/
public class SubscriptionManagedBean implements Serializable {

    private static Log log = LogFactoryUtil.getLog(SubscriptionManagedBean.class);
    private static final long serialVersionUID = 1L;
    private String name;
    private String lastName;
    private String email;
    private String requestedActivationKey = null;
    private String requestedsubscriptionId = null;
    private String requestedDeactivationKey = null;
    private String requestedUnsubscriptionId = null;
    
    private RegistrationConfig currentConfig;
    /*@Inject
    private NewsletterPortletSettingsService settingsService;
    @Inject
    private NewsletterCategoryService categoryService;
    @Inject
    private NewsletterSubscriptorService subscriptorService;
    @Inject
    private NewsletterSubscriptionService subscriptionService;
    @Inject
    private UserUiStateManagedBean uiStateManagedBean;
    @Inject
    private UserUiStateManagedBean uiState;*/

    /*@PostConstruct
    public void init() {
        currentConfig = new RegistrationConfig();        
        currentConfig.setGroupid(uiState.getGroupid());
        currentConfig.setCompanyid(uiState.getCompanyid());
        
        RegistrationConfig registrationConfig = settingsService.findConfig(uiState.getThemeDisplay(), FacesUtil.getPortletUniqueId());
        if (registrationConfig != null) {
            currentConfig.setListId(registrationConfig.getListId());
            currentConfig.setDisableName(registrationConfig.isDisableName());
        }
        clearData();
    }*/

    /*public void doSaveSettings() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        ServiceActionResult result = settingsService.updateConfig(uiState.getThemeDisplay(), FacesUtil.getPortletUniqueId(), currentConfig);
        if (result.isSuccess()) {
            String infoMessage = serverMessageBundle.getString("newsletter.registration.settings.save.successfully");
            FacesUtil.infoMessage(infoMessage);
            log.info("Newsletter Settings updated successfully");
        } else {
            String errorMessage = serverMessageBundle.getString("newsletter.registration.settings.save.error");
            FacesUtil.errorMessage(errorMessage);            
            log.info("Newsletter Settings could not be updated");
        }
    }

    public String doRegister() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle newsletterBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());

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
                        subscriptorResult = subscriptorService.findByEmail(uiState.getThemeDisplay(), email);
                        //If the subscriptor doesnt exists we should create it
                        if (!subscriptorResult.isSuccess()) {
                            subscriptor = new NewsletterSubscriptor();                            
                            subscriptor.setGroupid(uiState.getGroupid());
                            subscriptor.setCompanyid(uiState.getCompanyid());
                            
                            subscriptor.setEmail(email);
                            subscriptor.setFirstName(name);
                            subscriptor.setLastName(lastName);

                            subscriptorResult = subscriptorService.save(subscriptor);

                            //If the subscriptor does not exists
                            //and we could not create it, we abort the process
                            if (!subscriptorResult.isSuccess()) {
                                FacesUtil.errorMessage(subscriptorResult.getValidationKeys());
                                return null;
                            }
                        } else {
                            subscriptor = subscriptorResult.getPayload();
                        }

                        NewsletterSubscription subscription = subscriptionService.findBySubscriptorAndCategory(subscriptor, newsletterCategory);
                        //If the subscription for this subscriptor and category 
                        //does not exists we should create it
                        if (subscription == null) {
                            subscription = new NewsletterSubscription();
                            subscription.setDeactivationKey(SubscriptionUtil.getUniqueKey());
                            subscription.setGroupid(uiState.getGroupid());
                            subscription.setCompanyid(uiState.getCompanyid());
                            
                            
                            subscription.setSubscriptor(subscriptor);
                            subscription.setCategory(newsletterCategory);
                            subscription.setStatus(SubscriptionStatus.INACTIVE);
                            subscription.setActivationKey(SubscriptionUtil.getUniqueKey());
                            

                            subscription = subscriptionService.save(subscription).getPayload();
                        }

                        //Depending on the actual status of the subscription we send or not the registration email
                        boolean sendEmail = true;
                        if (subscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                            String errorMessage = serverMessageBundle.getString("newsletter.registration.alreadysubscribed");
                            FacesUtil.errorMessage(errorMessage);
                            sendEmail = false;
                        }

                        if (sendEmail) {
                            String content = subscriptionEmail;
                            String subject = newsletterBundle.getString("newsletter.subscription.mail.subject");
                            
                            content = EmailFormat.replaceUserInfo(content, subscription, uiStateManagedBean.getThemeDisplay());
                            
                            content = EmailFormat.fixImagesPath(content, uiStateManagedBean.getThemeDisplay());                            
                            InternetAddress fromIA = new InternetAddress(newsletterCategory.getFromEmail());
                            InternetAddress toIA = new InternetAddress(email);
                            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, subject, content);
                            LiferayMailingUtil.sendEmail(message);
                            //LiferayMailingUtil.sendEmail(newsletterCategory.getFromEmail(), email, subject, content);

                            String infoMessage = serverMessageBundle.getString("newsletter.registration.success.msg");
                            FacesUtil.infoMessage(infoMessage);
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
        return null;
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
                    subscriptorResult = subscriptorService.findByEmail(uiState.getThemeDisplay(), email);

                    if (!subscriptorResult.isSuccess()) {
                        String errorMessage = serverMessageBundle.getString("newsletter.unregistration.generalerror");
                        FacesUtil.errorMessage(errorMessage);
                    } else {
                        NewsletterSubscriptor subscriptor = subscriptorResult.getPayload();
                        NewsletterSubscription subscription =
                                subscriptionService.findBySubscriptorAndCategory(subscriptor, newsletterCategory);

                        if (subscription != null) {
                            
                            subscription.setDeactivationKey(SubscriptionUtil.getUniqueKey());
                            subscriptionService.update(subscription);
                            
                            String content = unsubscriptionEmail;
                            String subject = newsletterBundle.getString("newsletter.unsubscription.mail.subject");                            

                            content = content.replace(CONFIRMATION_LINK_TOKEN, CONFIRMATION_UNREGISTER_LINK_TOKEN);                             
                            content = EmailFormat.replaceUserInfo(content, subscription, uiStateManagedBean.getThemeDisplay());
                            
                            content = EmailFormat.fixImagesPath(content, uiStateManagedBean.getThemeDisplay());                            
                            InternetAddress fromIA = new InternetAddress(newsletterCategory.getFromEmail());
                            InternetAddress toIA = new InternetAddress(email);
                            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, subject, content);
                            LiferayMailingUtil.sendEmail(message);
                            //LiferayMailingUtil.sendEmail(newsletterCategory.getFromEmail(), email, subject, content);

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

    public String doConfirmRegistration() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        ResourceBundle newsletterBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        String infoMesage = "";
        try {            
            long subscriptionId = Long.parseLong(getRequestedsubscriptionId());
            ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);
            if (subscriptionResult.isSuccess()) {
                NewsletterSubscription subscription = subscriptionResult.getPayload();
                
                if (subscription.getActivationKey().equals(getRequestedActivationKey())) {

                    subscription.setStatus(SubscriptionStatus.ACTIVE);

                    subscriptionResult = subscriptionService.update(subscription);

                    //Send the greeting mail
                    if (subscriptionResult.isSuccess() && subscription.getCategory().getGreetingEmail() != null) {
                        NewsletterCategory category = subscription.getCategory();
                        String greetingEmail = category.getGreetingEmail();
                        String content = greetingEmail;
                        String subject = newsletterBundle.getString("newsletter.greetings.mail.subject");
                                                
                        content = EmailFormat.replaceUserInfo(content, subscription, uiStateManagedBean.getThemeDisplay());                        
                        
                        content = EmailFormat.fixImagesPath(content, uiStateManagedBean.getThemeDisplay());                            
                        InternetAddress fromIA = new InternetAddress(category.getFromEmail());
                        InternetAddress toIA = new InternetAddress(subscription.getSubscriptor().getEmail());
                        MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, subject, content);
                        LiferayMailingUtil.sendEmail(message);                        
                        
                        //Send email to list manager
                        if (subscription.getCategory().getAdminEmail() != null && !subscription.getCategory().getAdminEmail().isEmpty()) {
                            log.info("Sendig Subscription email to admin");
                            String adminSubject = newsletterBundle.getString("newsletter.admin.list.manager.subscription.info.subject");
                            adminSubject = adminSubject.replace("{0}", subscription.getCategory().getName());
                            String adminContent = newsletterBundle.getString("newsletter.admin.list.manager.subscription.info");
                            adminContent = adminContent.replace("{0}", subscription.getSubscriptor().getEmail());
                            adminContent = adminContent.replace("{1}", subscription.getCategory().getName());
                            InternetAddress toIAAdmin = new InternetAddress(subscription.getCategory().getAdminEmail());
                            MailMessage messageAdmin = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIAAdmin, adminSubject, adminContent);
                            LiferayMailingUtil.sendEmail(messageAdmin);
                        }
                        
                        infoMesage = serverMessageBundle.getString("newsletter.registration.confirmed.msg");
                        FacesUtil.infoMessage(infoMesage);
                    } else {
                        log.error("Could not retrieve the subscription.");
                        infoMesage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
                        FacesUtil.errorMessage(infoMesage);
                    }
                } else {
                    log.error("Invalid Key " + subscription.getActivationKey() + "!=" + getRequestedActivationKey());
                    infoMesage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
                    FacesUtil.errorMessage(infoMesage);
                }
            } else {
                log.error("Could not retrieve the subscription or the greeting mail is null");
                infoMesage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
                FacesUtil.errorMessage(infoMesage);
            }
            
        } catch (Exception ex) {
            log.error("Error", ex);
            String errorMessage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
            FacesUtil.errorMessage(errorMessage);
        }
        return infoMesage;
    }

    public String doConfirmUnregistration() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        ResourceBundle newsletterBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        String infoMesage = "";
        try {
            long subscriptionId = Long.parseLong(getRequestedUnsubscriptionId());
            ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);
            if (subscriptionResult.isSuccess()) {
                NewsletterSubscription subscription = subscriptionResult.getPayload();


                if (subscription.getDeactivationKey().equals(getRequestedDeactivationKey())) {               
                    
                    //Send email to list manager
                    if (subscription.getCategory().getAdminEmail() != null && !subscription.getCategory().getAdminEmail().isEmpty()) {
                        log.info("Sendig UnSubscription email to admin");
                        String adminSubject = newsletterBundle.getString("newsletter.admin.list.manager.unsubscription.info.subject");
                        adminSubject = adminSubject.replace("{0}", subscription.getCategory().getName());
                        String adminContent = newsletterBundle.getString("newsletter.admin.list.manager.unsubscription.info");
                        adminContent = adminContent.replace("{0}", subscription.getSubscriptor().getEmail());
                        adminContent = adminContent.replace("{1}", subscription.getCategory().getName());
                        InternetAddress toIAAdmin = new InternetAddress(subscription.getCategory().getAdminEmail());
                        InternetAddress fromIAAdmin = new InternetAddress(subscription.getCategory().getFromEmail());
                        MailMessage messageAdmin = EmailFormat.getMailMessageWithAttachedImages(fromIAAdmin, toIAAdmin, adminSubject, adminContent);
                        LiferayMailingUtil.sendEmail(messageAdmin);
                    }
                    
                    subscriptionResult = subscriptionService.delete(subscription);                    

                    if (subscriptionResult.isSuccess()) {
                        infoMesage = serverMessageBundle.getString("newsletter.unregistration.confirmed.msg");
                        infoMesage = infoMesage.replace("{0}", subscription.getCategory().getName());
                        FacesUtil.infoMessage(infoMesage);
                    } else {
                        infoMesage = serverMessageBundle.getString("newsletter.unregistration.unconfirmed.msg");
                        infoMesage = infoMesage.replace("{0}", subscription.getCategory().getName());
                        FacesUtil.errorMessage(infoMesage);
                    }                
                } else {
                    log.error("Invalid Key " + subscription.getDeactivationKey() + "!=" + getRequestedDeactivationKey());
                    infoMesage = serverMessageBundle.getString("newsletter.registration.unconfirmed.msg");
                    FacesUtil.errorMessage(infoMesage);
                }
                
            } else {
                infoMesage = serverMessageBundle.getString("newsletter.unregistration.unconfirmed.msg");
                FacesUtil.errorMessage(infoMesage);
            }
        } catch (Exception ex) {
            infoMesage = serverMessageBundle.getString("newsletter.unregistration.unconfirmed.msg");
            FacesUtil.errorMessage(infoMesage);
        }
        return infoMesage;
    }

    private void clearData() {
        this.name = "";
        this.lastName = "";
        this.email = "";
    }*/
    
    /**
     * Method that is launched when the registration-confirm portlet receive
     * the params from the URL
     * @return 
     */
    /*public String getActivationkey() {        
        String result = "";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, Object> map = facesContext.getExternalContext().getRequestMap();
        if (map != null) {
            for (String key : map.keySet()) {
                if (map.get(key) instanceof HttpServletRequestWrapper) {
                    HttpServletRequest request = (HttpServletRequest) ((HttpServletRequestWrapper) map.get(key)).getRequest();
                    setRequestedActivationKey((String) request.getParameter("activationkey"));
                    setRequestedsubscriptionId((String) request.getParameter("subscriptionId"));
                    setRequestedDeactivationKey((String) request.getParameter("deactivationkey"));
                    setRequestedUnsubscriptionId((String) request.getParameter("unsubscriptionId"));
                    break;
                }
            }
        }
        //To activate the subscription 
        if(getRequestedActivationKey() != null && getRequestedsubscriptionId() != null) {
            result = doConfirmRegistration();
        //to deactivate the subscription
        } else if(getRequestedDeactivationKey() != null && getRequestedUnsubscriptionId() != null) {
            result = doConfirmUnregistration();
        }
        
        return result;
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

    public String getRequestedActivationKey() {
        return requestedActivationKey;
    }

    public void setRequestedActivationKey(String requestedActivationKey) {
        this.requestedActivationKey = requestedActivationKey;
    }

    public String getRequestedsubscriptionId() {
        return requestedsubscriptionId;
    }

    public void setRequestedsubscriptionId(String requestedsubscriptionId) {
        this.requestedsubscriptionId = requestedsubscriptionId;
    }

    public String getRequestedDeactivationKey() {
        return requestedDeactivationKey;
    }

    public void setRequestedDeactivationKey(String requestedDeactivationKey) {
        this.requestedDeactivationKey = requestedDeactivationKey;
    }

    public String getRequestedUnsubscriptionId() {
        return requestedUnsubscriptionId;
    }

    public void setRequestedUnsubscriptionId(String requestedUnsubscriptionId) {
        this.requestedUnsubscriptionId = requestedUnsubscriptionId;
    }*/
    
    
}
