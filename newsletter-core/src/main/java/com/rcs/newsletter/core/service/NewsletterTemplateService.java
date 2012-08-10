package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.TemplateDTO;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
public interface NewsletterTemplateService extends CRUDService<NewsletterTemplate> {
    
    /**
     * Returns a list of all templates using paging
     * @param themeDisplay
     * @param start
     * @param limit
     * @param ordercrit
     * @param order
     * @return 
     */
    ServiceActionResult<ListResultsDTO<TemplateDTO>> findAllTemplates(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order);

    /**
     * Returns all templates
     * @param themeDisplay
     * @return 
     */
    List<TemplateDTO> findAllTemplates(ThemeDisplay themeDisplay);
    
    
    /**
     * Returns a single template by id
     * @param id
     * @return 
     */
    ServiceActionResult<TemplateDTO> findTemplate(Long id);

    
    /**
     * Stores a template in the database
     * @param templateDTO
     * @return 
     */
    ServiceActionResult<TemplateDTO> saveTemplate(ThemeDisplay themeDisplay, TemplateDTO templateDTO);

    
    /**
     * Deletes the template from the database
     * @param id
     * @return 
     */
    public ServiceActionResult deleteTemplate(ThemeDisplay themeDisplay, Long templateId);
    
    
    
    /**
     * Returns a count of all blocks [block] [/block] in the template
     * @param templateId
     * @return 
     */
    int countBlocksInTemplate(Long templateId);
   
        
}
