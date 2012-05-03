package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.util.PortalUtil;
import static com.rcs.newsletter.NewsletterConstants.NEWSLETTER_BUNDLE;
import static com.rcs.newsletter.NewsletterConstants.SERVER_MESSAGE_BUNDLE;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class SubscriptionEmailManagedBean {

    private static Log log = LogFactoryUtil.getLog(SubscriptionEmailManagedBean.class);
    private static final String GREETING_MAIL_INFO = "newsletter.admin.category.greetingmail.info";
    private static final String SUBSCRIPTION_MAIL_INFO = "newsletter.admin.category.subscriptionmail.info";
    private static final String UNSUBSCRIPTION_MAIL_INFO = "newsletter.admin.category.unsubscriptionmail.info";
    @Inject
    NewsletterCategoryService categoryService;
    @Inject
    private UserUiStateManagedBean uiState;
    private int categoryId;
    private NewsletterCategory newsletterCategory;
    private SubscriptionTypeEnum subscriptionType;
    private String subscriptionEmailBody;
    private String helpPageText;
    private Long plid;
    private String doAsUserId;
    //private String htmlCode;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public NewsletterCategory getNewsletterCategory() {
        return newsletterCategory;
    }

    public void setNewsletterCategory(NewsletterCategory newsletterCategory) {
        this.newsletterCategory = newsletterCategory;
    }

    public String getSubscriptionEmailBody() {
        return subscriptionEmailBody;
    }

    public void setSubscriptionEmailBody(String subscriptionEmailBody) {
        this.subscriptionEmailBody = subscriptionEmailBody;
    }

    public SubscriptionTypeEnum getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionTypeEnum subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getHelpPageText() {
        return helpPageText;
    }

    public void setHelpPageText(String helpPageText) {
        this.helpPageText = helpPageText;
    }

    public Long getPlid() {
        setPlid(uiState.getThemeDisplay().getPlid());
        return plid;
    }

    public void setPlid(Long plid) {
        this.plid = plid;
    }

    public String getDoAsUserId() {
        setDoAsUserId(uiState.getThemeDisplay().getDoAsUserId());
        return doAsUserId;
    }

    public void setDoAsUserId(String doAsUserId) {
        this.doAsUserId = doAsUserId;
    }

    public String redirectEditSubscribeMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.setSubscriptionType(SubscriptionTypeEnum.SUBSCRIBE);
        fillData();

        return "editSubscriptionMail";
    }

    public String redirectEditUnsubscribeMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.setSubscriptionType(SubscriptionTypeEnum.UNSUBSCRIBE);
        fillData();

        return "editSubscriptionMail";
    }

    public String redirectEditGreetingMail() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.LISTS_TAB_INDEX);
        this.setSubscriptionType(SubscriptionTypeEnum.GREETING);
        fillData();

        return "editSubscriptionMail";
    }

    /**
     * Method that fill the data in the managed bean:
     * JournalArticle
     * JournalArticleId
     * Subscription email body (article content viewer)
     * In case that doesnt exists in the category, fills those 
     * fields with empty values
     */
    private void fillData() {
        uiState.refresh();
        ServiceActionResult serviceActionResult = categoryService.findById(categoryId);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle newsletterMessageBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        if (serviceActionResult.isSuccess()) {
            this.newsletterCategory = (NewsletterCategory) serviceActionResult.getPayload();
            String emailContentBody = "";
            String helpText = "";
            switch (getSubscriptionType()) {
                case SUBSCRIBE:
                    emailContentBody = newsletterCategory.getSubscriptionEmail();
                    helpText = newsletterMessageBundle.getString(SUBSCRIPTION_MAIL_INFO);
                    break;
                case UNSUBSCRIBE:
                    emailContentBody = newsletterCategory.getUnsubscriptionEmail();
                    helpText = newsletterMessageBundle.getString(UNSUBSCRIPTION_MAIL_INFO);
                    break;
                case GREETING:
                    emailContentBody = newsletterCategory.getGreetingEmail();
                    helpText = newsletterMessageBundle.getString(GREETING_MAIL_INFO);
                    break;
            }
            this.setSubscriptionEmailBody(emailContentBody);
            this.setHelpPageText(helpText);
        }
    }

    public String save() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());

        ServiceActionResult<NewsletterCategory> serviceActionResult = categoryService.findById(categoryId);
        if (serviceActionResult.isSuccess()) {
            newsletterCategory = serviceActionResult.getPayload();
            switch (getSubscriptionType()) {
                case SUBSCRIBE:
                    newsletterCategory.setSubscriptionEmail(getSubscriptionEmailBody());
                    break;
                case UNSUBSCRIBE:
                    newsletterCategory.setUnsubscriptionEmail(getSubscriptionEmailBody());
                    break;
                case GREETING:
                    newsletterCategory.setGreetingEmail(getSubscriptionEmailBody());
                    break;
            }

            ServiceActionResult<NewsletterCategory> result = null;

            result = categoryService.update(newsletterCategory);

            if (result.isSuccess()) {
                String infoMsg = serverMessageBundle.getString("newsletter.admin.subscriptionmail.saved");
                uiState.setSuccesMessage(infoMsg);
            } else {
                String errorMsg = serverMessageBundle.getString("newsletter.admin.subscriptionmail.notsaved");
                uiState.setErrorMessage(errorMsg);
            }
        } else {
            String errorMsg = serverMessageBundle.getString("newsletter.admin.subscriptionmail.notsaved");
            uiState.setErrorMessage(errorMsg);
        }

        return "admin?faces-redirect=true";
    }
    public String getEditorLanguage(){
        PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        String languageId = LocaleUtil.toLanguageId(uiState.getThemeDisplay().getLocale());//ParamUtil.getString(request, "languageId");
        
        log.debug("************* Language Id : " + languageId);
        return languageId;
                
    }
    
    public String getFileBrowserUrl(){
        
        
        
        String result = null;

        String mainPath = uiState.getThemeDisplay().getPathMain();

        HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());

        Map<String, String> fileBrowserParamsMap = (Map<String, String>) request.getAttribute("liferay-ui:input-editor:fileBrowserParams");

        String fileBrowserParams = marshallParams(fileBrowserParamsMap);

        String portletId = PortalUtil.getPortletId(request);

        long groupId = uiState.getThemeDisplay().getDoAsGroupId();

        StringBundler sb = new StringBundler(10);

        sb.append(mainPath);
        sb.append("/portal/fckeditor?p_l_id=");
        sb.append(uiState.getThemeDisplay().getPlid());
        sb.append("&p_p_id=");
        sb.append(HttpUtil.encodeURL(portletId));
        sb.append("&doAsUserId=");
        sb.append(HttpUtil.encodeURL(uiState.getThemeDisplay().getDoAsUserId()));
        sb.append("&doAsGroupId=");
        sb.append(HttpUtil.encodeURL(String.valueOf(groupId)));
        sb.append(fileBrowserParams);

        String connectorURL = HttpUtil.encodeURL(sb.toString());


        result = PortalUtil.getPathContext() + "/html/js/editor/ckeditor/editor/filemanager/browser/liferay/browser.html?Connector=" + connectorURL;

        return result;
    }
    private String marshallParams(Map<String, String> params) {
        StringBundler sb = new StringBundler();

        if (params != null) {
            for (Map.Entry<String, String> configParam : params.entrySet()) {
                sb.append(StringPool.AMPERSAND);
                sb.append(configParam.getKey());
                sb.append(StringPool.EQUAL);
                sb.append(HttpUtil.encodeURL(configParam.getValue()));
            }
        }

        return sb.toString();
    }
    
    
    
}
