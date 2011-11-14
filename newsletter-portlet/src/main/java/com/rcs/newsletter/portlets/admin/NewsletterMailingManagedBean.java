package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterMailingService;
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
    private NewsletterMailingService service;
    
    @Inject
    private NewsletterCategoryService categoryService;
    
    private List<NewsletterMailing> mailingList;
    private List<NewsletterCategory> categories;
    
    @PostConstruct
    public void init() {
        mailingList = service.findAll().getPayload();
        categories = categoryService.findAll().getPayload();
    }
    
    //the mailing list.
    public List<NewsletterMailing> getMailingList() {
        return mailingList;
    }

    public List<NewsletterCategory> getCategories() {
        return categories;
    }
    
    public String addMailing() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.MAILING_TAB_INDEX);
        return "editmailing";
    }
    
}
