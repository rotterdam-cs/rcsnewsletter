/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.portlets.newsletterregistration;

import com.liferay.portal.util.PortalUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.portlets.forms.SubscriptionForm;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author marcoslacoste
 */
@Controller
@RequestMapping("VIEW")
public class NewsletterRegistrationController extends GenericController{
 
    @RenderMapping
    public ModelAndView initialView(RenderRequest request, RenderResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("registration/register", model);
    }
    
    
     /**
     * Show registration form view
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("showRegisterForm")
    public ModelAndView showRegisterForm(ResourceRequest request, ResourceResponse response, @ModelAttribute SubscriptionForm registerForm){
       String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));

        ModelAndView mav = new ModelAndView("registration/registerForm");
        mav.addObject("namespace", namespace);       // portlet namespace
        mav.addObject("registerForm", registerForm); // register form

        return mav;
    }
    
    /**
     * Show unregister form view
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("showUnregisterForm")
    public ModelAndView showUnregisterForm(ResourceRequest request, ResourceResponse response){
       String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));

        ModelAndView mav = new ModelAndView("registration/unregisterForm");
        mav.addObject("namespace", namespace);       // portlet namespace

        return mav;
    }
}
