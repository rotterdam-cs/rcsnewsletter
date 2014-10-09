/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.portlets.newsletterregistration;

import com.liferay.portal.util.PortalUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.portlets.forms.SubscriptionForm;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
 
    @Autowired
    private NewsletterSubscriptionService subscriptionService;
    
    @RenderMapping
    public ModelAndView initialView(RenderRequest request, RenderResponse response){
    	ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        response.setTitle(bundle.getString("newsletter.registration.portlet.title"));
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
        
        Long listId = Long.valueOf(request.getPreferences().getValue(NewsletterRegistrationEditController.PORTLET_PROPERTY_NEWSLETTER_LIST, "0"));
        registerForm.setCategoryId(listId);
        
        boolean showNameFields = !Boolean.valueOf(request.getPreferences().getValue(NewsletterRegistrationEditController.PORTLET_PROPERTY_DISABLED_NAME_FIELDS, "false"));
        mav.addObject("showNameFields", showNameFields); // show first name/last name fields flag

        

        return mav;
    }
    
    /**
     * Show unregister form view
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("showUnregisterForm")
    public ModelAndView showUnregisterForm(ResourceRequest request, ResourceResponse response, @ModelAttribute SubscriptionForm registerForm){
       String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));

        ModelAndView mav = new ModelAndView("registration/unregisterForm");
        mav.addObject("namespace", namespace);       // portlet namespace
        mav.addObject("registerForm", registerForm); // register form
        
        Long listId = Long.valueOf(request.getPreferences().getValue(NewsletterRegistrationEditController.PORTLET_PROPERTY_NEWSLETTER_LIST, "0"));
        registerForm.setCategoryId(listId);

        return mav;
    }
    
    
    /**
     * Register a new user to the selected list
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("register")
    public ModelAndView register(ResourceRequest request, ResourceResponse response, @ModelAttribute SubscriptionForm registerForm){

       NewsletterSubscriptionDTO subscriptionDTO = new NewsletterSubscriptionDTO();
       subscriptionDTO.setCategoryId(String.valueOf(registerForm.getCategoryId()));
       subscriptionDTO.setSubscriptorEmail(registerForm.getEmail());
       subscriptionDTO.setSubscriptorFirstName(registerForm.getFirstName());
       subscriptionDTO.setSubscriptorLastName(registerForm.getLastName());
       
       return jsonResponse(subscriptionService.createSubscription(subscriptionDTO, Utils.getThemeDisplay(request)));
       
    }
    
    
    /**
     * Unregister a new user from the selected list
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("unregister")
    public ModelAndView unregister(ResourceRequest request, ResourceResponse response, @ModelAttribute SubscriptionForm registerForm){

       NewsletterSubscriptionDTO subscriptionDTO = new NewsletterSubscriptionDTO();
       subscriptionDTO.setCategoryId(String.valueOf(registerForm.getCategoryId()));
       subscriptionDTO.setSubscriptorEmail(registerForm.getEmail());
       
       return jsonResponse(subscriptionService.removeSubscription(subscriptionDTO, Utils.getThemeDisplay(request)));
       
    }
}
