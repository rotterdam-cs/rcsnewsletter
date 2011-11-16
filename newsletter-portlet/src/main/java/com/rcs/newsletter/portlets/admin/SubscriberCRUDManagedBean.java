package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriberCRUDManagedBean {    
    private static Log log = LogFactoryUtil.getLog(CategoryCRUDManagedBean.class);
    
    private long id;
    private CRUDActionEnum action;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Long categoryId;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    @Inject
    NewsletterSubscriptorService subscriptorCRUDService;
    
    @Inject
    NewsletterCategoryService categoryService;
    
    @Inject
    NewsletterSubscriptorService subscriptorService;
    
    @Inject
    NewsletterSubscriptionService subscriptionService;
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CRUDActionEnum getAction() {
        return action;
    }

    public void setAction(CRUDActionEnum action) {
        this.action = action;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    
    public String redirectEditSubscriber() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        ServiceActionResult serviceActionResult = subscriptorCRUDService.findById(getId());
        
        if (serviceActionResult.isSuccess()) {
            NewsletterSubscriptor newsletterSubscriptor = (NewsletterSubscriptor) serviceActionResult.getPayload();
            
            this.email = newsletterSubscriptor.getEmail();
            this.firstName = newsletterSubscriptor.getFirstName();
            this.middleName = newsletterSubscriptor.getMiddleName();
            this.lastName = newsletterSubscriptor.getLastName();           
            
            this.setAction(CRUDActionEnum.UPDATE);
        }

        return "editSubscriber";
    }       
            
            
    public String redirectDeleteSubscriber() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        return "deleteSubscriber";
    }
    
    
    public String delete() {
        NewsletterSubscriptor subscriptor = subscriptorService.findById(getId()).getPayload();
        NewsletterCategory category = categoryService.findById(getCategoryId()).getPayload();
        String message = "";
        if (getCategoryId() == 0) {
            List<NewsletterSubscription> nls = subscriptionService.findBySubscriptor(subscriptor);
            for (NewsletterSubscription newsletterSubscription : nls) {
                log.error("Deleting Subscription :" + newsletterSubscription.getSubscriptor().getFirstName());
                subscriptionService.delete(newsletterSubscription);
            }            
            if (subscriptorService.delete(subscriptor).isSuccess()) {
                FacesUtil.infoMessage(message);
            } else {
                FacesUtil.errorMessage(message);
            }
        } else {
            NewsletterSubscription nls = subscriptionService.findBySubscriptorAndCategory(subscriptor, category);
            log.error("Deleting subscription for category " + category.getName());
            if ( subscriptionService.delete(nls).isSuccess()) {
                FacesUtil.infoMessage(message);
            } else {
                FacesUtil.errorMessage(message);
            }
        }
        return uiState.redirectAdmin();
    }
}