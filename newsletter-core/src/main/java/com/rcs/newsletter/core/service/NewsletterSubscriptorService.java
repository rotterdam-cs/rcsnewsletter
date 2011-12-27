
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptorService extends CRUDService<NewsletterSubscriptor> {
    
    ServiceActionResult<NewsletterSubscriptor> findByEmail(ThemeDisplay themeDisplay, String email);   
    
    List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory);
    
    List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory, int start, int limit);
    
    List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory, int start, int limit, String ordercrit, String order);
    
    List<NewsletterSubscriptor> findByCategoryAndStatus(NewsletterCategory newsletterCategory, int start, int limit, String ordercrit, String order, SubscriptionStatus status);
    
    List<NewsletterSubscriptor> findByCategoryAndStatus(NewsletterCategory newsletterCategory, SubscriptionStatus status);
    
    List<NewsletterSubscriptor> findAllByStatus(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order, SubscriptionStatus status);
    
    List<NewsletterSubscriptor> findAllByStatus(ThemeDisplay themeDisplay, SubscriptionStatus status);    
    
    int findAllByStatusCount(ThemeDisplay themeDisplay, SubscriptionStatus status);
    
    int findByCategoryCount(NewsletterCategory newsletterCategory);
    
    int findByCategoryAndStatusCount(NewsletterCategory newsletterCategory, SubscriptionStatus status);
    
}
