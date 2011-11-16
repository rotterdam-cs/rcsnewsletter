package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptionService extends CRUDService<NewsletterSubscription> {    
    
    List<NewsletterSubscription> findBySubscriptor(NewsletterSubscriptor newsletterSubscriptor);
    
    NewsletterSubscription findBySubscriptorAndCategory(NewsletterSubscriptor newsletterSubscriptor, NewsletterCategory newsletterCategory);    
    
    List<NewsletterSubscription> findSubscriptionByKey(String s);
    
}
