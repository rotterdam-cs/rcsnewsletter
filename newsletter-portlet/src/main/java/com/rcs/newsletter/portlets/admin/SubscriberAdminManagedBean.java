package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
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
@Scope("request")
public class SubscriberAdminManagedBean {
    
    @Inject
    NewsletterSubscriptorService subscriptorService;
    
    List<NewsletterSubscriptor> subscribers;
    
    @PostConstruct
    public void init() {
        ServiceActionResult<List<NewsletterSubscriptor>> result = subscriptorService.findAll();
        
        if(result.isSuccess()) {
            subscribers = result.getPayload();
        }
        
    }
    
    public List<NewsletterSubscriptor> getSubscribers() {
        return subscribers;
    }
    
}
