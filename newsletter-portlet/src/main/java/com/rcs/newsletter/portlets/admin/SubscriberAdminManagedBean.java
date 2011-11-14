package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class SubscriberAdminManagedBean {
    private static Log log = LogFactoryUtil.getLog(SubscriberAdminManagedBean.class);    
    private NewsletterCategory filterCategory;
    
    @Inject
    NewsletterSubscriptorService subscriptorService;
    
    List<NewsletterSubscriptor> subscribers;
    
    @PostConstruct
    public void init() {        
//        if (filterCategory == null) {        
            ServiceActionResult<List<NewsletterSubscriptor>> result = subscriptorService.findAll();

            if(result.isSuccess()) {
                subscribers = result.getPayload();
            }
//        } else {
//            subscribers = subscriptorService.findByCategory(filterCategory);            
//        }
        
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
    
    
    public void changeCategory(ValueChangeEvent event) {        
        //filterCategory = (NewsletterCategory) event.getNewValue();
        log.error("Change Category************");
        log.error("************" + event.getNewValue());        
        //this.subscriptionEmailArticle = (JournalArticle) event.getNewValue();        
        //this.subscriptionEmailBody = subscriptionEmailArticle.getContent();        
        //System.out.println("EmailBody " + subscriptionEmailBody);
    }
    
}
