package com.rcs.newsletter.portlets.subscription;

import com.liferay.portal.kernel.util.Validator;
import com.rcs.newsletter.portlets.forms.SubscriptionForm;
import java.util.List;
import com.rcs.newsletter.core.dto.SubscriptionDTO;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.util.JsonDTOBuilder;
import com.rcs.newsletter.util.JsonResponseUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class ManageSubscriptionController {

    @Autowired
    private NewsletterSubscriptionService subscriptionService;
    @Autowired
    private NewsletterCategoryService categoryService;
    @Autowired
    private NewsletterSubscriptorService subscriptorService;
    @Autowired
    private NewsletterSubscriptionExpert expert;

    @RenderMapping
    public ModelAndView firstPage(RenderRequest request, RenderResponse response) {

        List<NewsletterCategory> newsletterCategorys = categoryService.findAllNewsletterCategorys(false);
        Map<String, List<NewsletterCategory>> model = new HashMap<String, List<NewsletterCategory>>();

        model.put("categories", newsletterCategorys);

        return new ModelAndView("/subscription/subscription", model);
    }

    @ActionMapping(params = "action=unregisterNewsletterSubscription")
    public ModelAndView unregisterNewsletterSubscription(SubscriptionForm subscriptionForm, ActionRequest request, ActionResponse response) {
        JsonDTOBuilder jsonBuilder = new JsonDTOBuilder();
        boolean success = false;
        String message = "";

        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());

        if (Validator.isEmailAddress(subscriptionForm.getEmail())) {
            long categoryId = subscriptionForm.getCategoryId();
            String email = subscriptionForm.getEmail();

            NewsletterCategory newsletterCategory = categoryService.findById(categoryId, false);
            NewsletterSubscriptor newsletterSubscriptor = subscriptorService.findByEmail(email);

            NewsletterSubscription newsletterSubscription = subscriptionService.findBySubscriptorAndCategory(newsletterSubscriptor, newsletterCategory);

            if (newsletterSubscription != null) {

                if (newsletterSubscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                    String cancellationKey = expert.getUniqueKey(email);
                    newsletterSubscription.setCancellationKey(cancellationKey);

                    subscriptionService.updateNewsletterSubscription(newsletterSubscription);

                    SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
                    subscriptionDTO.setCategoryId(newsletterCategory.getId());
                    subscriptionDTO.setCategoryName(newsletterCategory.getName());
                    subscriptionDTO.setEmail(email);

                    jsonBuilder.setData(subscriptionDTO);
                    success = true;

                    expert.sendNotificationEmail(email, cancellationKey,
                            newsletterCategory.getName(), false, request.getLocale());
                } else {
                    message = resourceBundle.getString("newsletter.unsubscription.user.notactive");
                }
            } else {
                message = resourceBundle.getString("newsletter.unsubscription.user.notexists");
            }
        } else {
            message = resourceBundle.getString("newsletter.subscription.data.error");
        }

        jsonBuilder.setSuccess(success);
        jsonBuilder.setMessage(message);

        JsonResponseUtil.write(response, jsonBuilder);

        return null;
    }

    @ResourceMapping(value = "unregisterConfirmationPageAction")
    public ModelAndView unregisterConfirmationPageAction(String email, String categoryId, ResourceRequest request, ResourceResponse response) {

        Map<String, String> model = new HashMap<String, String>();

        model.put("email", email);
        model.put("categoryId", categoryId);

        return new ModelAndView("/subscription/unsubscriptionConfirmation", model);
    }

    @ActionMapping(params = "action=confirmNewsletterUnsubscription")
    public ModelAndView confirmNewsletterUnsubscription(@Valid SubscriptionForm subscriptionForm, BindingResult result, ActionRequest request, ActionResponse response) {
        JsonDTOBuilder jsonBuilder = new JsonDTOBuilder();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());

        boolean success = false;
        String message = "";

        NewsletterCategory newsletterCategory = categoryService.findById(subscriptionForm.getCategoryId(), false);
        NewsletterSubscriptor newsletterSubscriptor = subscriptorService.findByEmail(subscriptionForm.getEmail());

        NewsletterSubscription oldNewsletterSubscription = subscriptionService.findBySubscriptorAndCategory(newsletterSubscriptor, newsletterCategory);
        String newCancellationKey = subscriptionForm.getCancellationKey();

        if (oldNewsletterSubscription != null
                && oldNewsletterSubscription.getCancellationKey().equals(newCancellationKey)) {

            success = true;
            oldNewsletterSubscription.setStatus(SubscriptionStatus.INACTIVE);
            subscriptionService.updateNewsletterSubscription(oldNewsletterSubscription);

        } else {
            message = resourceBundle.getString("newsletter.unsubscription.confirmation.key.fail");
        }

        jsonBuilder.setSuccess(success);
        jsonBuilder.setMessage(message);

        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setCategoryName(newsletterCategory.getName());
        subscriptionDTO.setEmail(subscriptionForm.getEmail());

        jsonBuilder.setData(subscriptionDTO);

        JsonResponseUtil.write(response, jsonBuilder);

        return null;
    }

    @ActionMapping(params = "action=registerNewsletterSubscription")
    public ModelAndView registerNewsletterSubscription(SubscriptionForm subscriptionForm, ActionRequest request, ActionResponse response) {
        JsonDTOBuilder jsonBuilder = new JsonDTOBuilder();
        boolean success = false;
        String message = "";

        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());

        if (Validator.isEmailAddress(subscriptionForm.getEmail())) {
            long categoryId = subscriptionForm.getCategoryId();
            String email = subscriptionForm.getEmail();
            String confirmationKey = expert.getUniqueKey(email);

            NewsletterCategory newsletterCategory = categoryService.findById(categoryId, false);
            if (newsletterCategory != null) {
                boolean sendNotification = false;
                boolean subscriptorExists = true;
                NewsletterSubscription newsletterSubscription = null;
                NewsletterSubscriptor newsletterSubscriptor = subscriptorService.findByEmail(subscriptionForm.getEmail());

                //If the subscriptor does not exists we should create it
                if (newsletterSubscriptor == null) {
                    newsletterSubscriptor = new NewsletterSubscriptor();
                    newsletterSubscriptor.setFirstName(subscriptionForm.getFirstName());
                    newsletterSubscriptor.setLastName(subscriptionForm.getLastName());
                    newsletterSubscriptor.setEmail(email);

                    subscriptorExists = subscriptorService.addNewsletterSubscriptor(newsletterSubscriptor);
                } else {
                    //If the subscriptor exists we have to check if was already
                    //registered to the current newsletter
                    newsletterSubscription = subscriptionService.findBySubscriptorAndCategory(newsletterSubscriptor, newsletterCategory);
                }

                if (subscriptorExists) {
                    if (newsletterSubscription == null) {
                        newsletterSubscription = new NewsletterSubscription();
                        newsletterSubscription.setConfirmationKey(confirmationKey);
                        newsletterSubscription.setCategory(newsletterCategory);
                        newsletterSubscription.setSubscriptor(newsletterSubscriptor);
                        newsletterSubscription.setStatus(SubscriptionStatus.INVITED);

                        sendNotification = subscriptionService.addNewsletterSubscription(newsletterSubscription);
                    } else {
                        if (newsletterSubscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                            message = resourceBundle.getString("newsletter.subscription.already.activated");
                        } else {
                            newsletterSubscription.setConfirmationKey(confirmationKey);
                            newsletterSubscription.setStatus(SubscriptionStatus.INVITED);
                            sendNotification = subscriptionService.updateNewsletterSubscription(newsletterSubscription);
                        }
                    }

                    if (sendNotification) {
                        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
                        subscriptionDTO.setEmail(email);
                        subscriptionDTO.setCategoryId(newsletterCategory.getId());
                        subscriptionDTO.setCategoryName(newsletterCategory.getName());

                        jsonBuilder.setData(subscriptionDTO);
                        success = true;

                        expert.sendNotificationEmail(email, confirmationKey,
                                newsletterCategory.getName(), true, request.getLocale());
                    }
                } else {
                    message = resourceBundle.getString("newsletter.subscription.data.error");
                }
            } else {
                message = resourceBundle.getString("newsletter.subscription.category.notchoosed");
            }
        } else {
            message = resourceBundle.getString("newsletter.subscription.data.error");
        }

        jsonBuilder.setSuccess(success);
        jsonBuilder.setMessage(message);

        JsonResponseUtil.write(response, jsonBuilder);

        return null;
    }

    @ResourceMapping(value = "confirmationPageAction")
    public ModelAndView confirmationPage(String email, String categoryId, ResourceRequest request, ResourceResponse response) {

        Map<String, String> model = new HashMap<String, String>();

        model.put("email", email);
        model.put("categoryId", categoryId);

        return new ModelAndView("/subscription/subscriptionConfirmation", model);
    }

    @ActionMapping(params = "action=confirmNewsletterSubscription")
    public ModelAndView confirmSubscriptionAction(@Valid SubscriptionForm subscriptionForm, BindingResult result, ActionRequest request, ActionResponse response) {
        JsonDTOBuilder jsonBuilder = new JsonDTOBuilder();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, request.getLocale());

        boolean success = false;
        String message = "";

        NewsletterCategory newsletterCategory = categoryService.findById(subscriptionForm.getCategoryId(), false);
        NewsletterSubscriptor newsletterSubscriptor = subscriptorService.findByEmail(subscriptionForm.getEmail());

        NewsletterSubscription oldNewsletterSubscription = subscriptionService.findBySubscriptorAndCategory(newsletterSubscriptor, newsletterCategory);
        String newConfirmationKey = subscriptionForm.getConfirmationKey();

        if (oldNewsletterSubscription != null
                && oldNewsletterSubscription.getConfirmationKey().equals(newConfirmationKey)) {

            success = true;
            oldNewsletterSubscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionService.updateNewsletterSubscription(oldNewsletterSubscription);

        } else {
            message = resourceBundle.getString("newsletter.subscription.confirmation.key.fail");
        }

        jsonBuilder.setSuccess(success);
        jsonBuilder.setMessage(message);
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setCategoryName(newsletterCategory.getName());
        subscriptionDTO.setEmail(subscriptionForm.getEmail());

        jsonBuilder.setData(subscriptionDTO);

        JsonResponseUtil.write(response, jsonBuilder);

        return null;
    }

    @ResourceMapping(value = "subscriptionSuccessAction")
    public ModelAndView successfullySubscriptionPage(String email, String categoryName, ResourceRequest request, ResourceResponse response) {

        Map<String, String> model = new HashMap<String, String>();

        model.put("email", email);
        model.put("categoryName", categoryName);

        return new ModelAndView("/subscription/subscriptionSuccess", model);
    }

    @ResourceMapping(value = "subscriptionFailureAction")
    public ModelAndView failureSubscriptionPage(String email, String categoryName, ResourceRequest request, ResourceResponse response) {

        Map<String, String> model = new HashMap<String, String>();

        model.put("email", email);
        model.put("categoryName", categoryName);

        return new ModelAndView("/subscription/subscriptionFailure", model);
    }

    @ResourceMapping(value = "unsubscriptionSuccessAction")
    public ModelAndView successfullyUnsubscriptionPage(String email, String categoryName, ResourceRequest request, ResourceResponse response) {

        Map<String, String> model = new HashMap<String, String>();

        model.put("email", email);
        model.put("categoryName", categoryName);

        return new ModelAndView("/subscription/unsubscriptionSuccess", model);
    }

    @ResourceMapping(value = "unsubscriptionFailureAction")
    public ModelAndView failureUnsubscriptionPage(String email, String categoryName, String message, ResourceRequest request, ResourceResponse response) {

        Map<String, String> model = new HashMap<String, String>();

        model.put("email", email);
        model.put("categoryName", categoryName);
        model.put("message", message);

        return new ModelAndView("/subscription/unsubscriptionFailure", model);
    }
}
