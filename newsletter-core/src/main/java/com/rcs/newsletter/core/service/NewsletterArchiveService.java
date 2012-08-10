
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterArchiveService extends CRUDService<NewsletterArchive> {
        
    /**
     * Creates a NewsletterArchive instance based on a mailing
     * @param mailingDTO
     * @return 
     */
    public ServiceActionResult saveArchive(NewsletterMailing mailing, String emailBody, ThemeDisplay themeDisplay);
}
