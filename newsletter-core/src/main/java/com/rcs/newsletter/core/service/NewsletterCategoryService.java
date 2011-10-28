
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterCategoryService {
    
    boolean addNewsletterCategory(NewsletterCategory newsletterCategory);

    boolean updateNewsletterCategory(NewsletterCategory newsletterCategory);
    
    NewsletterCategory findById(long newsletterCategoryId, boolean fetchSubscriptors);
    
    NewsletterCategory findByKey(String categoryKey);
    
    List<NewsletterCategory> findAllNewsletterCategorys(boolean fetchSubscriptors);

    boolean deleteNewsletterCategory(NewsletterCategory newsletterCategory);    
    
}
