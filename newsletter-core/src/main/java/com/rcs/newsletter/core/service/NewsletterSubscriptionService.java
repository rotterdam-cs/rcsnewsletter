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
    
    ServiceActionResult<List<NewsletterSubscriptionDTO>> findSubscriptionsBySubscriptorId(long subscriptorId);

    void createSubscriptionsForCategory(CreateMultipleSubscriptionsResult result, ThemeDisplay themeDisplay, long categoryId, List<NewsletterSubscriptionDTO> newSubscriptions);
}
