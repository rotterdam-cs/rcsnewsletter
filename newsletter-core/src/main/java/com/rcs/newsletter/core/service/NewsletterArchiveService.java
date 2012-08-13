
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterArchiveDTO;
import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
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
    ServiceActionResult saveArchive(NewsletterMailing mailing, String emailBody, ThemeDisplay themeDisplay);

    
    /**
     * Returns a list of archive entries using paging
     * @param themeDisplay
     * @param calculateStart
     * @param rows
     * @param name
     * @param ORDER_BY_ASC
     * @return 
     */
    ServiceActionResult<ListResultsDTO<NewsletterArchiveDTO>> findAllArchives(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order);

    
    /**
     * Return archive details
     * @param id
     * @return 
     */
    ServiceActionResult<NewsletterArchiveDTO> findArchive(Long archiveId);
}
