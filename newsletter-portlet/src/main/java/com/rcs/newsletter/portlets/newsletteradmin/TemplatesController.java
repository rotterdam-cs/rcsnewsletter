package com.rcs.newsletter.portlets.newsletteradmin;

import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.JacksonJsonView;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.TemplateDTO;
import com.rcs.newsletter.core.service.NewsletterTemplateService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.forms.GridForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
        logger.info("--------------------------------------------------------------------");
        logger.info("getting templates list");
        logger.info("--------------------------------------------------------------------");
        
        // get records using paging
        ServiceActionResult<ListResultsDTO<TemplateDTO>> result = templateService.findAllTemplates(
                                    Utils.getThemeDisplay(request), 
                                    form.calculateStart(), 
                                    form.getRows(), 
                                    "name", 
                                    ORDER_BY_ASC);

        // if an error occurrs then return no record
        if (!result.isSuccess()){
            result.getPayload().setCurrentPage(0);
            result.getPayload().setResult(new ArrayList());
            result.getPayload().setTotalRecords(0);
        }
        
        
        TemplateDTO sample = new TemplateDTO();
        sample.setId(1L);
        sample.setName("Sample Template 1");
        result.getPayload().getResult().add(sample);
        
        
        return new ModelAndView (new JacksonJsonView(), JacksonJsonView.MODEL_NAME, result);
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
        Map<String,Object> model = new HashMap<String,Object>();
        ModelAndView mav = new ModelAndView("admin/templatesEdit", model);
        
        TemplateDTO entity = new TemplateDTO();
        
        // if editing a particular template, then retrieve it from DB
        if (id != null){
            ServiceActionResult<TemplateDTO> findTemplate = templateService.findTemplate(id);
            
            // show errors if they occur
            if (!findTemplate.isSuccess()){
                model.put(MODEL_ERRORS, new String[]{"newsletter.tab.templates.error.editing"});
                return mav;
            }
            
            
            entity = findTemplate.getPayload();
        }
        model.put("template", entity);
        
        
        return mav;
    }
    
    
}
