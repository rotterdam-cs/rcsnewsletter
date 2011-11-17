
package com.rcs.newsletter.portlets.admin;

import com.liferay.portlet.journal.model.JournalArticle;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portlet.journal.service.JournalArticleLocalService;
import com.rcs.newsletter.util.FacesUtil;
import javax.faces.event.AjaxBehaviorEvent;

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
    
    @Inject
    JournalArticleLocalService journalArticleLocalService;    
    
    private int categoryId;
    private NewsletterCategory newsletterCategory;
    private JournalArticle subscriptionArticle;
    private SubscriptionTypeEnum subscriptionType;
    private String subscriptionEmailBody;
    private Long subscriptionEmailArticleId;

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

    public Long getSubscriptionEmailArticleId() {
        return subscriptionEmailArticleId;
    }

    public void setSubscriptionEmailArticleId(Long subscriptionEmailArticleId) {
        this.subscriptionEmailArticleId = subscriptionEmailArticleId;
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

    public JournalArticle getSubscriptionArticle() {
        return subscriptionArticle;
    }

    public void setSubscriptionArticle(JournalArticle subscriptionArticle) {
        this.subscriptionArticle = subscriptionArticle;
    }
    
    public String redirectEditSubscribeMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.subscriptionType = SubscriptionTypeEnum.SUBSCRIBE;
        fillData();

        return "editSubscriptionMail";
    }

    public String redirectEditUnsubscribeMail() {        
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.subscriptionType = SubscriptionTypeEnum.UNSUBSCRIBE;                
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
            long articleId = -1;
            switch(getSubscriptionType()) {
                case SUBSCRIBE:
                    articleId = newsletterCategory.getSubscriptionArticleId();
                    break;
                case UNSUBSCRIBE:
                    articleId = newsletterCategory.getUnsubscriptionArticleId();
                    break;
            }
            
            JournalArticle journalArticle = uiState.getJournalArticleByArticleId(articleId);
            if(journalArticle != null) {
                this.subscriptionArticle = journalArticle;
                this.setSubscriptionEmailArticleId(articleId);
                this.setSubscriptionEmailBody(uiState.getContent(journalArticle));
            } else {
                this.subscriptionArticle = null;
                this.setSubscriptionEmailArticleId(-1l);
                this.setSubscriptionEmailBody("");
            }
        }
    }
    
    public void changeArticle(AjaxBehaviorEvent event) {
        JournalArticle journalArticle = uiState.getJournalArticleByArticleId(getSubscriptionEmailArticleId());
        this.subscriptionEmailBody = uiState.getContent(journalArticle);
    }
    
    public String save() {
        if(subscriptionType.equals(SubscriptionTypeEnum.SUBSCRIBE)) {
            newsletterCategory.setSubscriptionArticleId(subscriptionEmailArticleId);
        } else {
            newsletterCategory.setUnsubscriptionArticleId(subscriptionEmailArticleId);
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
