package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import com.rcs.newsletter.core.service.NewsletterTemplateBlockService;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterArchiveService;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.admin.dto.MailingTableRow;
import com.rcs.newsletter.core.service.util.EmailFormat;
import com.rcs.newsletter.util.FacesUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author juan
 */
@Named
@Scope("request")
public class NewsletterMailingManagedBean implements Serializable {
    private static Log log = LogFactoryUtil.getLog(NewsletterMailingManagedBean.class);  
    private static final long serialVersionUID = 1L;
    @Inject
    private UserUiStateManagedBean uiState;
    @Inject
    private EditMailingManagedBean mailingBean;
    @Inject
    private NewsletterMailingService service;
    @Inject
    private NewsletterCategoryService categoryService;
    @Inject
    NewsletterSubscriptionService subscriptionService;
    @Inject
    NewsletterSubscriptorService subscriptorService;
    @Inject
    private NewsletterArchiveService archiveService;
            
    private List<MailingTableRow> mailingList;
    private Long mailingId;
    private String testEmail;
    private MailingTableRow selectedMailing;

    /**
     * Load the listings on this managed bean.
     */
    @PostConstruct
    public void init() {
        mailingList = createMailingsList(service.findAll().getPayload());
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
            FacesUtil.errorMessage("Please select one row to send the test email");
            return;
        }        
        service.sendTestMailing(selectedMailing.getMailing().getId(), testEmail, uiState.getThemeDisplay());
        FacesUtil.infoMessage("Test email is scheduled to be sent");
    }
    
    public String sendMailing() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        String message = "";
        
        ServiceActionResult<NewsletterMailing> result = service.findById(mailingId);
        if (!result.isSuccess()) {
            FacesUtil.errorMessage("Could not find mailing to send");
            return "admin";
        }        
        
        NewsletterMailing mailing = result.getPayload();        
        
        //we are going to save this version of the mailing        
        String emailContent = service.getEmailFromTemplate(mailingId, uiState.getThemeDisplay());  
               
        Long archiveId = saveArchiveForMailing(mailing.getName(), mailing.getList().getName(), mailing.getName(), emailContent);       
        
        service.sendMailing(mailingId, uiState.getThemeDisplay(), archiveId);
       
        //We remove the mailing that is already sent
        result = service.delete(mailing);
        
        if (result.isSuccess()) {
            init();
        } else {
            message = serverMessageBundle.getString("newsletter.admin.mailing.delete.failure");        
            FacesUtil.infoMessage(message);
        }
        
        message = serverMessageBundle.getString("newsletter.admin.mailing.sent.succesfully");        
        FacesUtil.infoMessage(message);
        
        return "admin";
    }
    
    /**
     * Save this version of the Mailing
     * @param mailingName
     * @param categoryName
     * @param emailBody 
     */
    private Long saveArchiveForMailing(String mailingName, String categoryName, String articleTitle, String emailBody) {
        NewsletterArchive archive = new NewsletterArchive();
        archive.setDate(new Date());
        archive.setCategoryName(categoryName);
        archive.setArticleTitle(articleTitle);
        archive.setEmailBody(emailBody);
        archive.setName(mailingName);        

        archiveService.save(archive);
        return archive.getId();
    }
    
     public String redirectConfirmSend() {         
        if (selectedMailing == null) {
            FacesUtil.infoMessage("Please select one row to send the mailing");
            return null;
        } else {
            uiState.setAdminActiveTabIndex(UserUiStateManagedBean.MAILING_TAB_INDEX);
            return "listsSendConfirmation";
        }
    }
     
    public String getSelectedListName(){
        if (selectedMailing == null) {            
            return null;
        }else{
            return selectedMailing.getMailing().getList().getName();
        }
    }
    public String getSelectedMailingName(){
        if (selectedMailing == null) {            
            return null;
        }else{
            return selectedMailing.getMailing().getName();
        }
    }
    public String getSelectedArticleName(){
        if (selectedMailing == null) {            
            return null;
        }else{
            return selectedMailing.getArticleTitle();
        }
    }
    
    public int getSelectedListCountMembers(){
        if (selectedMailing == null) {            
            return 0;
        }else{            
            NewsletterCategory filterCategory = selectedMailing.getMailing().getList();
            return subscriptorService.findByCategoryAndStatusCount(filterCategory, SubscriptionStatus.ACTIVE);
        }
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
        return categoryService.findAll().getPayload();
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
            //ret.add(new MailingTableRow(newsletterMailing, uiState.getTitleByArticleId(newsletterMailing.getArticleId())));
            ret.add(new MailingTableRow(newsletterMailing, newsletterMailing.getName()));
        }
        
        return ret;
    }
    
}
