
package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class CategoryAdminManagedBean {
    
    @Inject
    NewsletterCategoryService categoryService;
    
    List<NewsletterCategory> categorys;
    
    @PostConstruct
    public void init() {
        categorys = categoryService.findAllNewsletterCategorys(true);
    }
    
    public List<NewsletterCategory> getCategorys() {
        return categorys;
    }
    
}
