package com.rcs.newsletter.portlets.newsletteradmin;

import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.NewsletterCategoryDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.admin.CRUDActionEnum;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class NewsletterAdminController extends GenericController {
    
    private Logger logger = Logger.getLogger(NewsletterAdminController.class);
    
    @Autowired
    private NewsletterCategoryService categoryService;
    
    @RenderMapping
    public ModelAndView initialView(RenderRequest request, RenderResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/admin", model);
    }
    
    @ResourceMapping("lists")
    public ModelAndView listsTab(){
        return new ModelAndView("admin/lists");
    }
    
    
    @ResourceMapping("getLists")
    public ModelAndView getLists(ResourceRequest request, @ModelAttribute GridForm gridForm){
        ServiceActionResult<ListResultsDTO<NewsletterCategoryDTO>> sarCategories = 
                categoryService.findAllNewsletterCategories(Utils.getThemeDisplay(request), gridForm);
        return jsonResponse(sarCategories);
    }
    
    @ResourceMapping("addEditDeleteList")
    public ModelAndView addEditDeleteList(ResourceRequest request, 
                                          String action,
                                          String id,
                                          String name,
                                          String description, 
                                          String fromname,
                                          String fromemail,
                                          String adminemail){
        
        CRUDActionEnum enumAction = null;
        try{
            enumAction = CRUDActionEnum.valueOf(action);
        }catch(IllegalArgumentException ex){
        }catch(NullPointerException ex){}

        if (enumAction == null){
            return null;
        }
        
        long categoryId = 0;
        try {
            categoryId = Long.parseLong(id);
        }catch(NumberFormatException ex){}
        
        ServiceActionResult result = null;
        switch (enumAction){
            case CREATE:
                long companyId = Utils.getThemeDisplay(request).getCompanyId();
                long groupId = Utils.getThemeDisplay(request).getScopeGroupId();
                result = categoryService.createCategory(groupId, companyId, name, description, fromname, fromemail, adminemail);
                break;
                
            case UPDATE:
                result = categoryService.editCategory(categoryId, name, description, fromname, fromemail, adminemail);
                break;
                
            case DELETE:
                result = categoryService.deleteCategory(categoryId);
                break;
        }
        
        if (result != null){
            return jsonResponse(result);
        }
        return null;
    }
    
    @ResourceMapping("getListData")
    public ModelAndView getListData(ResourceRequest request, @RequestParam long id){
        ServiceActionResult result = categoryService.getCategoryDTO(id);
        return jsonResponse(result);
    }
    
    @ResourceMapping("getCKEditor")
    public ModelAndView getCKEditor(ResourceRequest request, String listId, String type, ResourceResponse response){
        Map<String, Object> model = new HashMap<String, Object>();

        long categoryId = 0;
        try{
            categoryId = Long.parseLong(listId);
        }catch(NumberFormatException ex){}

        ServiceActionResult<NewsletterCategoryDTO> sarCategory = categoryService.getCategoryDTO(categoryId);
        if (!sarCategory.isSuccess()){
            model.put("currentContent", "");
            return new ModelAndView("admin/listsEditMails", model);
        }
        
        model.put("listId", categoryId);
        ResourceBundle newsletterBundle = ResourceBundle.getBundle("Newsletter", Utils.getCurrentLocale(request));
        String currentContent = null;
        if ("greeting".equalsIgnoreCase(type)){
            currentContent = sarCategory.getPayload().getGreetingEmail();
            model.put("title", newsletterBundle.getString("newsletter.admin.list.menu.greetingmail.edit"));
            model.put("type", type);
            model.put("helpContent", newsletterBundle.getString("newsletter.admin.category.greetingmail.info"));

        } else if ("subscribe".equalsIgnoreCase(type)){
            currentContent = sarCategory.getPayload().getSubscriptionEmail();
            model.put("title", newsletterBundle.getString("newsletter.admin.list.menu.subscribemail.edit"));
            model.put("type", type);
            model.put("helpContent", newsletterBundle.getString("newsletter.admin.category.subscriptionmail.info"));

        } else if ("unsubscribe".equalsIgnoreCase(type)){
            currentContent = sarCategory.getPayload().getUnsubscriptionEmail();
            model.put("title", newsletterBundle.getString("newsletter.admin.list.menu.unsubscribemail.edit"));
            model.put("type", type);
            model.put("helpContent", newsletterBundle.getString("newsletter.admin.category.unsubscriptionmail.info"));
        }

        if (currentContent == null){
            currentContent = "";
        }

        currentContent = currentContent.replace("\n", "")
                            .replace("\r", "")
                            .replace("\"", "\\\"");

        model.put("currentContent", currentContent);

        return new ModelAndView("admin/listsEditMails", model);
    }

    @ResourceMapping("saveEmail")
    public ModelAndView saveEmail(ResourceRequest request, ResourceResponse response, String listId, String type, String content){
        long categoryId = 0;
        try{
            categoryId = Long.parseLong(listId);
        }catch(NumberFormatException ex){}

        ServiceActionResult result = null;
        if ("greeting".equalsIgnoreCase(type)){
            result = categoryService.setCategoryGreetingEmailContent(categoryId, content);
        }else if ("subscribe".equalsIgnoreCase(type)){
            result = categoryService.setCategorySubscribeEmailContent(categoryId, content);
        }else if ("unsubscribe".equalsIgnoreCase(type)){
            result = categoryService.setCategoryUnsubscribeEmailContent(categoryId, content);
        }

        if (result == null){
            return null;
        }

        if (!result.isSuccess()){
            ResourceBundle newsletterBundle = ResourceBundle.getBundle("ServerMessages", Utils.getCurrentLocale(request));
            result.setMessages(new LinkedList<String>());
            result.addMessage(newsletterBundle.getString("newsletter.admin.subscriptionmail.notsaved"));
        }

        return jsonResponse(result);
    }
}
