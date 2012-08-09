package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.dtos.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptionService extends CRUDService<NewsletterSubscription> {    
    
    ServiceActionResult<NewsletterSubscriptionDTO> findSubscriptionBySubscriptorId(long subscriptorId);
    
    NewsletterSubscription findBySubscriptorAndCategory(NewsletterSubscriptor newsletterSubscriptor, NewsletterCategory newsletterCategory);
}
