/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.portlets.newsletterregistration;

import com.liferay.portal.util.PortalUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.NewsletterCategoryDTO;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.forms.RegistrationSettingsForm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.log4j.Logger;
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
@RequestMapping("EDIT")
public class NewsletterRegistrationEditController extends GenericController {

    public static final String PORTLET_PROPERTY_NEWSLETTER_LIST = "newsletterList";
    public static final String PORTLET_PROPERTY_DISABLED_NAME_FIELDS = "disabledNameFields";
    private Logger logger = Logger.getLogger(NewsletterRegistrationConfigurationAction.class);
    @Autowired
    private NewsletterCategoryService categoryService;

    @RenderMapping
    public ModelAndView registerEdit(RenderRequest request, RenderResponse response) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        response.setTitle(bundle.getString("newsletter.registration.portlet.title"));

        Map<String, Object> model = new HashMap<String, Object>();

        // pass namespace to jsp
        String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));
        model.put("namespace", namespace);       // portlet namespace

        // obtain portlet preferences
        Long listId = Long.valueOf(request.getPreferences().getValue(PORTLET_PROPERTY_NEWSLETTER_LIST, "0"));
        boolean disabledNameFields = Boolean.valueOf(request.getPreferences().getValue(PORTLET_PROPERTY_DISABLED_NAME_FIELDS, "false"));
        
        // create model form
        RegistrationSettingsForm settings = new RegistrationSettingsForm();
        settings.setListId(listId);
        settings.setDisabledNameFields(disabledNameFields);
        model.put("settings", settings);

        // get all lists and select one by default if there is no other one selected already
        List<NewsletterCategoryDTO> lists = categoryService.findAllNewsletterCategories(Utils.getThemeDisplay(request));
        if (lists.size() > 0 && listId.equals(0)) {
            settings.setListId(lists.get(0).getId());
            try {
                request.getPreferences().setValue(PORTLET_PROPERTY_NEWSLETTER_LIST, settings.getListId().toString());
                request.getPreferences().store();
            } catch (Exception e) {
                logger.error("Portlet preferences could not be updated. Exception: " + e.getMessage(), e);
            }
        }
        model.put("listOptions", lists);



        return new ModelAndView("registration/registerEdit", model);
    }

    /**
     * Save registration portlet settings
     *
     * @param request
     * @param response
     * @return
     */
    @ResourceMapping("saveSettings")
    public ModelAndView mailingList(ResourceRequest request, ResourceResponse response, @ModelAttribute RegistrationSettingsForm settings) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, request.getLocale());
        try {
            
            request.getPreferences().setValue(PORTLET_PROPERTY_NEWSLETTER_LIST, settings.getListId().toString());
            request.getPreferences().setValue(PORTLET_PROPERTY_DISABLED_NAME_FIELDS, String.valueOf(settings.isDisabledNameFields()));
            request.getPreferences().store();
            return jsonResponse(ServiceActionResult.buildSuccess(null, bundle.getString("newsletter.registration.settings.message.settingssaved")));
        } catch (Exception e) {
            logger.error("Portlet preferences could not be updated. Exception: " + e.getMessage(), e);
        }
        return jsonResponse(ServiceActionResult.buildFailure(null, bundle.getString("newsletter.registration.settings.error.settingsnotsaved")));
    }
}
