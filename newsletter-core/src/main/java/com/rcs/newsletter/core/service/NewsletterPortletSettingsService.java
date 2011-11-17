package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.RegistrationConfig;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author juan
 */
public interface NewsletterPortletSettingsService {
    /**
     * Find the registration portlet config by portlet id.
     * @param portletId
     * @return 
     */
    RegistrationConfig findConfig(String portletId);
    
    /**
     * Update the registration portlet config.
     * @param portletId
     * @param config
     * @return 
     */
    ServiceActionResult updateConfig(String portletId, RegistrationConfig config);
}
