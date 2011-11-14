package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
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
    
    private long id;
    private CRUDActionEnum action;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    @Inject
    NewsletterSubscriptorService subscriptorCRUDService;
    
    
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
    
    
    
    public String redirectEditSubscriber() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
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
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        return "deleteSubscriber";
    }
    
}