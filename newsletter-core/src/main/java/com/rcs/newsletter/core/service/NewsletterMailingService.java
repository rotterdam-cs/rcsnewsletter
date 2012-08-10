package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.ArticleDTO;
import com.rcs.newsletter.core.dto.MailingDTO;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

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
    
    /**
     * 
     * @param mailingId
     * @param themeDisplay
     * @return 
     */
    String getEmailFromTemplate(Long mailingId, ThemeDisplay themeDisplay); 
    
    /**
     * Validates the template format
     * @param mailingId
     * @return 
     */
    boolean validateTemplateFormat(Long mailingId);

    
    
    /**
     * Returns all mailings
     * @param themeDisplay
     * @param calculateStart
     * @param rows
     * @param name
     * @param ORDER_BY_ASC
     * @return 
     */
    ServiceActionResult<ListResultsDTO<MailingDTO>> findAllMailings(ThemeDisplay themeDisplay, int start, int limit, String orderField, String orderType);

    
    /**
     * Returns a single mailing instance by id
     * @param id
     * @return 
     */
    ServiceActionResult<MailingDTO> findMailing(Long id, ThemeDisplay themeDisplay);
    
    /**
     * Saves a mailing instance
     * @param themeDisplay
     * @param mailingDTO
     * @return 
     */
    ServiceActionResult<MailingDTO> saveMailing(ThemeDisplay themeDisplay, MailingDTO mailingDTO);
    
    /**
     * Deletes a mailing instance
     * @param themeDisplay
     * @param mailingId
     * @return 
     */
    ServiceActionResult<MailingDTO> deleteMailing(ThemeDisplay themeDisplay, Long mailingId);
    
    
     /**
     * Returns all articles for mailing
     * @param themeDisplay
     * @return 
     */
    public List<ArticleDTO> findAllArticlesForMailing(ThemeDisplay themeDisplay);
    
    
}
