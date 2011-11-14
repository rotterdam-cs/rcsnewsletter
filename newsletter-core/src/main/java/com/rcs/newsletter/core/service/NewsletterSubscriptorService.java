
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterSubscriptor;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptorService extends CRUDService<NewsletterSubscriptor> {
    
    NewsletterSubscriptor findByEmail(String email);    
}
