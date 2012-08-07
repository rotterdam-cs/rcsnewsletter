package com.rcs.newsletter.portlets.newsletteradmin;

import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.model.dtos.NewsletterCategoryDTO;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.admin.CRUDActionEnum;
import com.rcs.newsletter.portlets.forms.GridForm;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
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
    
    @Autowired
    private NewsletterCategoryService categoryService;
    
    @RenderMapping
    public ModelAndView initialView(RenderRequest request, RenderResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/admin", model);
    }
    
    @ResourceMapping("getLists")
    public ModelAndView getLists(ResourceRequest request, @ModelAttribute GridForm gridParams){
        ServiceActionResult<ListResultsDTO<NewsletterCategoryDTO>> sarCategories = 
                categoryService.findAllNewsletterCategories(Utils.getThemeDisplay(request),
                                                            gridParams.calculateStart(),
                                                            gridParams.getRows());
        return jsonResponse(sarCategories);
    }
    
    @ResourceMapping("addEditDeleteList")
    public ModelAndView addEditDeleteList(ResourceRequest request, 
                                          String action,
                                          @RequestParam(defaultValue="0") long id,
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
        
        ServiceActionResult result = null;
        switch (enumAction){
            case CREATE:
                long companyId = Utils.getThemeDisplay(request).getCompanyId();
                long groupId = Utils.getThemeDisplay(request).getScopeGroupId();
                result = categoryService.createCategory(groupId, companyId, name, description, fromname, fromemail, adminemail);
                break;
                
            case UPDATE:
                result = categoryService.editCategory(id, name, description, fromname, fromemail, adminemail);
                break;
                
            case DELETE:
                result = categoryService.deleteCategory(id);
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
}
