
package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.persistence.JournalArticleUtil;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriptionEmailManagedBean {
    
    private static Log log = LogFactoryUtil.getLog(SubscriptionEmailManagedBean.class);
    
    @Inject
    NewsletterCategoryService categoryCRUDService;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    private int categoryId;
    
    private String subscriptionEmailType;
    private String subscriptionEmailBody;
    private JournalArticle subscriptionEmailArticle;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public JournalArticle getSubscriptionEmailArticle() {
        return subscriptionEmailArticle;
    }

    public void setSubscriptionEmailArticle(JournalArticle subscriptionEmailArticle) {
        this.subscriptionEmailArticle = subscriptionEmailArticle;
    }

    public String getSubscriptionEmailBody() {
        return subscriptionEmailBody;
    }

    public void setSubscriptionEmailBody(String subscriptionEmailBody) {
        this.subscriptionEmailBody = subscriptionEmailBody;
    }

    public String getSubscriptionEmailType() {
        return subscriptionEmailType;
    }

    public void setSubscriptionEmailType(String subscriptionEmailType) {
        this.subscriptionEmailType = subscriptionEmailType;
    }    
    
    public String redirectEditSubscribeMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        ServiceActionResult serviceActionResult = categoryCRUDService.findById(categoryId);
        if (serviceActionResult.isSuccess()) {
            NewsletterCategory newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
            long articleId = newsletterCategory.getSubscriptionArticleId();
            JournalArticle journalArticle = uiState.getJournalArticleByArticleId(String.valueOf(articleId));
            if(journalArticle != null) {
                this.setSubscriptionEmailArticle(journalArticle);
                this.setSubscriptionEmailBody(journalArticle.getContent());
            }
            this.subscriptionEmailType = "Subscription";
        }

        return "editSubscriptionMail";
    }

    public String redirectEditUnsubscribeMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        ServiceActionResult serviceActionResult = categoryCRUDService.findById(categoryId);
        if (serviceActionResult.isSuccess()) {
            NewsletterCategory newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
            long articleId = newsletterCategory.getUnsubscriptionArticleId();
            JournalArticle journalArticle = uiState.getJournalArticleByArticleId(String.valueOf(articleId));
            if(journalArticle != null) {
                this.setSubscriptionEmailArticle(journalArticle);
                this.setSubscriptionEmailBody(journalArticle.getContent());
            }
            this.subscriptionEmailType = "Unsubscription";
        }

        return "editSubscriptionMail";
    }
    
    public String editSubscriptionMail() {
        
        subscriptionEmailArticle.setContent(subscriptionEmailBody);
        try {
            JournalArticleUtil.update(subscriptionEmailArticle, true);
        } catch (SystemException ex) {
            log.warn("Could not update subscription article", ex);
        }
        
        return "admin?faces-redirect=true";
    }

    public void changeArticle(ValueChangeEvent event) {
        System.out.println("Change Event");
        this.subscriptionEmailArticle = (JournalArticle) event.getNewValue();
        
        this.subscriptionEmailBody = subscriptionEmailArticle.getContent();
        
        System.out.println("EmailBody " + subscriptionEmailBody);
    }
    
}
