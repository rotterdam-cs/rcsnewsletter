package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.portlets.forms.NewsletterForm;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.util.JsonEntityBuilder;
import com.rcs.newsletter.util.JsonBuilder;
import com.rcs.newsletter.util.JsonResponseUtil;
import com.rcs.newsletter.util.MailingHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class NewsletterAdminController {

    @Autowired
    private NewsletterCategoryService categoryService;

    @RenderMapping
    public ModelAndView firstPage(RenderRequest request, RenderResponse response) {
        Map<String, List<NewsletterCategory>> model = new HashMap<String, List<NewsletterCategory>>();

        List<NewsletterCategory> newsletterCategorys = categoryService.findAllNewsletterCategorys(false);

        model.put("categories", newsletterCategorys);

        return new ModelAndView("/admin/admin", model);
    }
    
    @ActionMapping(params = "action=saveNewsletterAction")
    public ModelAndView saveNewsletterAction(@Valid NewsletterCategory newsletterCategory, BindingResult result, ActionRequest request, ActionResponse response) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());
        JsonBuilder jsonBuilder = new JsonBuilder();
        boolean success = false;
        String message = "";
        
        if (!result.hasErrors()) {
            success = true;
            message = resourceBundle.getString("newsletter.admin.category.save.success");
            categoryService.addNewsletterCategory(newsletterCategory);
        } else {
            message = resourceBundle.getString("newsletter.admin.category.save.failure");
        }
        
        jsonBuilder.setSuccess(success);
        jsonBuilder.setMessage(message);
        
        JsonResponseUtil.write(response, jsonBuilder);

        return null;
    }

    @ActionMapping(params = "action=updateNewsletterInformationAction")
    public ModelAndView updateNewsletterInformationAction(@Valid NewsletterCategory newsletterCategory, BindingResult result, ActionRequest request, ActionResponse response) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());
        JsonBuilder jsonBuilder = new JsonBuilder();
        boolean success = false;
        String message = "";
        
        if (!result.hasErrors()) {
            success = true;
            message = resourceBundle.getString("newsletter.admin.category.update.success");
            categoryService.updateNewsletterCategory(newsletterCategory);
        } else {
            message = resourceBundle.getString("newsletter.admin.category.update.failure");
        }
        
        jsonBuilder.setSuccess(success);
        jsonBuilder.setMessage(message);

        JsonResponseUtil.write(response, jsonBuilder);
        
        return null;
    }

    @ActionMapping(params = "action=sendNewsletterAction")
    public ModelAndView sendNewsletterAction(NewsletterForm form, ActionRequest request, ActionResponse response) {
        JsonBuilder jsonResponse = new JsonBuilder();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());

        boolean success = false;
        String message;
        if (form.getCategoryId() != -1) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

            success = true;
            message = resourceBundle.getString("newsletter.admin.send.success");
            
            NewsletterCategory newsletterCategory = categoryService.findById(form.getCategoryId(), true);

            for (NewsletterSubscription newsletterSubscription : newsletterCategory.getSubscriptions()) {
                
                if (newsletterSubscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                    String toEmail = newsletterSubscription.getSubscriptor().getEmail();
                    long articleId = newsletterCategory.getArticleId();
                    
                    MailingHelper.sendEmail(toEmail, NEWSLETTER_ADMIN, articleId, themeDisplay);
                }
            }

        } else {            
            message = resourceBundle.getString("newsletter.admin.send.failure");
        }

        jsonResponse.setSuccess(success);
        jsonResponse.setMessage(message);

        JsonResponseUtil.write(response, jsonResponse);

        return null;
    }

    @ActionMapping(params = "action=getNewsletterCategoryByIdAction")
    public ModelAndView getNewsletterCategoryByIdAction(@Valid NewsletterCategory newsletterCategory, BindingResult result, ActionRequest request, ActionResponse response) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());
        newsletterCategory = categoryService.findById(newsletterCategory.getId(), true);

        JsonEntityBuilder jsonResponse = new JsonEntityBuilder();
        boolean success = false;
        String message = "";

        if (newsletterCategory != null) {
            success = true;
            jsonResponse.setNewsletterEntity(newsletterCategory);
        } else {
            message = resourceBundle.getString("newsletter.admin.category.notexists");
        }

        jsonResponse.setSuccess(success);
        jsonResponse.setMessage(message);

        JsonResponseUtil.write(response, jsonResponse);

        return null;
    }
    
    @ActionMapping(params = "action=deleteNewsletterAction")
    public ModelAndView deleteNewsletterAction(@Valid NewsletterCategory newsletterCategory, BindingResult result, ActionRequest request, ActionResponse response) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());
        JsonBuilder jsonBuilder = new JsonBuilder();
        boolean success = false;
        String message = "";
        
        if (!result.hasErrors()) {
            success = true;
            message = resourceBundle.getString("newsletter.admin.category.delete.success");
            categoryService.deleteNewsletterCategory(newsletterCategory);
        } else {
            message = resourceBundle.getString("newsletter.admin.category.delete.failure");
        }
        
        jsonBuilder.setSuccess(success);
        jsonBuilder.setMessage(message);

        JsonResponseUtil.write(response, jsonBuilder);
        
        return null;
    }    
}
