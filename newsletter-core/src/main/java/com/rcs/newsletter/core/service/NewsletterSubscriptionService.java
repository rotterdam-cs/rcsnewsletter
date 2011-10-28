package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptionService {

    boolean addNewsletterSubscription(NewsletterSubscription newsletterSubscription);
    
    boolean updateNewsletterSubscription(NewsletterSubscription newsletterSubscription);

    List<NewsletterSubscription> findAllNewsletterSubscriptions();
    
    NewsletterSubscription findById(long newsletterSubscriptionId);
    
    NewsletterSubscription findBySubscriptor(NewsletterSubscriptor newsletterSubscriptor);
    
    NewsletterSubscription findBySubscriptorAndCategory(NewsletterSubscriptor newsletterSubscriptor, NewsletterCategory newsletterCategory);

    boolean deleteNewsletterSubscription(long newsletterSubscriptionId);
    
    List<NewsletterSubscription> findSubscriptionByKey(String s);
    
}
