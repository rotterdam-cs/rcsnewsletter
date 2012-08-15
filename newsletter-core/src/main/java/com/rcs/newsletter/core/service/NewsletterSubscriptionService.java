package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.CreateMultipleSubscriptionsResult;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterSubscriptionService extends CRUDService<NewsletterSubscription> {    
    
    /**
     * Returns a subscription by subscriptorId
     * @param subscriptorId
     * @return 
     */
    ServiceActionResult<List<NewsletterSubscriptionDTO>> findSubscriptionsBySubscriptorId(long subscriptorId);

    
    /**
     * Saves multiple subscriptions (used when importing)
     * @param result
     * @param themeDisplay
     * @param categoryId
     * @param newSubscriptions 
     */
    void createSubscriptionsForCategory(CreateMultipleSubscriptionsResult result, ThemeDisplay themeDisplay, long categoryId, List<NewsletterSubscriptionDTO> newSubscriptions);
    
    
    /**
     * Saves a subscription for a particular subscriptor
     * @param subscriptionDTO
     * @param themeDisplay
     * @return 
     */
    ServiceActionResult createSubscription(NewsletterSubscriptionDTO subscriptionDTO, ThemeDisplay themeDisplay);

    
    /**
     * Activates a subscription
     * @param subscriptionId
     * @param activationKey
     * @param themeDisplay
     * @return 
     */
    ServiceActionResult activateSubscription(Long subscriptionId, String activationKey, ThemeDisplay themeDisplay);
    
     /**
     * Deactivates a subscription
     * @param subscriptionId
     * @param activationKey
     * @param themeDisplay
     * @return 
     */
    ServiceActionResult deactivateSubscription(Long subscriptionId, String deactivationKey, ThemeDisplay themeDisplay);
}
