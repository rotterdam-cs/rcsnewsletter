package com.rcs.newsletter.portlets.newsletteradmin;

import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.model.dtos.NewsletterCategoryDTO;
import com.rcs.newsletter.core.model.dtos.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.forms.GridForm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class SubscriberController extends GenericController {
    
    @Autowired
    private NewsletterSubscriptorService subscriptorService;
    
    @Autowired
    private NewsletterCategoryService categoryService;    
    
    @ResourceMapping("subscribers")
    public ModelAndView subscribersTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        List<NewsletterCategoryDTO> lists = categoryService.findAllNewsletterCategories(Utils.getThemeDisplay(request));
        model.put("lists", lists);
        
        return new ModelAndView("admin/subscribers", model);
    }
    
    @ResourceMapping("getSubscribers")
    public ModelAndView getSubscribers(ResourceRequest request, GridForm gridParams, 
                                        String selectedList, 
                                        @RequestParam(defaultValue="false") boolean onlyInactive){
        
        SubscriptionStatus status = onlyInactive ? SubscriptionStatus.INACTIVE : SubscriptionStatus.ACTIVE;
        long listId = 0;
        if (StringUtils.hasText(selectedList)){
            try{
                listId = Long.parseLong(selectedList);
            }catch(NumberFormatException ex){}
        }
        
        ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> subscriptions = 
                subscriptorService.findAllByStatusAndCategory(Utils.getThemeDisplay(request), 
                                                    gridParams.calculateStart(), 
                                                    gridParams.getRows(), 
                                                    gridParams.getSidx(), 
                                                    gridParams.getSord(),
                                                    status,
                                                    listId);
        return jsonResponse(subscriptions);
    }
}
