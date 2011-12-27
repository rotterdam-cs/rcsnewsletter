
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterCategoryService extends CRUDService<NewsletterCategory> {
    
    NewsletterCategory findByKey(String categoryKey);
    
    List<NewsletterCategory> findAllNewsletterCategorys(ThemeDisplay themeDisplay, boolean fetchSubscriptors);
    
    List<NewsletterCategory> findNewsletterCategorysBySubscriber(NewsletterSubscriptor subscriptor);
    
}
