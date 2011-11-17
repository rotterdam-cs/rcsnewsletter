package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.admin.dto.MailingTableRow;
import com.rcs.newsletter.util.FacesUtil;
import java.io.Serializable;
import java.util.LinkedList;
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
    private List<MailingTableRow> mailingList;
    private List<NewsletterCategory> categories;
    private Long mailingId;
    private String testEmail;
    private MailingTableRow selectedMailing;

    /**
     * Load the listings on this managed bean.
     */
    @PostConstruct
    public void init() {
        mailingList = createMailingsList(service.findAll().getPayload());
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
    
    public void sendTestMailing() {
        if (selectedMailing == null) {
            FacesUtil.infoMessage("Please select one row to send the test email");
            return;
        }
        service.sendTestMailing(selectedMailing.getMailing().getId(), testEmail);
        FacesUtil.infoMessage("Test email is scheduled to be sent");
    }
    
    public void sendMailing() {
        if (selectedMailing == null) {
            FacesUtil.infoMessage("Please select one row to send the mailing");
            return;
        }
        service.sendMailing(selectedMailing.getMailing().getId());
        FacesUtil.infoMessage("Mailing scheduled to be sent.");
    }
    
    public Long getMailingId() {
        return mailingId;
    }

    public void setMailingId(Long mailingId) {
        this.mailingId = mailingId;
    }

    //the mailing list.
    public List<MailingTableRow> getMailingList() {
        return mailingList;
    }

    public List<NewsletterCategory> getCategories() {
        return categories;
    }

    public MailingTableRow getSelectedMailing() {
        return selectedMailing;
    }

    public void setSelectedMailing(MailingTableRow selectedMailing) {
        this.selectedMailing = selectedMailing;
    }

    public String getTestEmail() {
        return testEmail;
    }

    public void setTestEmail(String testEmail) {
        this.testEmail = testEmail;
    }

    private List<MailingTableRow> createMailingsList(List<NewsletterMailing> payload) {
        List<MailingTableRow> ret = new LinkedList<MailingTableRow>();
        
        for (NewsletterMailing newsletterMailing : payload) {
            ret.add(new MailingTableRow(newsletterMailing, uiState.getTitleByArticleId(newsletterMailing.getArticleId())));
        }
        
        return ret;
    }
    
}
