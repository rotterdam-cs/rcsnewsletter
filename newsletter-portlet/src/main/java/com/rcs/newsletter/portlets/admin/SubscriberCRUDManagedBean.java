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
@Scope("request")
public class SubscriberCRUDManagedBean {    
    
    private static Log log = LogFactoryUtil.getLog(CategoryCRUDManagedBean.class);    
    
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
    
    /////////////// PROPERTIES ////////////////////
    private long id;
    private CRUDActionEnum action;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Long categoryId;
    
    /////////////// GETTERS && SETTERS ////////////////    
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
    
    //////////////// METHODS //////////////////////
    
    /**
     * Redirect Action to Edit
     * @return 
     */
    public String redirectEditSubscriber() {
        ServiceActionResult serviceActionResult = subscriptorCRUDService.findById(this.getId());        
        
        if (serviceActionResult.isSuccess()) {
            NewsletterSubscriptor newsletterSubscriptor = (NewsletterSubscriptor) serviceActionResult.getPayload();
            
            this.email = newsletterSubscriptor.getEmail();
            this.firstName = newsletterSubscriptor.getFirstName();
            this.middleName = newsletterSubscriptor.getMiddleName();
            this.lastName = newsletterSubscriptor.getLastName();           
            
            this.setAction(CRUDActionEnum.UPDATE);
        }
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        return "editSubscriber";
    }       
        
    
    /**
     * Save Subscriber
     * @return 
     */
    public String save() {
        NewsletterSubscriptor subscriptor = subscriptorService.findById(getId()).getPayload();
        
        subscriptor.setEmail(email);
        subscriptor.setFirstName(firstName);
        subscriptor.setMiddleName(middleName);
        subscriptor.setLastName(lastName);
        
        String message = "";
        if (subscriptorService.update(subscriptor).isSuccess()) {
            FacesUtil.infoMessage(message);
        } else {
            FacesUtil.errorMessage(message);
        }
        return "admin";
    }
    
    
    /**
     * Redirect Action to Delete
     * @return 
     */
    public String redirectDeleteSubscriber() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        return "deleteSubscriber";
    }
    
    
    /**
     * Delete Subscribtion and Subscriber
     * @return 
     */
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
            log.error("Deleting subscription for category: " + nls.getCategory().getName() + "and Subscriptor: " + nls.getSubscriptor().getFirstName());
            if ( subscriptionService.delete(nls).isSuccess()) {
                FacesUtil.infoMessage(message);
            } else {
                FacesUtil.errorMessage(message);
            }
            
        }
        return "admin";
    }
}