package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriberAdminManagedBean {
    private static Log log = LogFactoryUtil.getLog(SubscriberAdminManagedBean.class);    
    private NewsletterCategory filterCategory;
    private Long categoryId;
    private String test;
    
    @Inject
    NewsletterSubscriptorService subscriptorService;
    
    @Inject
    NewsletterCategoryService categoryService;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    List<NewsletterSubscriptor> subscribers;
    
    @PostConstruct
    public void init() {        
        if (filterCategory == null) {        
            ServiceActionResult<List<NewsletterSubscriptor>> result = subscriptorService.findAll();

            if(result.isSuccess()) {
                subscribers = result.getPayload();
            }
        } else {
            subscribers = subscriptorService.findByCategory(filterCategory);            
        }
        
    }
    
    public List<NewsletterSubscriptor> getSubscribers() {
        return subscribers;
    }

    public NewsletterCategory getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(NewsletterCategory filterCategory) {
        this.filterCategory = filterCategory;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;        
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
    
    
    public void changeCategory(AjaxBehaviorEvent event) {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        log.error("Change Category************" + getCategoryId());
        try {
            if (getCategoryId() == 0) {
                subscribers = subscriptorService.findAll().getPayload();
            }else{
                filterCategory = categoryService.findById(categoryId).getPayload();
                subscribers = subscriptorService.findByCategory(filterCategory);            
            }
        } catch (Exception ex) {
            log.error("Error " + ex);
        }

    }

}
