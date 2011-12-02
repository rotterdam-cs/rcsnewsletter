package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriberAdminManagedBean extends PaginationManagedBean {
    
    private static Log log = LogFactoryUtil.getLog(SubscriberAdminManagedBean.class);    
    private NewsletterCategory filterCategory;
    private int categoryId = 0;
    private Boolean onlyInactive = false;
    private SubscriptionStatus status = null;
    private ResourceBundle messageBundle;
    
    @Inject
    NewsletterSubscriptorService subscriptorService;
    
    @Inject
    NewsletterCategoryService categoryService;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    @Inject
    NewsletterSubscriptionService subscriptionService;
    
    List<NewsletterSubscriptor> subscribers;    
    
    @PostConstruct
    public void init() {
        setPaginationStart(NewsletterConstants.PAGINATION_DEFAULT_START);
        setPaginationLimit(NewsletterConstants.PAGINATION_DEFAULT_LIMIT);
        updateSubscriptors();
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        messageBundle = ResourceBundle.getBundle(LANGUAGE_BUNDLE, facesContext.getViewRoot().getLocale());
    }

    public ResourceBundle getMessageBundle() {
        return messageBundle;
    }

    public void setMessageBundle(ResourceBundle messageBundle) {
        this.messageBundle = messageBundle;
    }
    
    public List<NewsletterSubscriptor> getSubscribers() {
        updateSubscriptors();
        return subscribers;
    }

    public NewsletterCategory getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(NewsletterCategory filterCategory) {
        this.filterCategory = filterCategory;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;        
    }

    public Boolean getOnlyInactive() {
        return onlyInactive;
    }

    public void setOnlyInactive(Boolean onlyInactive) {
        this.onlyInactive = onlyInactive;
    }
    
    public String getCategoryIdAsString() {
        return String.valueOf(categoryId);
    }

    public void changeCategory(AjaxBehaviorEvent event) {
        this.gotoFirstPage();
    }
    
    private void updateResults() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        updateSubscriptors();
    }
    
    private void updateSubscriptors() {
        try {
            if (onlyInactive) {
                status= SubscriptionStatus.INACTIVE;
            } else {
                status= SubscriptionStatus.ACTIVE;
            }
            if (getCategoryId() == 0) {                
                subscribers = subscriptorService.findAllByStatus(getPaginationStart(), getPaginationLimit(), "id", NewsletterConstants.ORDER_BY_ASC, status);                
                setPaginationTotal(subscriptorService.findAllByStatusCount(status));
                
            } else {
                filterCategory = categoryService.findById(categoryId).getPayload();
                subscribers = subscriptorService.findByCategoryAndStatus(filterCategory, getPaginationStart(), getPaginationLimit(), "id", NewsletterConstants.ORDER_BY_ASC, status);
                setPaginationTotal(subscriptorService.findByCategoryAndStatusCount(filterCategory, status));
            }
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    
    public List<NewsletterSubscriptor> getSubscriptorsByFilterCategory() {
        
        List<NewsletterSubscriptor> result = null;
        if (getCategoryId() == 0) {
            result = subscriptorService.findAllByStatus(SubscriptionStatus.ACTIVE);
        } else {
            result = subscriptorService.findByCategoryAndStatus(getFilterCategory(), SubscriptionStatus.ACTIVE);            
        }
        
        return result;
    }
    
    @Override
    public void gotoPage() {
        super.gotoPage();
        updateResults();
    }
    
    @Override
    public void nextPage() {        
        super.nextPage();        
        updateResults();
    }
    
    @Override
    public void prevPage() {        
        super.prevPage();        
        updateResults();
    }
    
    @Override
    public void gotoFirstPage() {        
        super.gotoFirstPage();        
        updateResults();        
    }
    
    @Override
    public void gotoLastPage() {        
        super.gotoLastPage();        
        updateResults();         
    }

}
