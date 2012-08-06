package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import static com.rcs.newsletter.NewsletterConstants.SERVER_MESSAGE_BUNDLE;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.util.ResourceBundle;
/*import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;*/
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
/*@Named
@Scope("request")*/
public class CategoryCRUDManagedBean {

    private static Log log = LogFactoryUtil.getLog(CategoryCRUDManagedBean.class);
    
    /*@Inject
    NewsletterCategoryService categoryCRUDService;
    
    @Inject
    private UserUiStateManagedBean uiState;*/
    
    /////////////// PROPERTIES ////////////////////
    private long id;
    private CRUDActionEnum action;
    private String name;
    private String fromName;
    private String fromEmail;
    private String description;
    private boolean active;
    private String adminEmail;

    /////////////// GETTERS && SETTERS ////////////////
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CRUDActionEnum getAction() {
        return action;
    }

    public void setAction(CRUDActionEnum action) {
        this.action = action;
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

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
    
    

    //////////////// METHODS //////////////////////
    /*
    public String redirectCreateCategory() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.setAction(CRUDActionEnum.CREATE);
        
        return "editCategory";
    }

    public String redirectEditCategory() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        ServiceActionResult serviceActionResult = categoryCRUDService.findById(getId());
        if (serviceActionResult.isSuccess()) {
            NewsletterCategory newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
            this.name = newsletterCategory.getName();
            this.description = newsletterCategory.getDescription();
            this.fromEmail = newsletterCategory.getFromEmail();
            this.fromName = newsletterCategory.getFromName();
            this.adminEmail = newsletterCategory.getAdminEmail();
            this.setAction(CRUDActionEnum.UPDATE);
        } else {
            return "admin?faces-redirect=true";
        }

        return "editCategory";
    }

    public String redirectDeleteCategory() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        
        return "deleteCategory";
    }

    public String save() {
        NewsletterCategory newsletterCategory = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        String message = "";        
        switch (getAction()) {
            case CREATE:
                newsletterCategory = new NewsletterCategory();
                newsletterCategory.setGroupid(uiState.getGroupid());
                newsletterCategory.setCompanyid(uiState.getCompanyid());
                
                fillNewsletterCategory(newsletterCategory);
                ServiceActionResult<NewsletterCategory> saveResult = categoryCRUDService.save(newsletterCategory);

                if (saveResult.isSuccess()) {
                    message = serverMessageBundle.getString("newsletter.admin.category.save.success");
                   uiState.setSuccesMessage(message);
                } else {
                    message = serverMessageBundle.getString("newsletter.admin.category.save.failure");
                    uiState.setErrorMessage(message);
                }
                break;
            case UPDATE:
                ServiceActionResult serviceActionResult = categoryCRUDService.findById(getId());
                if (serviceActionResult.isSuccess()) {
                    newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
                    fillNewsletterCategory(newsletterCategory);

                    ServiceActionResult<NewsletterCategory> updateResult = categoryCRUDService.update(newsletterCategory);

                    if (updateResult.isSuccess()) {
                        message = serverMessageBundle.getString("newsletter.admin.category.update.success");
                        uiState.setSuccesMessage(message);
                    } else {
                        message = serverMessageBundle.getString("newsletter.admin.category.update.failure");
                        uiState.setErrorMessage(message);
                    }
                }
                break;
        }
        
        return "admin?faces-redirect=true";
    }

    private void fillNewsletterCategory(NewsletterCategory newsletterCategory) {
        newsletterCategory.setName(name);
        newsletterCategory.setDescription(description);
        newsletterCategory.setFromName(fromName);
        newsletterCategory.setFromEmail(fromEmail);
        newsletterCategory.setAdminEmail(adminEmail);
    }

    public String delete() {
        ServiceActionResult<NewsletterCategory> serviceActionResult = categoryCRUDService.findById(getId());
        String message = "";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        
        if (serviceActionResult.isSuccess()) {
            NewsletterCategory newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
            ServiceActionResult<NewsletterCategory> deleteActionResult = categoryCRUDService.delete(newsletterCategory);

            if (deleteActionResult.isSuccess()) {
                message = serverMessageBundle.getString("newsletter.admin.category.delete.success");
                uiState.setSuccesMessage(message);
            } else {
                message = serverMessageBundle.getString("newsletter.admin.category.delete.failure");
                uiState.setErrorMessage(message);
            }
        } else {
            message = serverMessageBundle.getString("newsletter.admin.category.delete.failure");
            uiState.setErrorMessage(message);
        }

        return "admin?faces-redirect=true";
    }*/
}
