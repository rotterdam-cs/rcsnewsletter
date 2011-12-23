package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import java.util.List;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
public interface NewsletterTemplateBlockService  extends CRUDService<NewsletterTemplateBlock> {
    
    List<NewsletterTemplateBlock> findAllByMailing(NewsletterMailing mailing);

}
