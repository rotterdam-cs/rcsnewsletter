package com.rcs.newsletter.portlets.newsletteradmin;

import com.liferay.portal.kernel.util.HtmlUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.NewsletterTemplateDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.HashMap;
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
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class TemplatesController extends GenericController {
    
    private Logger logger = Logger.getLogger(TemplatesController.class);
    
    @Autowired
    private NewsletterTemplateService templateService;
    
    
    
    
    /**
     * Returns the main content for Templates Tab
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("templates")
    public ModelAndView templatesTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/templates", model);
    }

    
    
    /**
     * Show view with templates listing
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("templatesList")
    public ModelAndView templatesList(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/templatesList", model);
    }
    
        
    
    
    /**
     * Returns JSON data for templates data grid
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping(value="getTemplatesList")
    public ModelAndView templatesList(ResourceRequest request, ResourceResponse response, @ModelAttribute GridForm form){
        // get records using paging
        ServiceActionResult<ListResultsDTO<NewsletterTemplateDTO>> result = templateService.findAllTemplates(
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
     * Edit a new or existing template
     * @param request
     * @param response
     * @param id
     * @return 
     */
    @ResourceMapping(value="editTemplate")
    public ModelAndView editTemplate(ResourceRequest request, ResourceResponse response, Long id){
        ModelAndView mav = new ModelAndView("admin/templatesEdit");
        
        boolean showToRemove = request.getParameter("remove") != null;
        
        NewsletterTemplateDTO template = new NewsletterTemplateDTO();
        
        
        // if editing a particular template, then retrieve it from DB
        if (id != null){
            ServiceActionResult<NewsletterTemplateDTO> findTemplate = templateService.findTemplate(id);
            template = findTemplate.getPayload();
        }
        if (template.getTemplate() != null && !showToRemove){
            template.setTemplate(HtmlUtil.escapeJS(template.getTemplate()));
        }else if (template.getTemplate() == null){
            template.setTemplate("");
        }
        
        mav.addObject("template", template);
        mav.addObject("remove", showToRemove);
        
        
        return mav;
    }
    
    /**
     * Saves the template
     * @param request
     * @param response
     * @param templateDTO
     * @return 
     */
    @ResourceMapping(value="saveTemplate")
    public ModelAndView saveTemplate(ResourceRequest request, ResourceResponse response, @ModelAttribute NewsletterTemplateDTO templateDTO){
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        
        ServiceActionResult<NewsletterTemplateDTO> result = templateService.saveTemplate(Utils.getThemeDisplay(request), templateDTO);
        if (result.isSuccess()){
            result.addMessage(bundle.getString("newsletter.tab.templates.message.saved"));
        }else{
            result.addValidationKey(bundle.getString("newsletter.tab.templates.error.saving"));
        }
        return jsonResponse(result);
    }
    
     
    /**
     * Deletes the template
     * @param request
     * @param response
     * @param templateDTO
     * @return 
     */
    @ResourceMapping(value="deleteTemplate")
    public ModelAndView deleteTemplate(ResourceRequest request, ResourceResponse response, @RequestParam Long id){
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        
        ServiceActionResult result = templateService.deleteTemplate(Utils.getThemeDisplay(request), id);
        if (result.isSuccess()){
            result.addMessage(bundle.getString("newsletter.tab.templates.message.deleted"));
        }
        return jsonResponse(result);
    }
    
}
