package com.rcs.newsletter.portlets.newsletteradmin;

import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.CreateMultipleSubscriptionsResult;
import com.rcs.newsletter.core.dto.NewsletterCategoryDTO;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.admin.CRUDActionEnum;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.*;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
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
    private NewsletterSubscriptionService subscriptionService;
    
    @Autowired
    private NewsletterCategoryService categoryService;
    
    @Autowired
    private SubscriptorsResourceUtil subscriptorsResourceUtil;
    
    @ResourceMapping("subscribers")
    public ModelAndView subscribersTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        List<NewsletterCategoryDTO> lists = categoryService.findAllNewsletterCategories(Utils.getThemeDisplay(request));
        model.put("lists", lists);
        
        return new ModelAndView("admin/subscribers", model);
    }
    
    @ResourceMapping("getSubscribers")
    public ModelAndView getSubscribers(
    		 ResourceRequest request
    		,GridForm gridParams
    		,String selectedList
    		,@RequestParam(defaultValue="true") boolean onlyActive
    		//,@RequestParam(defaultValue="false") boolean onlyInactive
    		,String searchField
    		,String searchString
    	){
        
        //SubscriptionStatus status = onlyInactive ? SubscriptionStatus.INACTIVE : SubscriptionStatus.ACTIVE;
    	SubscriptionStatus status = null;
    	if (onlyActive) {
    		status = SubscriptionStatus.ACTIVE;
    	}
        long listId = 0;
        if (StringUtils.hasText(selectedList)){
            try{
                listId = Long.parseLong(selectedList);
            }catch(NumberFormatException ex){}
        }
        ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> subscriptions = null;
        if ( searchField != null && searchString != null && !searchField.isEmpty() && !searchString.isEmpty() ) {
        	subscriptions = subscriptorService.findAllByStatusAndCategoryAndCriteria(
        		 Utils.getThemeDisplay(request) 
                ,gridParams.calculateStart() 
                ,gridParams.getRows() 
                ,gridParams.getSidx() 
                ,gridParams.getSord()
                ,status
                ,listId
                ,searchField
                ,searchString  
            );
        } else {
        	subscriptions = subscriptorService.findAllByStatusAndCategory(
        		 Utils.getThemeDisplay(request) 
                ,gridParams.calculateStart() 
                ,gridParams.getRows() 
                ,gridParams.getSidx() 
                ,gridParams.getSord()
                ,status
                ,listId
            );
        }
        return jsonResponse(subscriptions);
    }
    
    @ResourceMapping("getSubscriptorData")
    public ModelAndView getSubscriptorData(ResourceRequest request, long subscriptorId){
        ServiceActionResult<List<NewsletterSubscriptionDTO>> result = subscriptionService.findSubscriptionsBySubscriptorId(subscriptorId);
        return jsonResponse(result);
    }
    
    @ResourceMapping("editDeleteSubscriptor")
    public ModelAndView editDeleteSubscriptor(String action,
                                              String subscriptorId,
                                              String firstName, String lastName, String email){
        CRUDActionEnum enumAction = null;
        try{
            enumAction = CRUDActionEnum.valueOf(action);
        }catch(IllegalArgumentException ex){
        }catch(NullPointerException ex){}
        
        long id = 0;
        try {
            id = Long.parseLong(subscriptorId);
        }catch(NumberFormatException ex){}
        
        ServiceActionResult result = null;
        switch (enumAction){
            case UPDATE:
                result = subscriptorService.updateSubscriptor(id, firstName, lastName, email);
                break;
                
            case DELETE:
                result = subscriptorService.deleteSubscriptor(id);
                break;
        }
        
        if (result == null){
            return null;
        }
        return jsonResponse(result);
    }

    @ActionMapping(params = "action=importSubscribers")
    public void importSubscribers(ActionRequest request, ActionResponse response, 
                                    @RequestParam(defaultValue="0") String list, 
                                    @RequestParam("file") MultipartFile file) throws IOException{
        
        long listId = 0;
        try {
            listId = Long.parseLong(list);
        }catch(NumberFormatException ex){}
        
        ObjectMapper mapper = new ObjectMapper();
        ResourceBundle newsletterBundle = ResourceBundle.getBundle("Newsletter", Utils.getCurrentLocale(request));
        try {
            if (!file.isEmpty()){
                CreateMultipleSubscriptionsResult result = subscriptorsResourceUtil.importSubscriptorsFromExcel(file.getInputStream(), listId, Utils.getThemeDisplay(request));
                if (result.isSuccess()){
                    
                }
                
                String resultText = newsletterBundle.getString("newsletter.admin.subscribers.import.success") + "<br/>" +
                    String.format(newsletterBundle.getString("newsletter.admin.subscribers.import.result"), result.getRowsProcessed(), result.getRowsOmitted(), result.getSubscriptionsCreated());

                String json = mapper.writeValueAsString(new UploadResult(resultText, true));
                write(response, json);
                return;
            }
            write(response,  mapper.writeValueAsString(new UploadResult(newsletterBundle.getString("newsletter.admin.subscribers.import.unsuccess"), false)));
        }catch(Exception ex){
            write(response,  mapper.writeValueAsString(new UploadResult(newsletterBundle.getString("newsletter.admin.subscribers.import.unsuccess"), false)));
        }
    }

    private void write(PortletResponse response, String json) {
        // Get the HttpServletResponse
        HttpServletResponse servletResponse = ((LiferayPortletResponse) response).getHttpServletResponse();

        // Sets the JSON Content Type
        servletResponse.setContentType("application/json");
        try {
            ServletResponseUtil.write(servletResponse, json);
        } catch (IOException ex) {
            logger.error("Error writing JSON response");
        }
    }
    
    @ResourceMapping("exportSubscribers")
    public void exportSubscribers(ResourceRequest request, ResourceResponse response, String categoryId){
        long catId = 0;
        try{
            catId = Long.parseLong(categoryId);
        }catch(NumberFormatException ex){}
        
        subscriptorsResourceUtil.writeSubscriptorsExcel(request, catId, Utils.getThemeDisplay(request), response);
    }

    class UploadResult implements Serializable {
        private String errors;
        private boolean success;

        public UploadResult() {
        }

        public UploadResult(String errors, boolean success) {
            this.errors = errors;
            this.success = success;
        }

        public String getErrors() {
            return errors;
        }

        public void setErrors(String errors) {
            this.errors = errors;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
