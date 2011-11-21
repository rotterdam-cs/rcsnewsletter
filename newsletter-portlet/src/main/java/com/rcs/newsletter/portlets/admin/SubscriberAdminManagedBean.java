package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
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
    
    List<NewsletterSubscriptor> subscribers;
    
    @PostConstruct
    public void init() {
        
        setPaginationStart(0);
        setPaginationLimit(5);
        if (filterCategory == null) {        
            subscribers = subscriptorService.findAll(getPaginationStart(), getPaginationLimit()).getPayload();
            setPaginationTotal(subscriptorService.findAllCount());            
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;        
    }

    public void changeCategory(AjaxBehaviorEvent event) {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        this.gotoFirstPage();
    }
    
    private void updateResults() {
        try {
            if (getCategoryId() == 0) {
                subscribers = subscriptorService.findAll(getPaginationStart(), getPaginationLimit()).getPayload();
                setPaginationTotal(subscriptorService.findAllCount());
            }else{
                filterCategory = categoryService.findById(categoryId).getPayload();
                subscribers = subscriptorService.findByCategory(filterCategory, getPaginationStart(), getPaginationLimit());
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
