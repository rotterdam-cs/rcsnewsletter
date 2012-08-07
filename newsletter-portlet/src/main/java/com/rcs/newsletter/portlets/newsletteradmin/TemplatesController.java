package com.rcs.newsletter.portlets.newsletteradmin;

import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.JacksonJsonView;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.model.NewsletterTemplate;
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
        ServiceActionResult<ListResultsDTO<NewsletterTemplate>> result = templateService.findAllTemplates(
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
        
        
        NewsletterTemplate sample = new NewsletterTemplate();
        sample.setId(1L);
        sample.setName("Sample Template 1");
        result.getPayload().getResult().add(sample);
        
        
        return new ModelAndView (new JacksonJsonView(), JacksonJsonView.MODEL_NAME, result);
    }
}
