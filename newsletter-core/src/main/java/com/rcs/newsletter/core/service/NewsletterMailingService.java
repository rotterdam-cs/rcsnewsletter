package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterMailing;

/**
 *
 * @author juan
 */
public interface NewsletterMailingService extends CRUDService<NewsletterMailing> {

    /**
     * Send the mailing to a test email address.
     * @param mailingId
     * @param themeDisplay 
     * @param testEmail
     */
    void sendTestMailing(Long mailingId, String testEmail, ThemeDisplay themeDisplay);

    /**
     * Send the mailing to everyone.
     * @param mailingId
     * @param themeDisplay 
     */
    void sendMailing(Long mailingId, ThemeDisplay themeDisplay, Long archiveId);    
}
