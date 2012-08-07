package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
public interface NewsletterTemplateService extends CRUDService<NewsletterTemplate> {
       
    ServiceActionResult<ListResultsDTO<NewsletterTemplate>> findAllTemplates(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order);
    
}
