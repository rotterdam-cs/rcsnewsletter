
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptorService extends CRUDService<NewsletterSubscriptor> {
    
    ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> findAllByStatusAndCategory(
         ThemeDisplay themeDisplay 
        ,int start
        ,int limit
        ,String ordercrit
        ,String order 
        ,SubscriptionStatus status
        ,long categoryId
    );
    
    ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> findAllByStatusAndCategoryAndCriteria(
            ThemeDisplay themeDisplay 
           ,int start
           ,int limit
           ,String ordercrit
           ,String order 
           ,SubscriptionStatus status
           ,long categoryId
           ,String searchField
           ,String searchString 
       );
    
    int findAllByStatusAndCategoryCount(ThemeDisplay themeDisplay, SubscriptionStatus status, long categoryId);
    
    int findAllByStatusAndCategoryCountAndCriteria(ThemeDisplay themeDisplay, SubscriptionStatus status, long categoryId, String searchField, String searchString );
    
    ServiceActionResult updateSubscriptor(long subscriptorId, String firstName, String lastName, String email);
    
    ServiceActionResult deleteSubscriptor(long subscriptorId);
}
