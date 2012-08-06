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
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.Map;
import java.util.ResourceBundle;
/*import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;*/
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
/*@Named
@Scope("request")
@ViewScoped*/
public class TemplateCRUDManagedBean {

    private static Log log = LogFactoryUtil.getLog(TemplateCRUDManagedBean.class);
    private static final String TEMPLATE_HELP_INFO = "newsletter.admin.template.info";
    /*@Inject
    NewsletterTemplateService templateCRUDService;
    @Inject
    private UserUiStateManagedBean uiState;*/
    /////////////// PROPERTIES ////////////////////
    private long id;
    private CRUDActionEnum action;
    private String name;
    private String template;
    private String helpPageText;
    private Long plid;
    private String doAsUserId;

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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public String getHelpPageText() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle newsletterMessageBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
        helpPageText = newsletterMessageBundle.getString(TEMPLATE_HELP_INFO);
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
    }*/

    //////////////// METHODS //////////////////////
    /*public String redirectCreateTemplate() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.TEMPLATE_TAB_INDEX);
        this.setAction(CRUDActionEnum.CREATE);
        return "editTemplate";
    }

    public String redirectEditTemplate() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.TEMPLATE_TAB_INDEX);
        ServiceActionResult serviceActionResult = templateCRUDService.findById(getId());
        if (serviceActionResult.isSuccess()) {
            NewsletterTemplate newsletterTemplate = (NewsletterTemplate) serviceActionResult.getPayload();
            this.name = newsletterTemplate.getName();
            this.template = newsletterTemplate.getTemplate();
            this.setAction(CRUDActionEnum.UPDATE);
        } else {
            return "admin?faces-redirect=true";
        }

        return "editTemplate";
    }

    public String redirectDeleteTemplate() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.TEMPLATE_TAB_INDEX);
        return "deleteTemplate";
    }

    public String save() {

        log.info("Executing save() in TemplateCRUDManagedBean");

        NewsletterTemplate newsletterTemplate = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());
        String message = "";

        if (getAction() == null) {
            log.info("Action null");
            this.setAction(CRUDActionEnum.CREATE);
        }

        switch (getAction()) {
            case CREATE:
                newsletterTemplate = new NewsletterTemplate();
                newsletterTemplate.setGroupid(uiState.getGroupid());
                newsletterTemplate.setCompanyid(uiState.getCompanyid());

                fillNewsletterTemplate(newsletterTemplate);
                ServiceActionResult<NewsletterTemplate> saveResult = templateCRUDService.save(newsletterTemplate);

                if (saveResult.isSuccess()) {
                    message = serverMessageBundle.getString("newsletter.admin.template.save.success");
                    uiState.setSuccesMessage(message);
                } else {
                    message = serverMessageBundle.getString("newsletter.admin.template.save.failure");
                    uiState.setErrorMessage(message);
                }
                break;
            case UPDATE:

                ServiceActionResult serviceActionResult = templateCRUDService.findById(getId());
                if (serviceActionResult.isSuccess()) {
                    newsletterTemplate = (NewsletterTemplate) serviceActionResult.getPayload();
                    fillNewsletterTemplate(newsletterTemplate);

                    ServiceActionResult<NewsletterTemplate> updateResult = templateCRUDService.update(newsletterTemplate);

                    if (updateResult.isSuccess()) {
                        message = serverMessageBundle.getString("newsletter.admin.template.update.success");
                        uiState.setSuccesMessage(message);
                    } else {
                        message = serverMessageBundle.getString("newsletter.admin.template.update.failure");
                        uiState.setErrorMessage(message);
                    }
                }
                break;
        }
        
        return "admin?faces-redirect=true";
    }

    public String cancel() {
        return "admin?faces-redirect=true";
    }

    private void fillNewsletterTemplate(NewsletterTemplate newsletterTemplate) {
        newsletterTemplate.setName(name);
        newsletterTemplate.setTemplate(template);
    }

    public String delete() {
        ServiceActionResult<NewsletterTemplate> serviceActionResult = templateCRUDService.findById(getId());
        String message = "";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(SERVER_MESSAGE_BUNDLE, facesContext.getViewRoot().getLocale());

        if (serviceActionResult.isSuccess()) {
            NewsletterTemplate newsletterTemplate = (NewsletterTemplate) serviceActionResult.getPayload();
            ServiceActionResult<NewsletterTemplate> deleteActionResult = templateCRUDService.delete(newsletterTemplate);

            if (deleteActionResult.isSuccess()) {
                message = serverMessageBundle.getString("newsletter.admin.template.delete.success");
                uiState.setSuccesMessage(message);
            } else {
                message = serverMessageBundle.getString("newsletter.admin.template.delete.failure");
                uiState.setErrorMessage(message);
            }
        } else {
            message = serverMessageBundle.getString("newsletter.admin.template.delete.failure");
            uiState.setErrorMessage(message);
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
    }*/
    
}
