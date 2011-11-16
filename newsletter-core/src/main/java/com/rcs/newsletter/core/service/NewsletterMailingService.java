package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterMailing;

/**
 *
 * @author juan
 */
public interface NewsletterMailingService extends CRUDService<NewsletterMailing> {

    /**
     * Send the mailing to a test email address.
     * @param mailingId
     * @param testEmail
     */
    void sendTestMailing(Long mailingId, String testEmail);

    /**
     * Send the mailing to everyone.
     * @param mailingId
     */
    void sendMailing(Long mailingId);
}
