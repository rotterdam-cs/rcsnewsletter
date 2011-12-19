package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class NewsletterTemplateManagedBean {
    
    List<NewsletterTemplate> templates;
    
    @Inject
    NewsletterTemplateService templateService;
    
    @PostConstruct
    public void init() {
        templates = templateService.findAll().getPayload();
    }

    public List<NewsletterTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<NewsletterTemplate> templates) {
        this.templates = templates;
    }
   
    
}