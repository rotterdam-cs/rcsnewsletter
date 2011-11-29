package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author juan
 */
@Named
@Scope("request")
public class EditMailingManagedBean {
    
    //////////////// DEPENDENCIES //////////////////
    @Inject
    private NewsletterMailingService service;
    
    private NewsletterMailingManagedBean mailingManagedBean;
    
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
    private Long mailingId;
    
    //////////////// METHODS //////////////////////
    @PostConstruct
    public void init() {
        currentAction = CRUDActionEnum.CREATE;
    }
    
    public String save() {
        
        NewsletterMailing mailing = null;
        
        switch(currentAction) {
            case CREATE:
                mailing = new NewsletterMailing();                
                break;
            case UPDATE:
                mailing = service.findById(mailingId).getPayload();                
                break;
        }
        
        mailing.setArticleId(articleId);
        mailing.setName(mailingName);
        mailing.setList(findListById(categoryId));
        
        ServiceActionResult result = null;
        
        switch(currentAction) {
            case CREATE:
                result = service.save(mailing);
                break;
            case UPDATE:
                result = service.update(mailing);
                break;
        }
        
        if (result.isSuccess()) {
            mailingManagedBean.init();            
            return "admin";
        } else {
            FacesUtil.errorMessage("Failed to create mailing");
            List<String> validationKeys = result.getValidationKeys();
            for (String key : validationKeys) {
                FacesUtil.errorMessage(key);
            }
        }
        
        return null;
    }    
    
    private NewsletterCategory findListById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        for (NewsletterCategory newsletterCategory : mailingManagedBean.getCategories()) {
            if (categoryId.equals(newsletterCategory.getId())) {
                return newsletterCategory;
            }
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

    public Long getMailingId() {
        return mailingId;
    }
    
    /**
     * Update the current mailing id and put values so the ui can display them
     * @param mailingId 
     */
    public void setMailingId(Long mailingId) {
        this.mailingId = mailingId;
        
        ServiceActionResult<NewsletterMailing> result = service.findById(mailingId);
        
        if (!result.isSuccess()) {
            return;
        }
        
        NewsletterMailing mailing = result.getPayload();
        
        this.mailingName = mailing.getName();
        this.categoryId = mailing.getList().getId();
        this.articleId = mailing.getArticleId();
        
    }

    public void setMailingManagedBean(NewsletterMailingManagedBean mailingManagedBean) {
        this.mailingManagedBean = mailingManagedBean;
    }
    
}
