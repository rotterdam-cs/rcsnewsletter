
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterSubscriptor;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptorService {
    
    boolean addNewsletterSubscriptor(NewsletterSubscriptor newsletterSubscriptor);
    
    boolean updateNewsletterSubscriptor(NewsletterSubscriptor newsletterSubscriptor);
    
    NewsletterSubscriptor findByEmail(String email);
    
}
