package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
public interface NewsletterTemplateBlockService  extends CRUDService<NewsletterTemplateBlock> {
    
    //List<NewsletterTemplateBlock> findNewsletterTemplateBlocksByTemplate(NewsletterTemplate template);
    ServiceActionResult<List<NewsletterTemplateBlock>> findNewsletterTemplateBlocksByTemplate();
}
