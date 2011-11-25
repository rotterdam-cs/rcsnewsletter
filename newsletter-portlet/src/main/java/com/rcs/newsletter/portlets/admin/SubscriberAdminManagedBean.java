package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;


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

    public void changeCategory(AjaxBehaviorEvent event) {
        this.gotoFirstPage();
    }
    
    private void updateResults() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        updateSubscriptors();
    }
    
    private void updateSubscriptors() {
        try {
            if (getCategoryId() == 0) {
                subscribers = subscriptorService.findAll(getPaginationStart(), getPaginationLimit(), "id", NewsletterConstants.ORDER_BY_ASC).getPayload();
                setPaginationTotal(subscriptorService.findAllCount());
            } else if (getCategoryId() == -1) {
                subscribers = subscriptorService.findAll(getPaginationStart(), getPaginationLimit(), "id", NewsletterConstants.ORDER_BY_ASC).getPayload();
                setPaginationTotal(subscriptorService.findAllCount());
            }else {
                filterCategory = categoryService.findById(categoryId).getPayload();
                subscribers = subscriptorService.findByCategory(filterCategory, getPaginationStart(), getPaginationLimit(), "id", NewsletterConstants.ORDER_BY_ASC);
                setPaginationTotal(subscriptorService.findByCategoryCount(filterCategory));
            }
        } catch (Exception ex) {
            log.error(ex);
        }
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
