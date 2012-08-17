package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterTemplateDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
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
    ServiceActionResult<ListResultsDTO<NewsletterTemplateDTO>> findAllTemplates(ThemeDisplay themeDisplay, GridForm gridForm, String orderField, String orderType);

    /**
     * Returns all templates
     * @param themeDisplay
     * @return 
     */
    List<NewsletterTemplateDTO> findAllTemplates(ThemeDisplay themeDisplay);
    
    
    /**
     * Returns a single template by id
     * @param id
     * @return 
     */
    ServiceActionResult<NewsletterTemplateDTO> findTemplate(Long id);

    
    /**
     * Stores a template in the database
     * @param templateDTO
     * @return 
     */
    ServiceActionResult<NewsletterTemplateDTO> saveTemplate(ThemeDisplay themeDisplay, NewsletterTemplateDTO templateDTO);

    
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
