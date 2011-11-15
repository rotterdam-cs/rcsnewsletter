package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.io.Serializable;
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
@Scope("session")
public class NewsletterMailingManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    private UserUiStateManagedBean uiState;
    @Inject
    private EditMailingManagedBean mailingBean;
    @Inject
    private NewsletterMailingService service;
    @Inject
    private NewsletterCategoryService categoryService;
    private List<NewsletterMailing> mailingList;
    private List<NewsletterCategory> categories;
    private Long mailingId;

    /**
     * Load the listings on this managed bean.
     */
    @PostConstruct
    public void init() {
        mailingList = service.findAll().getPayload();
        categories = categoryService.findAll().getPayload();
        //workaround for circular dependency injection.
        mailingBean.setMailingManagedBean(this);
    }

    public String addMailing() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.MAILING_TAB_INDEX);
        mailingBean.setCurrentAction(CRUDActionEnum.CREATE);
        return "editmailing";
    }

    public String editMailing() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.MAILING_TAB_INDEX);
        mailingBean.setCurrentAction(CRUDActionEnum.UPDATE);
        mailingBean.setMailingId(mailingId);
        return "editmailing";
    }

    public String beginDeletion() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.MAILING_TAB_INDEX);
        return "deleteMailing";
    }

    public String confirmDeletion() {

        ServiceActionResult<NewsletterMailing> result = service.findById(mailingId);
        if (!result.isSuccess()) {
            FacesUtil.errorMessage("Could not find mailing to delete");
            return null;
        }
        
        result = service.delete(result.getPayload());
        
        if (result.isSuccess()) {
            init();
        }
        
        return "admin";
    }

    public Long getMailingId() {
        return mailingId;
    }

    public void setMailingId(Long mailingId) {
        this.mailingId = mailingId;
    }

    //the mailing list.
    public List<NewsletterMailing> getMailingList() {
        return mailingList;
    }

    public List<NewsletterCategory> getCategories() {
        return categories;
    }
}
