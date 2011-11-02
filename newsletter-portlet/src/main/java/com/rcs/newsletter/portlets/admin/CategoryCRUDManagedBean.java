
package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class CategoryCRUDManagedBean {
    
    @Inject
    NewsletterCategoryService categoryCRUDService;
    
    private long id;
    private String name;
    private String fromName;
    private String fromEmail;
    private String description;
    private boolean active;
    private long articleId;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void save() {
        NewsletterCategory newsletterCategory = new NewsletterCategory();
        
        newsletterCategory.setName(name);
        newsletterCategory.setDescription(description);
        newsletterCategory.setActive(active);
        
        categoryCRUDService.save(newsletterCategory);     
    }
    
    public void delete() {
        ServiceActionResult result = categoryCRUDService.findById(id);
        if(result.isSuccess()) {
            NewsletterCategory newsletterCategory = (NewsletterCategory) result.getPayload();
            System.out.println("Deleting " + newsletterCategory.getName());
            categoryCRUDService.delete(newsletterCategory);
        }
    }
}
