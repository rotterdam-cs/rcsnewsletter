package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import static com.rcs.newsletter.NewsletterConstants.NEWSLETTER_BUNDLE;
import static com.rcs.newsletter.NewsletterConstants.SERVER_MESSAGE_BUNDLE;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class templateCRUDManagedBean {

    private static Log log = LogFactoryUtil.getLog(templateCRUDManagedBean.class);
    private static final String TEMPLATE_HELP_INFO = "newsletter.admin.template.info";
    @Inject
    NewsletterTemplateService templateCRUDService;
    @Inject
    private UserUiStateManagedBean uiState;
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

    public String getHelpPageText() {
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
    }

    //////////////// METHODS //////////////////////
    public String redirectCreateTemplate() {
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

    public String cancel() {
        return "admin?faces-redirect=true";
    }
}
