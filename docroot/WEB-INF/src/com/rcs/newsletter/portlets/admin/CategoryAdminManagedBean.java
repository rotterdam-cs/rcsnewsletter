
package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import java.util.List;
import javax.annotation.PostConstruct;
//import javax.inject.Inject;
//import javax.inject.Named;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
//@Named
//@Scope("request")
public class CategoryAdminManagedBean {
    
    private NewsletterSubscriptor subscriptor;
    private long subscriptorId;
    
   /* @Inject
    NewsletterCategoryService categoryService;    
    
    List<NewsletterCategory> categorys;
    
    @Inject
    NewsletterSubscriptorService subscriptorService;
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    @PostConstruct
    public void init() {
        categorys = categoryService.findAllNewsletterCategorys(uiState.getThemeDisplay(), true);
    }
    
    public List<NewsletterCategory> getCategorys() {        
        return categoryService.findAllNewsletterCategorys(uiState.getThemeDisplay(), true);
    }

    public List<NewsletterCategory> getSubscriberCategorys() {
        return categorys;
    }

    public NewsletterSubscriptor getSubscriptor() {
        return subscriptor;
    }

    public void setSubscriptor(NewsletterSubscriptor subscriptor) {
        this.subscriptor = subscriptor;
    }

    public long getSubscriptorId() {
        return subscriptorId;
    }

    public void setSubscriptorId(long subscriptorId) {
        this.subscriptorId = subscriptorId;
        subscriptor = subscriptorService.findById(subscriptorId).getPayload();
    }    */
}
