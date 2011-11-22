
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

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriptionEmailManagedBean {
    
    private static Log log = LogFactoryUtil.getLog(SubscriptionEmailManagedBean.class);
    
    @Inject
    NewsletterCategoryService categoryService;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    private int categoryId;
    private NewsletterCategory newsletterCategory;
    private SubscriptionTypeEnum subscriptionType;
    private String subscriptionEmailBody;

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
        if (serviceActionResult.isSuccess()) {
            this.newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
            String emailContentBody = "";
            switch(getSubscriptionType()) {
                case SUBSCRIBE:
                    emailContentBody = newsletterCategory.getSubscriptionEmail();
                    break;
                case UNSUBSCRIBE:
                    emailContentBody = newsletterCategory.getSubscriptionEmail();
                    break;
                case GREETING:
                    emailContentBody = newsletterCategory.getGreetingEmail();
                    break;
            }
            this.setSubscriptionEmailBody(emailContentBody);            
        }
    }
    
    public String save() {
        switch(getSubscriptionType()) {
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
        
        if(result.isSuccess()) {
            FacesUtil.infoMessage("Saved succesfully");
        } else {
            FacesUtil.errorMessage(result.getValidationKeys());
        }
        
        return uiState.redirectAdmin();
    }
}
