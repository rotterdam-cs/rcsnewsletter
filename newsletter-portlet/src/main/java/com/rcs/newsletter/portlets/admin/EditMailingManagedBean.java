package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.model.commons.TemplateBlockComparator;
import java.util.Collections;
import com.rcs.newsletter.core.service.NewsletterTemplateBlockService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import com.rcs.newsletter.core.service.util.EmailFormat;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.util.List;
import javax.annotation.PostConstruct;
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
public class EditMailingManagedBean {
    private static Log log = LogFactoryUtil.getLog(EditMailingManagedBean.class);  
    
    @Value("${newsletter.articles.type}")
    private String newsletterArticleType;
    
    @Value("${newsletter.articles.category}")
    private String newsletterArticleCategory;
    
    @Value("${newsletter.articles.tag}")
    private String newsletterArticleTag;
    
    //////////////// DEPENDENCIES //////////////////
    @Inject
    private NewsletterMailingService service;
    
    @Inject
    private NewsletterTemplateService templateService;
    
    @Inject
    private NewsletterTemplateBlockService templateBlockService;
        
    @Inject
    private UserUiStateManagedBean uiState;
    
    private NewsletterMailingManagedBean mailingManagedBean;
    
    /////////////// L10N KEYS //////////////////////
    public static final String CREATE_SAVE_BUTTON_KEY = "newsletter.admin.mailing.createbutton";
    public static final String UPDATE_SAVE_BUTTON_KEY = "newsletter.admin.mailing.updatebutton";
    
    public static final String CREATE_TITLE_KEY = "newsletter.admin.mailing.createtitle";
    public static final String UPDATE_TITLE_KEY = "newsletter.admin.mailing.updatetitle";
    public static final String NO_BLOCKS_IN_TEMPLATE = "newsletter.admin.mailing.template.no.blocks";     
    /////////////// PROPERTIES ////////////////////
    
    private CRUDActionEnum currentAction;
    
    private Long categoryId;
    private Long articleId;
    private Long templateId;
    List<NewsletterTemplate> templates;
    private String mailingName;
    private Long mailingId;
    private String template = "";
    private String templateArticles = "";
    
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
                mailing.setGroupid(uiState.getGroupid());
                mailing.setCompanyid(uiState.getCompanyid());
                break;
            case UPDATE:
                mailing = service.findById(mailingId).getPayload();
                break;
        }
        NewsletterTemplate nlt = templateService.findById(templateId).getPayload();
        mailing.setTemplate(nlt);
        mailing.setName(mailingName);
        mailing.setList(findListById(categoryId));
        ServiceActionResult result = null;
        switch(currentAction) {
            case CREATE:
                log.error("CREATE");
                result = service.save(mailing);
                break;
            case UPDATE:
                 log.error("UPDATE");
                result = service.update(mailing);
                
                List <NewsletterTemplateBlock> ntbsOld =  templateBlockService.findAllByMailing(mailing);               
                
                for (NewsletterTemplateBlock ntbOld : ntbsOld) {
                    templateBlockService.delete(ntbOld);
                }                
                break;
        }
        if (result.isSuccess()) {            
                            
            //Update the TemplateBlocks       
            String[] articleIds;
            articleIds = templateArticles.split(",");
            for(int i =0; i < articleIds.length ; i++) {                
                NewsletterTemplateBlock ntb = new NewsletterTemplateBlock();                
                ntb.setGroupid(uiState.getGroupid());
                ntb.setCompanyid(uiState.getCompanyid());
                
                ntb.setArticleId(Long.valueOf(articleIds[i]));
                ntb.setBlockOrder(i);
                ntb.setMailing(mailing);
                templateBlockService.save(ntb);
            }  
            
            mailingManagedBean.init(); 
            return "admin";
        } else {
            FacesUtil.errorMessage("Failed to create mailing");
            List<String> validationKeys = result.getValidationKeys();
            for (String key : validationKeys) {
                FacesUtil.errorMessage(key);
                log.error("ERROR Failed to create mailing:" + key);
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

    public Long getTemplateId() {        
        return templateId;
    }

    public void setTemplateId(Long templateId) {        
        this.templateId = templateId;
    }

    public String getTemplateArticles() {
        return templateArticles;
    }

    public void setTemplateArticles(String templateArticles) {
        this.templateArticles = templateArticles;
    }
        
    public List<NewsletterTemplate> getTemplates() {
        setTemplates(templateService.findAll(uiState.getThemeDisplay()).getPayload());
        return templates;
    }

    public void setTemplates(List<NewsletterTemplate> templates) {
        this.templates = templates;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }    
    
    public void changeTemplate() {
        if (templateId != null && templateId != 0) {
            String templateContent = templateService.findById(templateId).getPayload().getTemplate();
            templateContent = parseTemplateEdit(templateContent);
            setTemplate(templateContent);
        } else {
            setTemplate("");
        }
    }
    
    private String parseTemplateEdit(String template) {
        String result = "";
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ResourceBundle newsletterMessageBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());            
            ThemeDisplay themedisplay = uiState.getThemeDisplay(); 
            result = EmailFormat.parseTemplateEdit(template, newsletterArticleType, newsletterArticleCategory, newsletterArticleTag, themedisplay);
            if (result.isEmpty()){            
                result = newsletterMessageBundle.getString(NO_BLOCKS_IN_TEMPLATE);
            }
        } catch (ClassNotFoundException ex) {
            log.error(ex);
        } catch (InstantiationException ex) {
            log.error(ex);
        } catch (IllegalAccessException ex) {
            log.error(ex);
        }
        return result;
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
        this.templateId = mailing.getTemplate().getId();
        changeTemplate();
        
        this.templateArticles = "";
        List <NewsletterTemplateBlock> ntbsOld =  templateBlockService.findAllByMailing(mailing); 
        Collections.sort(ntbsOld, new TemplateBlockComparator());
        for (NewsletterTemplateBlock ntbOld : ntbsOld) {
            if (this.templateArticles.isEmpty()) {
                this.templateArticles += ntbOld.getArticleId();
            } else {
                this.templateArticles += "," + ntbOld.getArticleId();
            }            
        }        
    }

    public void setMailingManagedBean(NewsletterMailingManagedBean mailingManagedBean) {
        this.mailingManagedBean = mailingManagedBean;
    }
    
}
