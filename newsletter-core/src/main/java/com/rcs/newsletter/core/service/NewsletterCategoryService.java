
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterCategoryService extends CRUDService<NewsletterCategory> {
    
    NewsletterCategory findByKey(String categoryKey);
    
    List<NewsletterCategory> findAllNewsletterCategorys(boolean fetchSubscriptors);
    
}
