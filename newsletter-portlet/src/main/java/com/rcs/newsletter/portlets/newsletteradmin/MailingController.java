package com.rcs.newsletter.portlets.newsletteradmin;

import com.liferay.portal.util.PortalUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.ArticleDTO;
import com.rcs.newsletter.core.dto.MailingDTO;
import com.rcs.newsletter.core.dto.TemplateDTO;
import com.rcs.newsletter.core.model.dtos.NewsletterCategoryDTO;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.forms.GridForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
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
    
    private Logger logger = Logger.getLogger(MailingController.class);
    
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
        ServiceActionResult<ListResultsDTO<MailingDTO>> result = mailingService.findAllMailings(
                                    Utils.getThemeDisplay(request), 
                                    form.calculateStart(), 
                                    form.getRows(), 
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
        
        MailingDTO mailing = new MailingDTO();
        
        
        // if editing a particular mailing, then retrieve it from DB
        if (id != null){
            ServiceActionResult<MailingDTO> findMailingResult = mailingService.findMailing(id, Utils.getThemeDisplay(request));
            mailing = findMailingResult.getPayload();
        }
        
        
        // get all lists
        List<NewsletterCategoryDTO> lists = categoryService.findAllNewsletterCategories(Utils.getThemeDisplay(request));
        NewsletterCategoryDTO emptyCategory = new NewsletterCategoryDTO();
        lists.add(0,emptyCategory);
        
        
        // get all templates
        List<TemplateDTO> templates = templateService.findAllTemplates(Utils.getThemeDisplay(request));
        TemplateDTO emptyTemplate = new TemplateDTO();
        templates.add(0,emptyTemplate);
        
        // get all articles
        List<ArticleDTO> articles = mailingService.findAllArticlesForMailing(Utils.getThemeDisplay(request));
        
        String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));
        
        mav.addObject("namespace", namespace);       // portlet namespace
        mav.addObject("mailing", mailing);           // main model
        mav.addObject("listOptions", lists);         // for lists combo
        mav.addObject("templateOptions", templates); // for templates combo
        mav.addObject("articlesOptions", articles); // for templates combo
        mav.addObject("remove", showToRemove);       // view to remove/edit
        
        
        
        return mav;
    }
    
    
    /**
     * Saves the mailing
     * @param request
     * @param response
     * @param mailingDTO
     * @return 
     */
    @ResourceMapping(value="saveMailing")
    public ModelAndView saveMailing(ResourceRequest request, ResourceResponse response, @ModelAttribute MailingDTO mailingDTO){
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        
        ServiceActionResult<MailingDTO> result = mailingService.saveMailing(Utils.getThemeDisplay(request), mailingDTO);
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
}
