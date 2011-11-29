package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.rcs.newsletter.util.FacesUtil;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class SubscriptionEmailManagedBean {

    private static Log log = LogFactoryUtil.getLog(SubscriptionEmailManagedBean.class);
    
    private static final String GREETING_MAIL_INFO = "newsletter.admin.category.greetingmail.info";
    private static final String SUBSCRIPTION_MAIL_INFO = "newsletter.admin.category.subscriptionmail.info";
    private static final String UNSUBSCRIPTION_MAIL_INFO = "newsletter.admin.category.unsubscriptionmail.info";
    
    @Inject
    NewsletterCategoryService categoryService;
    @Inject
    private UserUiStateManagedBean uiState;
    private int categoryId;
    private NewsletterCategory newsletterCategory;
    private SubscriptionTypeEnum subscriptionType;
    private String subscriptionEmailBody;
    private String helpPageText;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public NewsletterCategory getNewsletterCategory() {
        return newsletterCategory;
    }

    public void setNewsletterCategory(NewsletterCategory newsletterCategory) {
        this.newsletterCategory = newsletterCategory;
    }

    public String getSubscriptionEmailBody() {
        return subscriptionEmailBody;
    }

    public void setSubscriptionEmailBody(String subscriptionEmailBody) {
        this.subscriptionEmailBody = subscriptionEmailBody;
    }

    public SubscriptionTypeEnum getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionTypeEnum subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getHelpPageText() {
        return helpPageText;
    }

    public void setHelpPageText(String helpPageText) {
        this.helpPageText = helpPageText;
    }

    public String redirectEditSubscribeMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.setSubscriptionType(SubscriptionTypeEnum.SUBSCRIBE);
        fillData();

        return "editSubscriptionMail";
    }

    public String redirectEditUnsubscribeMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.setSubscriptionType(SubscriptionTypeEnum.UNSUBSCRIBE);
        fillData();

        return "editSubscriptionMail";
    }

    public String redirectEditGreetingMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.setSubscriptionType(SubscriptionTypeEnum.GREETING);
        fillData();

        return "editSubscriptionMail";
    }

    /**
     * Method that fill the data in the managed bean:
     * JournalArticle
     * JournalArticleId
     * Subscription email body (article content viewer)
     * In case that doesnt exists in the category, fills those 
     * fields with empty values
     */
    private void fillData() {
        uiState.refresh();
        ServiceActionResult serviceActionResult = categoryService.findById(categoryId);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle newsletterMessageBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        if (serviceActionResult.isSuccess()) {
            this.newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
            String emailContentBody = "";
            String helpText = "";
            switch (getSubscriptionType()) {
                case SUBSCRIBE:
                    emailContentBody = newsletterCategory.getSubscriptionEmail();
                    helpText = newsletterMessageBundle.getString(SUBSCRIPTION_MAIL_INFO);
                    break;
                case UNSUBSCRIBE:
                    emailContentBody = newsletterCategory.getUnsubscriptionEmail();
                    helpText = newsletterMessageBundle.getString(UNSUBSCRIPTION_MAIL_INFO);
                    break;
                case GREETING:
                    emailContentBody = newsletterCategory.getGreetingEmail();
                    helpText = newsletterMessageBundle.getString(GREETING_MAIL_INFO);
                    break;
            }
            this.setSubscriptionEmailBody(emailContentBody);
            this.setHelpPageText(helpText);
        }
    }

    public String save() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        
        ServiceActionResult<NewsletterCategory> serviceActionResult = categoryService.findById(categoryId);
        if (serviceActionResult.isSuccess()) {
            newsletterCategory = serviceActionResult.getPayload();
            switch (getSubscriptionType()) {
                case SUBSCRIBE:
                    newsletterCategory.setSubscriptionEmail(getSubscriptionEmailBody());
                    break;
                case UNSUBSCRIBE:
                    newsletterCategory.setUnsubscriptionEmail(getSubscriptionEmailBody());
                    break;
                case GREETING:
                    newsletterCategory.setGreetingEmail(getSubscriptionEmailBody());
                    break;
            }

            ServiceActionResult<NewsletterCategory> result = null;

            result = categoryService.update(newsletterCategory);

            if (result.isSuccess()) {
                String infoMsg = serverMessageBundle.getString("newsletter.admin.subscriptionmail.saved");
                FacesUtil.infoMessage(infoMsg);
            } else {
                String errorMsg = serverMessageBundle.getString("newsletter.admin.subscriptionmail.notsaved");
                FacesUtil.errorMessage(errorMsg);
            }
        } else {
            String errorMsg = serverMessageBundle.getString("newsletter.admin.subscriptionmail.notsaved");
            FacesUtil.errorMessage(errorMsg);
        }

        return uiState.redirectAdmin();
    }
}
