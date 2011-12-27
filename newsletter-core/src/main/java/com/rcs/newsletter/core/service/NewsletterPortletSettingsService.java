package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
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
    RegistrationConfig findConfig(ThemeDisplay themeDisplay, String portletId);
    
    /**
     * Update the registration portlet config.
     * @param portletId
     * @param config
     * @return 
     */
    ServiceActionResult updateConfig(ThemeDisplay themeDisplay, String portletId, RegistrationConfig config);
}
