
package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class CategoryAdminManagedBean {
    
    private NewsletterSubscriptor subscriptor;
    private long subscriptorId;
    
    @Inject
    NewsletterCategoryService categoryService;    
    
    List<NewsletterCategory> categorys;
    
    @Inject
    NewsletterSubscriptorService subscriptorService;
    
    @PostConstruct
    public void init() {
        categorys = categoryService.findAllNewsletterCategorys(true);
    }
    
    public List<NewsletterCategory> getCategorys() {        
        return categoryService.findAllNewsletterCategorys(true);
    }

    public List<NewsletterCategory> getSubscriberCategorys() {
        categorys = categoryService.findNewsletterCategorysBySubscriber(subscriptor);
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
    }    
}
