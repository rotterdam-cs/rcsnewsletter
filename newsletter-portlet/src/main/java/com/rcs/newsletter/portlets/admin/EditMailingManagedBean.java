package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author juan
 */
@ManagedBean
@Scope("session")
public class EditMailingManagedBean {
    
    //////////////// DEPENDENCIES //////////////////
    @Inject
    private NewsletterMailingService service;
    
    
    /////////////// L10N KEYS //////////////////////
    public static final String CREATE_SAVE_BUTTON_KEY = "newsletter.admin.mailing.createbutton";
    public static final String UPDATE_SAVE_BUTTON_KEY = "newsletter.admin.mailing.updatebutton";
    
    public static final String CREATE_TITLE_KEY = "newsletter.admin.mailing.createtitle";
    public static final String UPDATE_TITLE_KEY = "newsletter.admin.mailing.updatetitle";
    
    
    /////////////// PROPERTIES ////////////////////
    
    private CRUDActionEnum currentAction;
    
    private Long categoryId;
    private Long articleId;
    private String mailingName;
    
    //////////////// METHODS //////////////////////
    @PostConstruct
    public void init() {
        currentAction = CRUDActionEnum.CREATE;
    }
    
    public String save() {
        System.out.println(categoryId);
        System.out.println(articleId);
        System.out.println(mailingName);
        
        NewsletterMailing mailing = new NewsletterMailing();
        
        ServiceActionResult result = service.save(mailing);
        
        if (result.isSuccess()) {
            return "admin";
        } else {
            
        }
        
        return null;
    }
    
    /////////////// GETTERS && SETTERS ////////////////
    public CRUDActionEnum getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(CRUDActionEnum currentAction) {
        this.currentAction = currentAction;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getMailingName() {
        return mailingName;
    }

    public void setMailingName(String mailingName) {
        this.mailingName = mailingName;
    }
    
    /**
     * Get the l10n key for the title of the page.
     * @return 
     */
    public String getTitleKey() {
        if (currentAction == CRUDActionEnum.CREATE) {
            return CREATE_TITLE_KEY;
        }
        
        if (currentAction == CRUDActionEnum.UPDATE) {
            return UPDATE_TITLE_KEY;
        }
        
        return null;
    }

    /**
     * Get the l10n key for the save button of the page.
     * @return 
     */
    public String getSaveButtonKey() {
        if (currentAction == CRUDActionEnum.CREATE) {
            return CREATE_SAVE_BUTTON_KEY;
        }
        
        if (currentAction == CRUDActionEnum.UPDATE) {
            return UPDATE_SAVE_BUTTON_KEY;
        }
        
        return null;
    }
    
    
}
