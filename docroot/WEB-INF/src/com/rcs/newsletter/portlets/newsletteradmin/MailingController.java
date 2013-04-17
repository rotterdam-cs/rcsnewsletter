package com.rcs.newsletter.portlets.newsletteradmin;

import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.rcs.newsletter.commons.ResourceBundleHelper;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.JournalArticleDTO;
import com.rcs.newsletter.core.dto.NewsletterMailingDTO;
import com.rcs.newsletter.core.dto.NewsletterTemplateDTO;
import com.rcs.newsletter.core.dto.NewsletterCategoryDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.core.service.util.EmailFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author Marcos
 */
@Controller
@RequestMapping("VIEW")
public class MailingController extends GenericController {
    
    @Autowired
    private NewsletterMailingService mailingService;
    @Autowired
    private NewsletterCategoryService categoryService;
    @Autowired
    private NewsletterTemplateService templateService;

    
    private Log logger = LogFactoryUtil.getLog(MailingController.class);
    
    @ResourceMapping("mailing")
    public ModelAndView mailingTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/mailing", model);
    }
    
    
     /**
     * Show view with mailing listing
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("mailingList")
    public ModelAndView mailingList(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/mailingList", model);
    }
    
        
    
    
    /**
     * Returns JSON data for mailing data grid
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping(value="getMailingList")
    public ModelAndView mailingList(ResourceRequest request, ResourceResponse response, @ModelAttribute GridForm form){
        
        // get records using paging
        form.addFieldAlias("listName", "list.name");
        ServiceActionResult<ListResultsDTO<NewsletterMailingDTO>> result = mailingService.findAllMailings(
                                    Utils.getThemeDisplay(request), 
                                    form, 
                                    "name", 
                                    ORDER_BY_ASC);

        // if an error occurrs then return no record
        if (result.isSuccess()){
            result.getPayload().setCurrentPage(form.getPage());
        }else{
            result.getPayload().setCurrentPage(0);
            result.getPayload().setResult(new ArrayList());
            result.getPayload().setTotalRecords(0);
        }
        
        
        return jsonResponse(result);
    }
    
    
     /**
     * Edit a new or existing mailing
     * @param request
     * @param response
     * @param id
     * @return 
     */
    @ResourceMapping(value="editMailing")
    public ModelAndView editTemplate(ResourceRequest request, ResourceResponse response, Long id){
        ModelAndView mav = new ModelAndView("admin/mailingEdit");        
        boolean showToRemove = request.getParameter("remove") != null;
        
        NewsletterMailingDTO mailing = new NewsletterMailingDTO();
        
        
        // if editing a particular mailing, then retrieve it from DB
        if (id != null){
            ServiceActionResult<NewsletterMailingDTO> findMailingResult = mailingService.findMailing(id, Utils.getThemeDisplay(request));
            mailing = findMailingResult.getPayload();
        }
        
        
        // get all lists
        List<NewsletterCategoryDTO> lists = categoryService.findAllNewsletterCategories(Utils.getThemeDisplay(request));
        NewsletterCategoryDTO emptyCategory = new NewsletterCategoryDTO();
        lists.add(0,emptyCategory);
        
        
        // get all templates
        List<NewsletterTemplateDTO> templates = templateService.findAllTemplates(Utils.getThemeDisplay(request));
        NewsletterTemplateDTO emptyTemplate = new NewsletterTemplateDTO();
        templates.add(0,emptyTemplate);
        
        // get all articles
        List<JournalArticleDTO> articles = mailingService.findAllArticlesForMailing(Utils.getThemeDisplay(request));
        
        String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));
        
        mav.addObject("namespace", namespace);       // portlet namespace
        mav.addObject("mailing", mailing);           // main model
        mav.addObject("listOptions", lists);         // for lists combo
        mav.addObject("templateOptions", templates); // for templates combo
        mav.addObject("articlesOptions", articles); // for templates combo
        mav.addObject("remove", showToRemove);       // view to remove/edit
        
        return mav;
    }
    
    
    
    @ResourceMapping(value = "getPreview")
    public void getPreview(
		 Long templateId
		,ResourceRequest request
		,ResourceResponse response
    ) throws Exception {		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		Locale locale = LocaleUtil.fromLanguageId(LanguageUtil.getLanguageId(request));
		logger.info(templateId);
		if (templateId != null) {
			String templateContent = templateService.findById(templateId).getPayload().getTemplate(); 
			String content = parseTemplateEdit(templateContent, themeDisplay, locale);
			
			HttpServletResponse servletResponse = ((LiferayPortletResponse) response).getHttpServletResponse();
			servletResponse.setContentType("text/plain");
			ServletResponseUtil.write(servletResponse, content);
		}
    }
    
    private String parseTemplateEdit(String template, ThemeDisplay themeDisplay, Locale locale ) {
    	logger.debug("Executing parseTemplateEdit() in EditMailingManagedBean");
        String result = "";
        try {            
            result = EmailFormat.parseTemplateEdit(template, "rcs-newsletter", "newsletter", "newsletter", themeDisplay);
            if (result.isEmpty()) {
            	result = ResourceBundleHelper.getKeyLocalizedValue("newsletter.admin.mailing.template.no.blocks", locale);
            }
        } catch (ClassNotFoundException ex) {
        	logger.error(ex);
        } catch (InstantiationException ex) {
        	logger.error(ex);
        } catch (IllegalAccessException ex) {
        	logger.error(ex);
        }
        return result;
    }
    
    
    /**
     * Saves the mailing
     * @param request
     * @param response
     * @param mailingDTO
     * @return 
     */
    @ResourceMapping(value="saveMailing")
    public ModelAndView saveMailing(ResourceRequest request, ResourceResponse response, @ModelAttribute NewsletterMailingDTO mailingDTO){
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        
        ServiceActionResult<NewsletterMailingDTO> result = mailingService.saveMailing(Utils.getThemeDisplay(request), mailingDTO);
        if (result.isSuccess()){
            result.addMessage(bundle.getString("newsletter.tab.mailing.message.saved"));
        }else{
            result.addValidationKey(bundle.getString("newsletter.tab.mailing.error.saving"));
        }
        return jsonResponse(result);
    }
    
     
    /**
     * Deletes the mailing
     * @param request
     * @param response
     * @param mailing id
     * @return 
     */
    @ResourceMapping(value="deleteMailing")
    public ModelAndView deleteMailing(ResourceRequest request, ResourceResponse response, @RequestParam Long id){
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        
        ServiceActionResult result = mailingService.deleteMailing(Utils.getThemeDisplay(request), id);
        if (result.isSuccess()){
            result.addMessage(bundle.getString("newsletter.tab.mailing.message.deleted"));
        }else{
            result.addValidationKey(bundle.getString("newsletter.tab.mailing.error.deleting"));
        }
        return jsonResponse(result);
    }
    
    
    /**
     * Send test emails according to a email address and a mailing list
     * @param request
     * @param response
     * @param mailingId
     * @param emailAddress
     * @return 
     */
    @ResourceMapping(value="testEmail")
    public ModelAndView sendTestEmail(ResourceRequest request, ResourceResponse response, @RequestParam Long mailingId, @RequestParam String emailAddress){
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        try{
            mailingService.sendTestMailing(mailingId, emailAddress, Utils.getThemeDisplay(request));
            return jsonResponse(ServiceActionResult.buildSuccess(null, bundle.getString("newsletter.tab.mailing.message.testemailsent")));
        }catch(Exception e){
            logger.error("Error while trying to send test email. Exception: " + e.getMessage(), e);
            return jsonResponse(ServiceActionResult.buildFailure(null, bundle.getString("newsletter.tab.mailing.error.sendingtestemail")));
        }
       
    }
    
    
    
    @ResourceMapping(value="sendNewsletter")
    public ModelAndView sendNewsletter(ResourceRequest request, ResourceResponse response, @RequestParam Long mailingId){
        logger.info("Sending newsletter for mailing: " + mailingId);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        try{
            mailingService.sendNewsletter(mailingId, Utils.getThemeDisplay(request));
            return jsonResponse(ServiceActionResult.buildSuccess(null, bundle.getString("newsletter.tab.mailing.message.newslettersent")));
        }catch(Exception e){
            logger.error("Error while trying to send test email. Exception: " + e.getMessage(), e);
            return jsonResponse(ServiceActionResult.buildFailure(null, bundle.getString("newsletter.tab.mailing.error.sendingnewsletter")));
        }
    }
    
    
}
