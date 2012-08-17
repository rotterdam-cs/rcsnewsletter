package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author juan
 */
public interface NewsletterPortletSettingsService {
	
	/**
	 * Validate portlet config settings
	 * @param categoryId
	 * @param themeDisplay
	 * @return
	 */
	public ServiceActionResult validateSettings(Long categoryId, ThemeDisplay themeDisplay);
	
}
