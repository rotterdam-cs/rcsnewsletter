/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.portlets.newsletterregistrationconfirm;

import com.liferay.portal.util.PortalUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 *
 * @author marcoslacoste
 */
@Controller
@RequestMapping("VIEW")
public class NewsletterConfirmationController extends GenericController{
 
    private Logger logger = Logger.getLogger(NewsletterConfirmationController.class);
    
    @Autowired
    private NewsletterSubscriptionService subscriptionService;
    
    @RenderMapping
    public ModelAndView initialView(RenderRequest request, RenderResponse response){
        
        ModelAndView mav = new ModelAndView("confirmation/confirm");
        HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest( PortalUtil.getHttpServletRequest(request));
        
        
        // if the parameters are not received then skip logic
        if (originalRequest.getParameter("subscriptionId") == null){
            return mav;
        }
        
        Long subscriptionId = Long.valueOf(originalRequest.getParameter("subscriptionId"));
        
        // if it's an activation
        if (originalRequest.getParameter("activationkey") != null){
            String activationKey = originalRequest.getParameter("activationkey");
            ServiceActionResult activationResult = subscriptionService.activateSubscription(subscriptionId, activationKey, Utils.getThemeDisplay(request));
            mav.addObject("result", activationResult);
            
        }
        
        // if it's an deactivation
        if (originalRequest.getParameter("deactivationkey") != null){
            String deactivationKey = originalRequest.getParameter("deactivationkey");
            ServiceActionResult deactivationResult = subscriptionService.deactivateSubscription(subscriptionId, deactivationKey, Utils.getThemeDisplay(request));
            mav.addObject("result", deactivationResult);
        }
        
        return mav;
        
    }
    
    
}
