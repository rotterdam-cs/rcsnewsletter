
package com.rcs.newsletter.core.service;

import java.util.Date;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterScheduleDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSchedule;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author Pablo Rendon <pablo.rendon@rotterdam-cs.com>
 */
public interface NewsletterScheduleService extends CRUDService<NewsletterSchedule> {
        
    /**
     * Creates a NewsletterArchive instance based on a mailing
     * @param mailingDTO
     * @return 
     */
    ServiceActionResult<NewsletterScheduleDTO> saveSchedule(NewsletterMailing mailing, Date sendDate, ThemeDisplay themeDisplay);

    
    /**
     * Returns a list of archive entries using paging
     * @param themeDisplay
     * @param calculateStart
     * @param rows
     * @param name
     * @param ORDER_BY_ASC
     * @return 
     */
    ServiceActionResult<ListResultsDTO<NewsletterScheduleDTO>> findAllSchedules(ThemeDisplay themeDisplay, GridForm gridForm, String ordercrit, String order);

    
    /**
     * Return archive details
     * @param id
     * @return 
     */
    ServiceActionResult<NewsletterScheduleDTO> findSchedule(Long scheduleId);

}
