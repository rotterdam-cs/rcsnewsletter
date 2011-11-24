
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptorService extends CRUDService<NewsletterSubscriptor> {
    
    ServiceActionResult<NewsletterSubscriptor> findByEmail(String email);   
    
    List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory);
    
    List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory, int start, int limit);
    
    List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory, int start, int limit, String ordercrit, String order);
    
    int findByCategoryCount(NewsletterCategory newsletterCategory);
    
}
