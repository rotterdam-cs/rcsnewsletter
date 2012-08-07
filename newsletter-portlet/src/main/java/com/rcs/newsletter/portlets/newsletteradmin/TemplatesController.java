package com.rcs.newsletter.portlets.newsletteradmin;

import java.util.HashMap;
import java.util.Map;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class TemplatesController {
    
    private Logger logger = Logger.getLogger(TemplatesController.class);
    
    @ResourceMapping("templates")
    public ModelAndView templatesTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/templates", model);
    }
    
    
    @ResourceMapping(value="getTemplatesList")
    public String templatesList(ResourceRequest request, ResourceResponse response){
        logger.info("invoking templatesList() in TemplatesController");
        return "";
    }
}
