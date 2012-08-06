package com.rcs.newsletter.portlets.newsletteradmin;

import java.util.HashMap;
import java.util.Map;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class NewsletterAdminController {

    @RenderMapping
    public ModelAndView initialView(RenderRequest request, RenderResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/admin", model);
    }
    
    @ResourceMapping("subscribers")
    public ModelAndView subscribersTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/subscribers", model);
    }
    
    @ResourceMapping("mailing")
    public ModelAndView mailingTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/mailing", model);
    }
    
    @ResourceMapping("archive")
    public ModelAndView archiveTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/archive", model);
    }
    
    @ResourceMapping("templates")
    public ModelAndView templatesTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/templates", model);
    }
    
}
